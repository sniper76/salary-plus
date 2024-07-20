package ag.act.validator;

import ag.act.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class DefaultRequestValidator {

    public void validateNotNull(String value, String errorMessage) {
        if (StringUtils.isBlank(value)) {
            log.error("validateNotNull isBlank error: {}", errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }

    public void validateNotNull(Long value, String errorMessage) {
        if (value == null) {
            log.error("validateNotNull isNull error: {}", errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }

    public void validateNotNull(MultipartFile value, String errorMessage) {
        if (value == null) {
            log.error("validateNotNull isNull error: {}", errorMessage);
            throw new BadRequestException(errorMessage);
        }
    }
}
