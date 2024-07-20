package ag.act.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class IsPhoneNumberValidator implements ConstraintValidator<IsPhoneNumber, String> {

    @Override
    public void initialize(IsPhoneNumber constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        return value.matches("^(010)\\d{8}$");
    }
}