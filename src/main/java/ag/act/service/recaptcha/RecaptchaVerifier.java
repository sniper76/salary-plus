package ag.act.service.recaptcha;

import ag.act.core.holder.RequestContextHolder;
import ag.act.module.captcha.RecaptchaHttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecaptchaVerifier {
    private final RecaptchaHttpClientUtil recaptchaHttpClientUtil;

    public boolean verifyCaptcha(String captchaResponse) {
        Map<String, String> body = new HashMap<>();
        body.put("response", captchaResponse);
        body.put("remoteip", RequestContextHolder.getUserIP());

        final Map<String, Object> responseMap = recaptchaHttpClientUtil.callApi(body);

        return (boolean) responseMap.getOrDefault("success", false);
    }
}
