package ag.act.validator;

import ag.act.exception.BadRequestException;
import ag.act.service.user.UserPasswordService;
import ag.act.util.PasswordUtil;
import ag.act.validator.user.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStrongPassword;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordValidatorTest {

    @InjectMocks
    private PasswordValidator validator;

    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private PasswordUtil passwordUtil;

    @Nested
    class ValidateChangePassword {

        @Mock
        private ag.act.model.ChangePasswordRequest request;
        private String currentPassword;
        private String currentEncryptedPassword;
        private String password;

        @BeforeEach
        void setUp() {
            currentPassword = someString(15).trim();
            currentEncryptedPassword = someString(10).trim();
            password = someStrongPassword(8, 20);
            String confirmPassword = password;

            given(request.getCurrentPassword()).willReturn(currentPassword);
            given(request.getPassword()).willReturn(password);
            given(request.getConfirmPassword()).willReturn(confirmPassword);
            given(userPasswordService.isCorrectPassword(currentPassword, currentEncryptedPassword)).willReturn(true);
            given(passwordUtil.isStrongPassword(password)).willReturn(true);
        }

        @Nested
        class ValidateSuccessfully {

            @Test
            void shouldValidatePassword() {
                validator.validateChangePassword(request, currentEncryptedPassword);
            }
        }

        @Nested
        class FailToValidateCurrentPassword {
            @Test
            void shouldThrowBadRequest() {

                // Given
                given(userPasswordService.isCorrectPassword(currentPassword, currentEncryptedPassword))
                    .willReturn(false);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateChangePassword(request, currentEncryptedPassword),
                    "현재 비밀번호가 일치하지 않습니다."
                );
            }
        }

        @Nested
        class PasswordAndConfirmPasswordAreNotSame {
            @Test
            void shouldThrowBadRequest() {

                // Given
                given(request.getPassword()).willReturn(someString(10));
                given(request.getConfirmPassword()).willReturn(someString(20));
                given(userPasswordService.isCorrectPassword(anyString(), anyString())).willReturn(true);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateChangePassword(request, currentEncryptedPassword),
                    "비밀번호와 컨펌 비밀번호가 일치하지 않습니다."
                );
            }
        }

        @Nested
        class PasswordIsNotStrongEnough {

            @Test
            void shouldThrowBadRequest() {

                // Given
                final Integer minLength = someIntegerBetween(10, 20);
                given(userPasswordService.isCorrectPassword(anyString(), anyString())).willReturn(true);
                given(passwordUtil.isStrongPassword(password)).willReturn(false);
                given(passwordUtil.getMinLength()).willReturn(minLength);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateChangePassword(request, currentEncryptedPassword),
                    "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함하여 %s자 이상이어야 합니다.".formatted(minLength)
                );
            }
        }

        @Nested
        class NewPasswordIsSameAsCurrentPassword {

            @Test
            void shouldThrowBadRequest() {

                // Given
                final String samePassword = someString(15);

                given(request.getPassword()).willReturn(samePassword);
                given(request.getCurrentPassword()).willReturn(samePassword);
                given(request.getConfirmPassword()).willReturn(samePassword);
                given(userPasswordService.isCorrectPassword(anyString(), anyString())).willReturn(true);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateChangePassword(request, currentEncryptedPassword),
                    "현재 비밀번호와 동일한 비밀번호는 사용할 수 없습니다."
                );
            }
        }
    }
}
