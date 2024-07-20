package ag.act.service;

import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.FileContent;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.service.aws.S3Service;
import ag.act.service.io.FileContentService;
import ag.act.service.io.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UploadServiceTest {

    @InjectMocks
    private UploadService service;

    @Mock
    private S3Service s3Service;
    @Mock
    private FileContentService fileContentService;
    @Mock
    private MultipartFile file;
    @Mock
    private InputStream inputStream;
    @Mock
    private FileContent fileContent;

    @Nested
    class WhenUploadFile {

        private FileContentType fileContentType;
        private FileType fileType;
        private String description;

        @BeforeEach
        void setUp() throws IOException {
            fileContentType = someEnum(FileContentType.class);
            fileType = someEnum(FileType.class);
            description = someString(5);

            given(file.getInputStream()).willReturn(inputStream);
            given(s3Service.putObject(any(UploadFilePathDto.class), eq(inputStream))).willReturn(true);
            given(fileContentService.saveFileContent(
                any(UploadFilePathDto.class),
                eq(fileContentType),
                eq(fileType),
                eq(description),
                eq(ag.act.model.Status.ACTIVE)
            )).willReturn(fileContent);
        }

        @Test
        void shouldUploadFileAndCreateFileContent() {

            // When
            final Optional<FileContent> actual = service.uploadFile(file, fileContentType, fileType, description);

            // Then
            assertThat(actual.isPresent(), is(true));
            assertThat(actual.get(), is(fileContent));
            then(s3Service).should().putObject(any(UploadFilePathDto.class), eq(inputStream));
            then(fileContentService).should().saveFileContent(
                any(UploadFilePathDto.class),
                eq(fileContentType),
                eq(fileType),
                eq(description),
                eq(ag.act.model.Status.ACTIVE)
            );
        }
    }
}
