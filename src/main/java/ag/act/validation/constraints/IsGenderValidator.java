package ag.act.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class IsGenderValidator implements ConstraintValidator<IsGender, String> {

    @Override
    public void initialize(IsGender constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        try {
            ag.act.model.Gender.fromValue(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}