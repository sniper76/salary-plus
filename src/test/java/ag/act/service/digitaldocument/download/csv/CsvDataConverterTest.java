package ag.act.service.digitaldocument.download.csv;

import ag.act.converter.DecryptColumnConverter;
import ag.act.converter.csv.CsvDataConverter;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CsvDataConverterTest {
    @InjectMocks
    private CsvDataConverter converter;
    private List<MockedStatic<?>> statics;

    @Mock
    private DecryptColumnConverter decryptColumnConverter;
    @Mock
    private DigitalDocumentUser digitalDocumentUser;


    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class), mockStatic(KoreanDateTimeUtil.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class GetUpdatedAtInKoreanTime {

        private String expectedResult;
        private String actualResult;

        @BeforeEach
        void setUp() {
            final LocalDateTime updatedAt = LocalDateTime.now();
            final ZonedDateTime koreanTime = ZonedDateTime.now();
            expectedResult = someString(5);

            given(digitalDocumentUser.getUpdatedAt()).willReturn(updatedAt);
            given(KoreanDateTimeUtil.toKoreanTime(updatedAt)).willReturn(koreanTime);
            given(DateTimeUtil.getFormattedKoreanTime("yyyy-MM-dd HH:mm:ss", koreanTime.toInstant())).willReturn(expectedResult);

            actualResult = converter.getUpdatedAtInKoreanTime(digitalDocumentUser);
        }

        @Test
        void shouldReturnExpectedResult() {
            assertThat(actualResult, is(expectedResult));
        }

    }

    @Nested
    class GetPhoneNumber {
        @Test
        void shouldReturnPhoneNumber() {

            // Given
            final String hashedPhoneNumber = someString(5);
            final String phone1 = someNumericString(3);
            final String phone2 = someNumericString(4);
            final String phone3 = someNumericString(4);
            final String phoneNumber = phone1 + phone2 + phone3;

            given(digitalDocumentUser.getHashedPhoneNumber()).willReturn(hashedPhoneNumber);
            given(decryptColumnConverter.convert(hashedPhoneNumber)).willReturn(phoneNumber);

            // When
            final String actualPhoneNumber = converter.getPhoneNumber(digitalDocumentUser);

            // Then
            assertThat(actualPhoneNumber, is(phone1 + "-" + phone2 + "-" + phone3));
        }
    }
}
