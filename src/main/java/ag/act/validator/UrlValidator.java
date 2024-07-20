package ag.act.validator;

import ag.act.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UrlValidator {
    //일반적인 web url에 대한 정규표현식
    private static final String urlRegex = "https?://(?:www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*(/[\\w-/]*)?";

    public void validate(String url) {
        if (StringUtils.isBlank(url)) {
            throw new BadRequestException("url을 확인해주세요.");
        }
        if (!url.trim().matches(urlRegex)) {
            throw new BadRequestException("url 형식이 잘못되었습니다. 올바른 url을 입력해주세요.");
        }
    }
}
