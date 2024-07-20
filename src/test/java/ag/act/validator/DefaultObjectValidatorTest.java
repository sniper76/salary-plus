package ag.act.validator;

import ag.act.entity.ActEntity;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.model.Status;
import ag.act.util.StatusUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultObjectValidatorTest {

    @InjectMocks
    private DefaultObjectValidator validator;

    @Nested
    class ValidateAndGetWithErrorMessage {
        @Nested
        class WhenValueIsNotNull {
            @Test
            void shouldNotThrowAnyException() {

                // Given
                final String value = someString(5);
                final Optional<String> optional = Optional.of(value);
                final String message = someString(5);

                // When
                final String actual = validator.validateAndGet(optional, message);

                // Then
                assertThat(actual, is(value));
            }
        }

        @Nested
        class WhenValueIsInvalid {

            @Test
            void shouldThrowNotFoundException() {

                // Given
                final Optional<String> optional = Optional.empty();
                final String message = someString(5);

                // When
                final NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> validator.validateAndGet(optional, message)
                );

                // Then
                assertThat(exception.getMessage(), is(message));
            }
        }
    }

    @Nested
    class ValidateStatus {

        @Mock
        private ActEntity actEntity;

        @Nested
        class WhenStatusIsNotDeleted {

            @Test
            void shouldNotThrowAnyException() {

                // Given
                final String message = someString(5);
                given(actEntity.getStatus()).willReturn(someThing(Status.ACTIVE, Status.INACTIVE_BY_USER, Status.INACTIVE_BY_ADMIN));

                // When
                validator.validateStatus(actEntity, StatusUtil.getDeleteStatuses(), message);
            }
        }

        @Nested
        class WhenStatusIsDeleted {

            @Test
            void shouldThrowNotFoundException() {

                // Given
                final String message = someString(5);
                given(actEntity.getStatus()).willReturn(someThing(Status.DELETED_BY_ADMIN, Status.DELETED_BY_USER));

                // When
                final NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> validator.validateStatus(actEntity, StatusUtil.getDeleteStatuses(), message)
                );

                // Then
                assertThat(exception.getMessage(), is(message));
            }
        }
    }

    @Nested
    class ValidateNotEmpty {

        @Nested
        class WhenCollectionIsNotEmpty {

            @Test
            void shouldNotThrowAnyException() {

                // Given
                final String message = someString(5);

                // When
                validator.validateNotEmpty(List.of(someString(5)), message);
            }
        }

        @Nested
        class WhenStatusIsDeleted {

            @Test
            void shouldThrowInternalServerException() {

                // Given
                final String message = someString(5);

                // When
                final InternalServerException exception = assertThrows(
                    InternalServerException.class,
                    () -> validator.validateNotEmpty(List.of(), message)
                );

                // Then
                assertThat(exception.getMessage(), is(message));
            }
        }
    }
}