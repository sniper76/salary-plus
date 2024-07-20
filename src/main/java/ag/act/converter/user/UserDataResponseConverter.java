package ag.act.converter.user;

import ag.act.entity.User;
import ag.act.model.UserDataResponse;
import org.springframework.stereotype.Component;

@Component
public class UserDataResponseConverter {

    private final UserConverter userConverter;

    public UserDataResponseConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    public UserDataResponse convert(User user) {
        return new UserDataResponse().data(userConverter.convert(user));
    }
}
