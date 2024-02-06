package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.repositories.BatchRepository;
import ci.yoru.hackathon.utils.FilesUtils;
import lombok.extern.java.Log;
import lombok.val;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Log
@EnableScheduling
public class ProductBatchController {
    private final JobLauncher jobLauncher;
    private final Job jobProduct;
    private final String depositAbsoluteFullname;
    private final String destinationAbsoluteFullname;
    private final BatchRepository batchRepository;

    ProductBatchController(
            @Qualifier("productJob") final Job jobProduct,
            final JobLauncher jobLauncher,
            final BatchRepository batchRepository,
            @Value("${deposit_path}") final String depositAbsoluteFullname,
            @Value("${destination_path}") final String destinationAbsoluteFullname
    ){
        this.jobLauncher = jobLauncher;
        this.jobProduct = jobProduct;
        this.depositAbsoluteFullname = depositAbsoluteFullname;
        this.destinationAbsoluteFullname = destinationAbsoluteFullname;
        this.batchRepository = batchRepository;
    }

    private File[] getUploadFiles(final String depositFullPathName) {
        val depotPath = Paths.get(depositFullPathName);
        if (!depotPath.toFile().exists()) {
            log.info(String.format("Folder <<%s>> does not exist", depositFullPathName));
            return new File[]{};
        }
        val fileFolder = new File(depositFullPathName);
        val files = fileFolder.listFiles();
        if (files.length == 0) {
            log.info("folder is empty");
            return new File[]{};
        }
        return files;
    }

    private void runJobOfEachFileUploaded(final File currentFile) {
        var destinationPath = Paths.get(destinationAbsoluteFullname + currentFile.getName());
        try {
            val expectedFilename = batchRepository.getFilenameByName(currentFile.getName());
            if (FilesUtils.regex(expectedFilename.getOrDefault("PARAMETER_VALUE", "DEFAULT").toString())) {
                log.info("file find are wrong filename");
                return;
            }
            JobExecution jobExecution = jobLauncher.run(
                    jobProduct,
                    new JobParametersBuilder()
                            .addString(
                                    FilesUtils.productDepositFileName,
                                    currentFile.getAbsolutePath())
                            .toJobParameters()
            );
            if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
                val jobparamter = jobExecution.getJobParameters().getParameter(FilesUtils.productDepositFileName);
                val currentPath = Paths.get(jobparamter.getValue().toString());
                destinationPath = Paths.get(destinationAbsoluteFullname + currentPath.getFileName());
                Files.move(currentPath, destinationPath);
                return;
            }
        } catch (IOException exception) {
            log.warning("I/O error occured : transfer aborted");
            log.warning(exception.getLocalizedMessage());
        } catch (JobExecutionAlreadyRunningException exception) {
            log.warning("Job exectution aborted: it already running");
            log.warning(exception.getLocalizedMessage());
        } catch (JobInstanceAlreadyCompleteException | JobRestartException exception) {
            log.warning("Job exectution aborted: it already completed");
            log.warning(exception.getLocalizedMessage());
        } catch (JobParametersInvalidException exception) {
            log.warning("Job exectution aborted: invalid parameters");
            log.warning(exception.getLocalizedMessage());
        }


        try {
            Files.move(currentFile.toPath(), destinationPath);
        } catch (IOException e) {
            log.warning("I/O error occured : transfer aborted");
            log.warning(e.getLocalizedMessage());
        }
    }

    @Scheduled(fixedRate = 50000)
    public void serviceWatcher() {
        val files = getUploadFiles(depositAbsoluteFullname);
        for (val file : files) {
            runJobOfEachFileUploaded(file);
        }
    }

}
