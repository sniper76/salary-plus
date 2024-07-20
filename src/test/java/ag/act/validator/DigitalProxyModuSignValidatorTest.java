package ag.act.validator;

import ag.act.entity.DigitalProxy;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.validator.document.DigitalProxyModuSignValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomTimes.someTimeInThePast;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalProxyModuSignValidatorTest {

    private DigitalProxyModuSignValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DigitalProxyModuSignValidator();
    }

    @Nested
    class ValidateAndGet {
        @Mock
        private DigitalProxy digitalProxy;

        @Test
        void shouldValidateAndReturnDigitalProxy() {

            // When
            final DigitalProxy actual = validator.validateAndGet(digitalProxy);

            // Then
            assertThat(actual, is(digitalProxy));
        }

        @Test
        void shouldThrowNotFoundException() {
            assertException(
                NotFoundException.class,
                () -> validator.validateAndGet(null),
                "의결권위임 정보를 찾을 수 없습니다."
            );
        }
    }

    @Nested
    class ValidateModuSignDocument {
        @Mock
        private ModuSignDocument document;

        @BeforeEach
        void setUp() {
            // Given
            given(document.getParticipantId()).willReturn(someString(5));
            given(document.getId()).willReturn(someString(5));
        }

        @Test
        void shouldNotThrowAnyException() {

            // When // Then
            validator.validateModuSignDocument(document);
        }

        @Nested
        class WhenParticipantIdIsNull {
            @Test
            void shouldThrowInternalServerException() {

                // Given
                given(document.getParticipantId()).willReturn(null);

                // When // Then
                assertException(
                    InternalServerException.class,
                    () -> validator.validateModuSignDocument(document),
                    "모두싸인을 통해 위임중에 알 수 없는 오류가 발생하였습니다. (participantId)"
                );
            }
        }

        @Nested
        class WhenDocumentIdIsNull {
            @Test
            void shouldThrowInternalServerException() {

                // Given
                given(document.getId()).willReturn(null);

                // When // Then
                assertException(
                    InternalServerException.class,
                    () -> validator.validateModuSignDocument(document),
                    "모두싸인을 통해 위임중에 알 수 없는 오류가 발생하였습니다. (documentId)"
                );
            }
        }
    }

    @Nested
    class ValidateCreateDigitalProxyRequest {

        @Nested
        class CreateDigitalProxyRequestIsNull {
            @Test
            void shouldNotThrowAnyException() {

                // When // Then
                validator.validate(null);
            }
        }

        @Nested
        class TargetEndDateIsPast {
            @Mock
            private ag.act.model.CreateDigitalProxyRequest createDigitalProxyRequest;

            @Test
            void shouldThrowBadRequestException() {
                // Given
                given(createDigitalProxyRequest.getTargetEndDate()).willReturn(someTimeInThePast().toInstant());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createDigitalProxyRequest),
                    "의결권위임 종료일은 현재 시간 이후로 설정해주세요."
                );
            }
        }
    }
}
