package ag.act.validator.user;

import ag.act.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CorporateUserValidator {

    private static final Pattern VALID_CORPORATE_NO_PATTERN = Pattern.compile("^\\d*(\\d|\\d-)*\\d$");
    private static final int CORPORATE_NO_MAX_LENGTH = 255;

    public void validateCorporateNo(String corporateNo) {
        if (!validateCorporateNoLength(corporateNo) || !validateCorporateNoPattern(corporateNo)) {
            throw new BadRequestException("법인사업자번호 형식이 맞지 않습니다.");
        }
    }

    private boolean validateCorporateNoLength(String corporateNo) {
        return corporateNo.length() <= CORPORATE_NO_MAX_LENGTH;
    }

    private boolean validateCorporateNoPattern(String corporateNo) {
        return VALID_CORPORATE_NO_PATTERN.matcher(corporateNo).matches();
    }
}
