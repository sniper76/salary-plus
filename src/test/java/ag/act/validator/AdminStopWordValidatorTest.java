package ag.act.validator;

import ag.act.entity.StopWord;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.repository.StopWordRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.enums.ActErrorCode.DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminStopWordValidatorTest {

    @Mock
    private StopWordRepository stopWordRepository;
    @Mock
    private StopWord stopWord;
    @InjectMocks
    private AdminStopWordValidator validator;

    @Nested
    class ValidateDuplicateStopWord {

        @Nested
        class ShouldThrowException {
            @Test
            void whenExistingStopWordIsActive() {
                // Given
                given(stopWordRepository.findByWord(anyString())).willReturn(Optional.of(stopWord));
                given(stopWord.isActive()).willReturn(true);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateDuplicateStopWord(anyString()),
                    "이미 등록된 금칙어입니다."
                );
            }

            @Test
            void whenExistingStopWordIsInActive() {
                // Given
                final String trimmedWord = someAlphanumericString(10);
                final Long stopWordId = someLong();

                given(stopWordRepository.findByWord(trimmedWord)).willReturn(Optional.of(stopWord));
                given(stopWord.isActive()).willReturn(false);
                given(stopWord.getId()).willReturn(stopWordId);
                given(stopWord.getWord()).willReturn(trimmedWord);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateDuplicateStopWord(trimmedWord),
                    "이미 등록된 비활성화 금칙어입니다. 활성화 하시겠습니까?",
                    DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE,
                    Map.of("stopWordId", stopWordId, "word", trimmedWord)
                );
            }
        }
    }

    @Nested
    class ValidateAlreadyUpdate {

        @Nested
        class ShouldThrowException {
            @Test
            void whenAlreadyActivate() {
                // Given
                given(stopWord.getStatus()).willReturn(Status.ACTIVE);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateAlreadyUpdate(stopWord, Status.INACTIVE_BY_ADMIN, Status.ACTIVE),
                    "이미 활성화된 금칙어입니다."
                );
            }

            @Test
            void whenAlreadyInActivate() {
                // Given
                given(stopWord.getStatus()).willReturn(Status.INACTIVE_BY_ADMIN);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateAlreadyUpdate(stopWord, Status.ACTIVE, Status.INACTIVE_BY_ADMIN),
                    "이미 비활성화된 금칙어입니다."
                );
            }
        }
    }

    @Nested
    class ValidateStopWordStatus {
        @Nested
        class ShouldThrowException {
            @Test
            void whenNotStopWordStatus() {

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateStopWordStatus(Status.INACTIVE_BY_USER, Status.ACTIVE),
                    "상태를 확인해주세요."
                );
            }
        }
    }
}
