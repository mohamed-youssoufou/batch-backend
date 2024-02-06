package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.repositories.ProductRepository;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.hibernate.grammars.hql.HqlParser;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


@SpringBatchTest
@SpringJUnitConfig(classes = { ProductBatchTestConfig.class, ProductBatchConfig.class })
class ProductBatchIntegrationTest {

    private final String WRONG_INPUT = "src/test/resources/batch/wrong-input-file.csv";
    private final String GOOD_INPUT = "src/test/resources/batch/CLT-129-01012018.csv";
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired @Qualifier("productJob") Job  job;


    private JobParameters parameters(String path) {
        val paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("fullname", WRONG_INPUT);
        return paramsBuilder.toJobParameters();
    }

    @Test
    void should_job_must_run_with_wrong_input_file_and_correct_payload() throws Exception {
        jobLauncherTestUtils.setJob(job);
        val jobExecution = jobLauncherTestUtils.launchJob(parameters(WRONG_INPUT));
        Assertions.assertThat(ExitStatus.COMPLETED.getExitCode().equals(jobExecution.getStatus().COMPLETED));
        Assertions.assertThat(jobExecution.getJobParameters().getParameter("fullname").equals(WRONG_INPUT));
        Assertions.assertThat(jobExecution.getJobInstance().getJobName().equals("productJob"));
    }
}