package ag.act.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class IsAuthTypeValidator implements ConstraintValidator<IsAuthType, String> {

    @Override
    public void initialize(IsAuthType constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        try {
            ag.act.model.AuthType.fromValue(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
