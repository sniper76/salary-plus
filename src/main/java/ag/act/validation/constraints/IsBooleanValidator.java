package ag.act.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsBooleanValidator implements ConstraintValidator<IsBoolean, Boolean> {

    @Override
    public void initialize(IsBoolean constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        return value != null;
    }
}