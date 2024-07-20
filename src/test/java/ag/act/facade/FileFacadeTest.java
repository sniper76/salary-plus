package ag.act.facade;

import ag.act.converter.image.DefaultImageResponseConverter;
import ag.act.converter.image.ImageResponseConverter;
import ag.act.entity.FileContent;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.exception.InternalServerException;
import ag.act.model.DefaultProfileImagesResponse;
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

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class FileFacadeTest {
    @InjectMocks
    private FileFacade facade;

    @Mock
    private UploadService uploadService;
    @Mock
    private S3Service s3Service;
    @Mock
    private FileContentService fileContentService;
    @Mock
    private ImageResponseConverter imageResponseConverter;
    @Mock
    private DefaultImageResponseConverter defaultImageResponseConverter;
    @Mock
    private MultipartFile file;
    @Mock
    private ag.act.model.FileContentResponse fileContentResponse;
    @Mock
    private ag.act.model.ImageUploadResponse imageUploadResponse;

    @Nested
    class WhenUploadImage {

        private FileContentType fileContentType;
        private FileType fileType;
        private String description;
        @Mock
        private FileContent fileContent;

        @BeforeEach
        void setUp() {
            fileContentType = someEnum(FileContentType.class);
            fileType = someEnum(FileType.class);
            description = someString(5);

            given(imageResponseConverter.convert(fileContent)).willReturn(fileContentResponse);
            given(imageResponseConverter.convert(fileContentResponse)).willReturn(imageUploadResponse);
        }

        @Nested
        class AndSuccess {

            @Test
            void shouldUploadFileAndCreateFileContent() {
                // Given
                given(uploadService.uploadFile(file, fileContentType, fileType, description))
                    .willReturn(Optional.of(fileContent));

                // When
                final ag.act.model.ImageUploadResponse actual = facade.uploadImage(file, fileContentType, fileType, description);

                // Then
                assertThat(actual, is(imageUploadResponse));
                then(uploadService).should().uploadFile(file, fileContentType, fileType, description);
                then(imageResponseConverter).should().convert(fileContent);
            }
        }

        @Nested
        class AndCanNotCreateFileContent {
            @Test
            void shouldThrowInternalServerException() {
                // Given
                given(uploadService.uploadFile(file, fileContentType, fileType, description))
                    .willReturn(Optional.empty());

                // When
                assertException(
                    InternalServerException.class,
                    () -> facade.uploadImage(file, fileContentType, fileType, description),
                    "이미지를 저장하는 중에 오류가 발생하였습니다."
                );

                // Then
                then(uploadService).should().uploadFile(file, fileContentType, fileType, description);
                then(imageResponseConverter).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    class GetDefaultProfileImages {

        @Mock
        private ag.act.model.DefaultProfileImageResponse defaultProfileImageResponse;
        @Mock
        private ag.act.model.DefaultProfileImagesResponse defaultProfileImagesResponse;
        @Mock
        private FileContent fileContent;

        @BeforeEach
        void setUp() {
            final List<FileContent> images = List.of(fileContent);

            given(fileContentService.getDefaultProfileImages()).willReturn(images);
            given(defaultImageResponseConverter.convert(fileContent)).willReturn(defaultProfileImageResponse);
            given(defaultImageResponseConverter.convert(List.of(defaultProfileImageResponse)))
                .willReturn(defaultProfileImagesResponse);
        }

        @Test
        void shouldGetDefaultProfileImages() {

            // Given

            // When
            final DefaultProfileImagesResponse actual = facade.getDefaultProfileImages();

            // Then
            assertThat(actual, is(defaultProfileImagesResponse));
        }
    }

    @Nested
    class DeleteFile {
        @Mock
        private FileContent deletedFileContent;

        @Test
        void shouldDeleteFileInS3() {

            // Given
            final String filename = someString(5);
            final FileType fileType = someEnum(FileType.class);
            given(deletedFileContent.getFilename()).willReturn(filename);
            given(deletedFileContent.getFileType()).willReturn(fileType);
            willDoNothing().given(s3Service).removeObject(fileType, filename);

            // When
            facade.deleteFile(deletedFileContent);

            // Then
            then(s3Service).should().removeObject(fileType, filename);
        }
    }
}
