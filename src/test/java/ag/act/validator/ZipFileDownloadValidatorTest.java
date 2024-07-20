package ag.act.validator;

import ag.act.entity.ZipFileDownload;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class ZipFileDownloadValidatorTest {

    @InjectMocks
    private ZipFileDownloadValidator validator;

    @Nested
    class Validate {
        @Mock
        private ZipFileDownload zipFileDownload;

        @BeforeEach
        void setUp() {
            given(zipFileDownload.getZipFileStatus()).willReturn(ZipFileStatus.COMPLETE);
            given(zipFileDownload.getIsLatest()).willReturn(true);
        }

        @Nested
        class WhenTheZipFileDownloadIsNull {
            @BeforeEach
            void setUp() {
                zipFileDownload = null;
            }

            @Test
            void shouldThrowABadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(zipFileDownload),
                    "ZIP 파일이 준비되지 않았습니다."
                );
            }
        }

        @Nested
        class WhenZipFileStatusIsNotComplete {
            @BeforeEach
            void setUp() {
                given(zipFileDownload.getZipFileStatus())
                    .willReturn(someThing(ZipFileStatus.IN_PROGRESS, ZipFileStatus.REQUEST));
            }

            @Test
            void shouldThrowABadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(zipFileDownload),
                    "ZIP 파일이 준비되지 않았습니다."
                );
            }
        }

        @Nested
        class WhenZipFileDownloadIsNotLatest {
            @BeforeEach
            void setUp() {
                given(zipFileDownload.getIsLatest()).willReturn(false);
            }

            @Test
            void shouldThrowABadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(zipFileDownload),
                    "ZIP 파일을 더 이상 다운로드 할 수 없습니다."
                );
            }
        }
    }
}
