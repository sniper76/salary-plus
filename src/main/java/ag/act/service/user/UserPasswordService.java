package ag.act.service.user;

import ag.act.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserPasswordService {
    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean isCorrectPassword(String password, String encryptedPassword) {
        return passwordEncoder.matches(password, encryptedPassword);
    }

    public User setPasswordAndChangeRequired(User user, String password, Boolean isChangePasswordRequired) {
        user.setPassword(encodePassword(password));
        user.setIsChangePasswordRequired(isChangePasswordRequired);
        return user;
    }
}
