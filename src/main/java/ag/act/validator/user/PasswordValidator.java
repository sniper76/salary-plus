package ag.act.validator.user;

import ag.act.exception.BadRequestException;
import ag.act.model.ChangePasswordRequest;
import ag.act.service.user.UserPasswordService;
import ag.act.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class PasswordValidator {
    private final UserPasswordService userPasswordService;
    private final PasswordUtil passwordUtil;

    public void validateChangePassword(ChangePasswordRequest changePasswordRequest, String currentEncryptedPassword) {
        final String currentPassword = changePasswordRequest.getCurrentPassword().trim();

        if (!userPasswordService.isCorrectPassword(currentPassword, currentEncryptedPassword)) {
            throw new BadRequestException("현재 비밀번호가 일치하지 않습니다.");
        }

        final String password = changePasswordRequest.getPassword().trim();
        final String confirmPassword = changePasswordRequest.getConfirmPassword().trim();

        if (!Objects.equals(password, confirmPassword)) {
            throw new BadRequestException("비밀번호와 컨펌 비밀번호가 일치하지 않습니다.");
        }

        if (Objects.equals(password, currentPassword)) {
            throw new BadRequestException("현재 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");
        }

        if (!passwordUtil.isStrongPassword(password)) {
            throw new BadRequestException("비밀번호는 영문 대소문자, 숫자, 특수문자를 포함하여 %s자 이상이어야 합니다.".formatted(passwordUtil.getMinLength()));
        }
    }
}
