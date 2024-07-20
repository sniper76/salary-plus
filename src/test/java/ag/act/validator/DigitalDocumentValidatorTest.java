package ag.act.validator;

import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.exception.BadRequestException;
import ag.act.validator.document.DigitalDocumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentValidatorTest {

    @InjectMocks
    private DigitalDocumentValidator validator;

    @Mock
    private ag.act.model.CreateDigitalDocumentRequest createDigitalDocumentRequest;

    @Nested
    class ValidateDigitalDocument {
        private Instant targetStartDate;
        private Instant targetEndDate;
        private Long stockReferenceDateId;
        @Mock
        private ag.act.model.JsonAttachOption jsonAttachOption;

        @Nested
        class WhenSuccess {

            @BeforeEach
            void setUp() {
                targetStartDate = Instant.now();
                targetEndDate = targetStartDate.plus(2, ChronoUnit.DAYS);
                stockReferenceDateId = someLong();

                given(createDigitalDocumentRequest.getStockReferenceDateId()).willReturn(stockReferenceDateId);
                given(createDigitalDocumentRequest.getTargetStartDate()).willReturn(targetStartDate);
                given(createDigitalDocumentRequest.getTargetEndDate()).willReturn(targetEndDate);
                given(createDigitalDocumentRequest.getAttachOptions()).willReturn(jsonAttachOption);
                given(createDigitalDocumentRequest.getType()).willReturn(DigitalDocumentType.DIGITAL_PROXY.name());
                given(createDigitalDocumentRequest.getVersion()).willReturn(DigitalDocumentVersion.V1.name());
                given(jsonAttachOption.getSignImage()).willReturn(someEnum(AttachOptionType.class).name());
                given(jsonAttachOption.getIdCardImage()).willReturn(someEnum(AttachOptionType.class).name());
                given(jsonAttachOption.getBankAccountImage()).willReturn(someEnum(AttachOptionType.class).name());
                given(jsonAttachOption.getHectoEncryptedBankAccountPdf()).willReturn(someEnum(AttachOptionType.class).name());
            }

            @Test
            void shouldBeSuccess() {
                validator.validateCommon(createDigitalDocumentRequest);
            }
        }

        @Nested
        class WhenBothNotExists {

            @BeforeEach
            void setUp() {
                stockReferenceDateId = someLong();

                given(createDigitalDocumentRequest.getStockReferenceDateId()).willReturn(stockReferenceDateId);
            }

            @Test
            void shouldBeException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateCommon(createDigitalDocumentRequest)
                );
                // Then
                assertThat(exception.getMessage(), is("시작일 종료일을 확인하세요."));
            }
        }

        @Nested
        class WhenBeforeDate {

            @BeforeEach
            void setUp() {
                targetEndDate = Instant.now();
                targetStartDate = targetEndDate.plus(2, ChronoUnit.DAYS);
                stockReferenceDateId = someLong();

                given(createDigitalDocumentRequest.getStockReferenceDateId()).willReturn(stockReferenceDateId);
                given(createDigitalDocumentRequest.getTargetStartDate()).willReturn(targetStartDate);
                given(createDigitalDocumentRequest.getTargetEndDate()).willReturn(targetEndDate);
            }

            @Test
            void shouldBeforeException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateCommon(createDigitalDocumentRequest)
                );
                // Then
                assertThat(exception.getMessage(), is("종료일을 시작일 이전으로 등록 불가능합니다."));
            }
        }

        @Nested
        class WhenIsPastDate {

            @BeforeEach
            void setUp() {
                targetStartDate = Instant.now();
                targetEndDate = targetStartDate;
                stockReferenceDateId = someLong();

                given(createDigitalDocumentRequest.getStockReferenceDateId()).willReturn(stockReferenceDateId);
                given(createDigitalDocumentRequest.getTargetStartDate()).willReturn(targetStartDate);
                given(createDigitalDocumentRequest.getTargetEndDate()).willReturn(targetEndDate);
            }

            @Test
            void shouldIsPastException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateCommon(createDigitalDocumentRequest)
                );
                // Then
                assertThat(exception.getMessage(), is("종료일은 현재 시간 이후로 설정해주세요."));
            }
        }

        @Nested
        class WhenValidateEndDate {
            private String text;
            private LocalDateTime endDateTime;

            @BeforeEach
            void setUp() {
                text = "제출";
                endDateTime = LocalDateTime.now().minusDays(1);
            }

            @Test
            void shouldBeforeEndDateException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateTargetEndDate(endDateTime, text)
                );
                // Then
                assertThat(exception.getMessage(), is("이미 위임기간이 종료되어 제출하실 수 없습니다."));
            }
        }

        @Nested
        class WhenValidateEndDateIsPast {
            private Instant endDateTime;

            @BeforeEach
            void setUp() {
                endDateTime = Instant.now().minus(1, ChronoUnit.DAYS);
            }

            @Test
            void shouldBeforeEndDateException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateTargetEndDateIsPast(endDateTime)
                );
                // Then
                assertThat(exception.getMessage(), is("종료일은 현재 시간 이후로 설정해주세요."));
            }
        }

        @Nested
        class WhenValidateTargetStartDateIsBeforeTargetEndDate {
            private Instant startDateTime;
            private Instant endDateTime;

            @BeforeEach
            void setUp() {
                startDateTime = Instant.now();
                endDateTime = startDateTime.minus(1, ChronoUnit.DAYS);
            }

            @Test
            void shouldBeforeEndDateException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateTargetStartDateIsBeforeTargetEndDate(startDateTime, endDateTime)
                );
                // Then
                assertThat(exception.getMessage(), is("종료일을 시작일 이전으로 등록 불가능합니다."));
            }
        }

        @Nested
        class WhenValidateTargetStartDateIsBeforeToday {
            private LocalDateTime startDateTime;

            @BeforeEach
            void setUp() {
                startDateTime = LocalDateTime.now().minusDays(1);
            }

            @Test
            void shouldBeforeEndDateException() {
                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> validator.validateTargetStartDateIsBeforeToday(startDateTime)
                );
                // Then
                assertThat(exception.getMessage(), is("기준일은 시작일 이전에 변경 가능합니다."));
            }
        }
    }
}
