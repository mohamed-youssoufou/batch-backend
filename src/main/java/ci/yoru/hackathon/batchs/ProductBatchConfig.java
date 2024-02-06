package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.entities.Product;
import ci.yoru.hackathon.repositories.ProductRepository;
import ci.yoru.hackathon.utils.FilesUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ProductBatchConfig {
    private final int chunckSize = 100;

    private final ProductRepository productRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<Product> itemReader(
            @Value("#{jobParameters['" + FilesUtils.productDepositFileName + "']}") final String fileWithFullPath,
            final ProductMapper productMapper) {

        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        val fileSystemResource = new FileSystemResource(new File(fileWithFullPath));
        reader.setResource(fileSystemResource);
        reader.setLinesToSkip(1);
        reader.setLineMapper(flatFileMapper(productMapper));
        return reader;
    }

    @Bean
    public DefaultLineMapper<Product> flatFileMapper(final ProductMapper productMapper){
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        val headers = new String[]{"ref_client", "client_name", "address", "email", "productRef", "productName", "productQuantity", "productPrice"};
        tokenizer.setNames(headers);
        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(productMapper);
        return lineMapper;
    }

    @Bean
    @StepScope
    public ItemProcessor<Product, Product> processor() {
        return chunk -> chunk;
    }

    @Bean
    @Transactional(rollbackFor = {RuntimeException.class})
    public ItemWriter<Product> writer() {
        return productRepository::saveAll;
    }

    @Bean
    protected Step productStep(final JobRepository jobRepository, final PlatformTransactionManager platformTransactionManager, ProductMapper productMapper) throws Exception {
        return new StepBuilder("create_product", jobRepository)
                .<Product, Product>chunk(chunckSize, platformTransactionManager)
                .reader(itemReader(null, productMapper))
                .listener(new ProductReaderListener())
                .processor(processor())
                .listener(new ProductProccessorListener())
                .writer(writer())
                .listener(new ProductWritterListener())
                .faultTolerant()
                .build();
    }

    @Bean(name = "productJob")
    public Job job(final Step productStep, final JobRepository jobRepository) {
        return new JobBuilder("productJob", jobRepository)
                .preventRestart()
                .flow(productStep)
                .end()
                .build();
    }
}
