package ag.act.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IsBirthDateValidator implements ConstraintValidator<IsBirthDate, String> {

    private static final String DATE_FORMAT = "yyyyMMdd";

    @Override
    public void initialize(IsBirthDate constraintAnnotation) {
        // No initialization required
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean isValid(String birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false;
        }

        try {
            LocalDate.parse(birthDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}