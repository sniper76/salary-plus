package ag.act.util;

import ag.act.module.digitaldocumentgenerator.dto.DigitalDocumentFilenameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class FilenameUtilTest {
    private DigitalDocumentFilenameDto dto;

    @BeforeEach
    void setUp() {
        dto = DigitalDocumentFilenameDto.builder()
            .stockName("종 목 이 름 S t o c k n a  me")
            .postTitle("게 시 글 제 목  ~!@#$%^&*-+")
            .userName("사 용 자 이  름")
            .userBirthDate(
                DateTimeUtil.parseLocalDateTime(
                    "1998-08-13 00:00:00", "yyyy-MM-dd HH:mm:ss"
                )
            )
            .build();
    }

    @Nested
    class WhenGetDigitalDocumentFileName {
        @Test
        void shouldGetDigitalDocumentFilename() {
            // When
            String actual = FilenameUtil.getDigitalDocumentFilename(dto);

            // Then
            assertThat(actual, startsWith("종목이름Stockname_게시글제목_사용자이름(980813)_"));
            assertThat(actual, endsWith(".pdf"));
        }
    }

    @Nested
    class WhenGetDigitalDocumentCertificationFileName {

        @Test
        void shouldGetDigitalDocumentCertificationFilename() {
            // When
            String actual = FilenameUtil.getDigitalDocumentCertificationFilename(dto);

            // Then
            assertThat(actual, startsWith("종목이름Stockname_게시글제목_사용자이름(980813)_전자문서인증서"));
            assertThat(actual, endsWith(".pdf"));
        }
    }
}
