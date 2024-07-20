package ag.act.core.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component("s3Environment")
public class S3Environment {
    private final String baseUrl;
    private final String publicBucketName;
    private final String privateBucketName;

    public S3Environment(
        @Value("${aws.s3.public.bucket.url}") String baseUrl,
        @Value("${aws.s3.public.bucket.files}") String publicBucketName,
        @Value("${aws.s3.private.bucket.files}") String privateBucketName
    ) {
        this.baseUrl = baseUrl;
        this.publicBucketName = publicBucketName;
        this.privateBucketName = privateBucketName;
    }

    public String getBaseUrlWithTailingSlash() {
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
