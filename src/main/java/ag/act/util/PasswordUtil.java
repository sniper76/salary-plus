package ag.act.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtil {

    private final String passwordPattern;

    @Getter
    private final int minLength;

    public PasswordUtil(
        @Value("${security.password.min.length}") int minLength,
        @Value("${security.password.max.length}") int maxLength
    ) {
        this.minLength = minLength;
        passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\!@#$%^&\\*\\(\\)\\_\\-\\+=<>?]).{" + minLength + "," + maxLength + "}$";
    }

    public boolean isStrongPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }

        return Pattern.matches(passwordPattern, password);
    }
}
