package ag.act.service.aws;

import ag.act.core.infra.S3Environment;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.enums.FileType;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("s3Service")
public class S3Service {

    private static final int REMOVE_OBJECT_MAX_RETRY_COUNT = 2;
    private final S3Client s3Client;
    private final S3Environment s3Environment;

    @Autowired
    public S3Service(S3Client s3Client, S3Environment s3Environment) {
        this.s3Client = s3Client;
        this.s3Environment = s3Environment;
    }

    public boolean putObject(UploadFilePathDto uploadFilePathDto, InputStream inputStream) {

        final String bucketName = getBucketName(uploadFilePathDto.getFileType().getIsPublic());
        try {
            final PutObjectRequest request = createPutObjectRequest(uploadFilePathDto, bucketName);
            final RequestBody requestBody = RequestBody.fromInputStream(inputStream, uploadFilePathDto.getFileSize());

            final PutObjectResponse response = s3Client.putObject(request, requestBody);
            return response.eTag() != null;
        } catch (S3Exception e) {
            log.error("Can't create a file in S3 bucket({})", bucketName, e);
            throw new InternalServerException("파일을 저장하는 중에 오류가 발생하였습니다.", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close input stream: {}", e.getMessage(), e);
            }
        }
    }

    public boolean putObject(UploadFilePathDto uploadFilePathDto, String content) {

        final String bucketName = getBucketName(uploadFilePathDto.getFileType().getIsPublic());
        try {
            final PutObjectRequest request = createPutObjectRequest(uploadFilePathDto, bucketName);
            final RequestBody requestBody = RequestBody.fromString(content);

            final PutObjectResponse response = s3Client.putObject(request, requestBody);
            return response.eTag() != null;
        } catch (S3Exception e) {
            log.error("Can't create a file in S3 bucket({})", bucketName, e);
            throw new InternalServerException("파일을 저장하는 중에 오류가 발생하였습니다.", e);
        }
    }

    private PutObjectRequest createPutObjectRequest(UploadFilePathDto uploadFilePathDto, String bucketName) {
        return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(uploadFilePathDto.getUploadPath())
            .contentType(uploadFilePathDto.getMimeType())
            .build();
    }

    public void removeObject(FileType fileType, String filePath) {
        final String bucketName = getBucketName(fileType.getIsPublic());

        final DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void removeObject(String fileKey) {

        final DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(s3Environment.getPrivateBucketName())
            .key(fileKey)
            .build();

        s3Client.deleteObject(deleteRequest);
    }

    @Retryable(maxAttempts = REMOVE_OBJECT_MAX_RETRY_COUNT, backoff = @Backoff(delay = 1000))
    public boolean removeObjectInRetry(String fileKey) {
        removeObject(fileKey);
        return true;
    }

    public InputStream readObject(String fileKey) {
        return readObject(fileKey, s3Environment.getPrivateBucketName());
    }

    private InputStream readObject(String fileKey, String bucketName) {
        try {
            return s3Client.getObject(getGetObjectRequest(fileKey, bucketName));
        } catch (SdkException e) {
            log.error("{}\n - bucketName : [{}], fileKey : [{}]", e.getMessage(), bucketName, fileKey, e);
            throw new NotFoundException("파일을 찾을 수 없습니다.", e);
        }
    }

    public Optional<InputStream> findObjectFromPrivateBucket(String fileKey) {
        return findObject(fileKey, s3Environment.getPrivateBucketName());
    }

    public Optional<InputStream> findObjectFromPublicBucket(String fileKey) {
        return findObject(fileKey, s3Environment.getPublicBucketName());
    }

    private Optional<InputStream> findObject(String fileKey, String bucketName) {
        try {
            return Optional.of(readObject(fileKey, bucketName));
        } catch (Exception e) {
            log.warn("fileKey : [{}], bucketName : [{}]", fileKey, bucketName, e);
            return Optional.empty();
        }
    }

    public void deleteDirectoryInFiles(String prefix) {
        ListObjectsRequest listObjects = ListObjectsRequest
            .builder()
            .bucket(s3Environment.getPrivateBucketName())
            .prefix(prefix)
            .build();

        ListObjectsResponse res = s3Client.listObjects(listObjects);
        List<S3Object> objects = res.contents();
        for (S3Object s3File : objects) {
            try {
                removeObject(s3File.key());
            } catch (S3Exception e) {
                log.error("deleteDirectoryInFiles fileKey : [{}]", s3File.key(), e);
            }
        }
    }

    private GetObjectRequest getGetObjectRequest(String fileKey, String bucketName) {
        return GetObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .build();
    }

    private String getBucketName(boolean isPublic) {
        return isPublic ? s3Environment.getPublicBucketName() : s3Environment.getPrivateBucketName();
    }

    public boolean isActImage(String imageUrl) {
        if (imageUrl == null) {
            return false;
        }

        return imageUrl.startsWith(s3Environment.getBaseUrl());
    }

    public String getBaseUrl() {
        return s3Environment.getBaseUrl();
    }

    public String getBaseUrlWithTailingSlash() {
        return s3Environment.getBaseUrlWithTailingSlash();
    }

}
