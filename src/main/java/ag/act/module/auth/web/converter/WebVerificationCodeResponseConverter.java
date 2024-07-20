package ag.act.module.auth.web.converter;

import ag.act.configuration.security.TokenProvider;
import ag.act.converter.user.UserConverter;
import ag.act.entity.User;
import ag.act.model.WebVerificationCodeResponse;
import ag.act.model.WebVerificationStatus;
import ag.act.module.auth.web.WebVerificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WebVerificationCodeResponseConverter {
    private final TokenProvider tokenProvider;
    private final UserConverter userConverter;

    public WebVerificationCodeResponse convert(final WebVerificationDto webVerificationDto) {
        final User user = webVerificationDto.user();

        if (webVerificationDto.status() == WebVerificationStatus.VERIFIED) {
            return new WebVerificationCodeResponse()
                .status(webVerificationDto.status())
                .token(tokenProvider.createWebToken(user.getId().toString()))
                .user(userConverter.convert(user));
        }

        return new WebVerificationCodeResponse()
            .status(webVerificationDto.status());
    }
}
