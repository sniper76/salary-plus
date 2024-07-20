package ag.act.validator;

import ag.act.enums.MediaType;
import ag.act.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ImageMediaTypeValidator {

    public void validate(MultipartFile file) {
        final String contentType = file.getContentType();
        try {
            MediaType.fromValue(contentType);
        } catch (IllegalArgumentException e) {
            log.error("Not supported content type - {}", contentType, e);
            throw new BadRequestException("지원하지 않는 이미지 형식입니다.", e);
        }
    }
}
