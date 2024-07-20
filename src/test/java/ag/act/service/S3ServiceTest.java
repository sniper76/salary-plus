package ag.act.service;

import ag.act.core.infra.S3Environment;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.enums.FileType;
import ag.act.service.aws.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;
    @Mock
    private S3Client s3Client;
    @Mock
    private S3Environment s3Environment;
    @Mock
    private FileType fileType;

    @BeforeEach
    void setUp() {
        final String publicBucketName = someString(5);
        given(s3Environment.getPublicBucketName()).willReturn(publicBucketName);
    }

    @Nested
    class PutObject {

        @Mock
        private UploadFilePathDto uploadFilePathDto;
        @Mock
        private InputStream inputStream;
        @Mock
        private PutObjectResponse response;

        @BeforeEach
        void setUp() {
            final String uploadPath = someString(5);
            final String mimeType = someString(5);
            final Long fileSize = somePositiveLong();
            final String s3Tag = someString(5);

            given(uploadFilePathDto.getFileType()).willReturn(fileType);
            given(uploadFilePathDto.getUploadPath()).willReturn(uploadPath);
            given(uploadFilePathDto.getMimeType()).willReturn(mimeType);
            given(uploadFilePathDto.getFileSize()).willReturn(fileSize);
            given(fileType.getIsPublic()).willReturn(true);
            given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).willReturn(response);
            given(response.eTag()).willReturn(s3Tag);
        }

        @Test
        void shouldUploadFileToS3() {
            // when
            final boolean actual = s3Service.putObject(uploadFilePathDto, inputStream);

            // then
            assertThat(actual, is(true));
        }
    }

    @Nested
    class RemoveObject {

        @Mock
        private DeleteObjectResponse deleteObjectResponse;

        @BeforeEach
        void setUp() {
            final String privateBucketName = someString(5);
            given(fileType.getIsPublic()).willReturn(false);
            given(s3Environment.getPrivateBucketName()).willReturn(privateBucketName);
            given(s3Client.deleteObject((DeleteObjectRequest) any())).willReturn(deleteObjectResponse);
        }

        @Test
        void shouldDeleteObject() {

            // When
            s3Service.removeObject(fileType, someString(5));

            // Then
            then(s3Client).should().deleteObject((DeleteObjectRequest) any());
        }
    }

    @Nested
    class ReadObject {

        @Mock
        private ResponseInputStream<GetObjectResponse> inputStream;

        @BeforeEach
        void setUp() {
            given(fileType.getIsPublic()).willReturn(false);
            given(s3Client.getObject((GetObjectRequest) any())).willReturn(inputStream);
        }

        @Test
        void shouldDeleteObject() {

            // Given
            final String fileKey = someString(5);

            // When
            final InputStream actualInputStream = s3Service.readObject(fileKey);

            // Then
            assertThat(actualInputStream, is(inputStream));
        }
    }
}
