package ag.act.service;

import ag.act.converter.image.S3PathProvider;
import ag.act.entity.FileContent;
import ag.act.exception.ImageProcessingException;
import ag.act.service.aws.S3Service;
import ag.act.service.image.ImageResizeResult;
import ag.act.service.image.ImageResizer;
import ag.act.service.image.S3PublicImageHandler;
import ag.act.service.image.ThumbnailImagePathProvider;
import ag.act.service.image.ThumbnailImageService;
import ag.act.service.io.FileContentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"AbbreviationAsWordInName", "checkstyle:MemberName"})
@MockitoSettings(strictness = Strictness.LENIENT)
class ThumbnailImageServiceTest {
    @InjectMocks
    private ThumbnailImageService service;

    @Mock
    private S3Service s3Service;
    @Mock
    private S3PathProvider s3PathProvider;
    @Mock
    private ImageResizer imageResizer;
    @Mock
    private S3PublicImageHandler s3PublicImageHandler;
    @Mock
    private FileContentService fileContentService;
    @Mock
    private ThumbnailImagePathProvider thumbnailImagePathProvider;

    @Mock
    private FileContent fileContent;
    @Mock
    private BufferedImage bufferedImage;
    @Mock
    private ImageResizeResult imageResizeResult;
    private String content;
    private List<Long> imageIds;
    private String thumbnailImageUrl;

    @Nested
    class WhenGetThumbnailImageUrl {
        @BeforeEach
        void setUp() throws ImageProcessingException {
            content = someString(10);
            final String baseUrl = someString(10);
            thumbnailImageUrl = "%s/%s".formatted(baseUrl, someString(30));

            given(s3Service.getBaseUrl()).willReturn(baseUrl);
            given(s3Service.getBaseUrlWithTailingSlash()).willReturn(baseUrl + "/");
            given(s3Service.isActImage(anyString())).willReturn(true);

            given(s3PublicImageHandler.uploadImage(anyString(), anyString(), any())).willReturn(Optional.ofNullable(thumbnailImageUrl));
            given(s3PublicImageHandler.findImage(anyString())).willReturn(Optional.of(bufferedImage));

            given(imageResizer.isResizeNeeded(any(BufferedImage.class))).willReturn(Boolean.TRUE);
            given(imageResizer.resize(eq(bufferedImage))).willReturn(Optional.of(imageResizeResult));
            given(imageResizeResult.toByteArray()).willReturn(new byte[0]);
            given(imageResizeResult.extension()).willReturn(someString(3));

            given(thumbnailImagePathProvider.getThumbnailImageUrl(anyString())).willReturn(thumbnailImageUrl);
        }

        @Nested
        @DisplayName("이미지 아이디가 있는 경우, 썸네일이미지 URL 반환")
        class WhenImageIdsNotEmpty {
            @BeforeEach
            void setUp() {
                imageIds = List.of(someLong(), someLong());

                given(fileContentService.findById(anyLong())).willReturn(Optional.of(fileContent));
                given(s3PathProvider.getFullPath(fileContent)).willReturn(thumbnailImageUrl);
            }

            @Test
            void shouldReturnThumbnailImageUrl() {
                final String resultThumbnailImageUrl = service.generate(imageIds, content);

                assertThat(resultThumbnailImageUrl, is(thumbnailImageUrl));
            }
        }

        @Nested
        @DisplayName("이미지 아이디가 없고, 이미지를 포함한 Html content가 있는 경우, 썸네일이미지 URL 반환")
        class WhenImagesInHtmlContent {

            private static MockedStatic<Pattern> patterns;
            @Mock
            private Pattern pattern;
            @Mock
            private Matcher matcher;

            @BeforeEach
            void setUp() {
                imageIds = List.of();
                patterns = mockStatic(Pattern.class);

                given(Pattern.compile(anyString())).willReturn(pattern);
                given(pattern.matcher(content)).willReturn(matcher);
                given(matcher.find()).willReturn(true);
                given(matcher.group(1)).willReturn(thumbnailImageUrl);
            }

            @AfterEach
            void tearDown() {
                patterns.close();
            }

            @Test
            void shouldReturnThumbnailImageUrl() {
                final String resultThumbnailImageUrl = service.generate(imageIds, content);

                assertThat(resultThumbnailImageUrl, is(thumbnailImageUrl));
            }
        }

        @Nested
        @DisplayName("이미지 아이디도, 이미지 html 내용도 없는 경우, null 반환")
        class WhenNoImages {
            private List<Long> imageIds;

            @BeforeEach
            void setUp() {
                imageIds = List.of();

                given(fileContentService.findById(anyLong())).willReturn(Optional.of(fileContent));
                given(s3PathProvider.getFullPath(fileContent)).willReturn(null);
            }

            @Test
            void shouldReturnNull() {
                final String resultThumbnailImageUrl = service.generate(imageIds, content);

                assertThat(resultThumbnailImageUrl, is(nullValue()));
            }
        }
    }
}
