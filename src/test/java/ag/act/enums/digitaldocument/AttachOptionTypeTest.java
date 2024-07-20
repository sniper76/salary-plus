package ag.act.enums.digitaldocument;

import ag.act.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static ag.act.TestUtil.assertException;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AttachOptionTypeTest {

    @BeforeEach
    void setUp() {

    }

    @Nested
    class AttachOptionTypeNone {

        private List<MultipartFile> images;
        private final String fileType = someString(5);

        @Nested
        class WhenImagesAreEmpty {

            @BeforeEach
            void setUp() {
                images = List.of();
            }

            @Test
            void shouldNotThrowException() {
                AttachOptionType.NONE.validate(images, fileType);
            }
        }

        @Nested
        class WhenImagesHaveSomeNullValues {

            @BeforeEach
            void setUp() {
                images = Arrays.asList(null, null);
            }

            @Test
            void shouldNotThrowException() {
                AttachOptionType.NONE.validate(images, fileType);
            }
        }

        @Nested
        class WhenImagesHaveSomeValues {

            @Mock
            private MultipartFile multipartFile;

            @BeforeEach
            void setUp() {
                images = Arrays.asList(null, multipartFile, null);
            }

            @Test
            void shouldThrowBadRequestException() {

                assertException(
                    BadRequestException.class,
                    () -> AttachOptionType.NONE.validate(images, fileType),
                    "%s는 필수 파일이 아닙니다.".formatted(fileType)
                );
            }
        }
    }
}