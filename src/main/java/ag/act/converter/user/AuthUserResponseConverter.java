package ag.act.converter.user;

import ag.act.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthUserResponseConverter {

    private final UserConverter userConverter;

    public AuthUserResponseConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    public ag.act.model.AuthUserResponse convert(ag.act.model.UserResponse userResponse, String accessToken, String finpongAccessToken) {
        return new ag.act.model.AuthUserResponse()
            .token(new ag.act.model.AuthUserResponseToken().accessToken(accessToken).finpongAccessToken(finpongAccessToken))
            .user(userResponse);
    }

    public ag.act.model.AuthUserResponse convert(User user, String accessToken, String finpongAccessToken) {
        final ag.act.model.UserResponse userResponse = userConverter.convert(user);

        return new ag.act.model.AuthUserResponse()
            .token(new ag.act.model.AuthUserResponseToken().accessToken(accessToken).finpongAccessToken(finpongAccessToken))
            .user(userResponse);
    }
}
