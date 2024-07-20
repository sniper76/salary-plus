package ag.act.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsConfiguration {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .build();
    }

    @Bean
    public LambdaAsyncClient lambdaAsyncClient() {
        return LambdaAsyncClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .build();
    }

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .build();
    }

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .build();
    }
}
