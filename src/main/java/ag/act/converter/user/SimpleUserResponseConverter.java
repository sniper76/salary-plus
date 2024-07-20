package ag.act.converter.user;

import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.User;
import ag.act.model.SimpleUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleUserResponseConverter implements Converter<User, ag.act.model.SimpleUserResponse> {

    private final DecryptColumnConverter decryptColumnConverter;

    public SimpleUserResponse convert(User user) {
        return new SimpleUserResponse()
            .id(user.getId())
            .name(user.getName())
            .birthDate(DateTimeConverter.convert(user.getBirthDate()))
            .email(user.getEmail())
            .status(user.getStatus())
            .nickname(user.getNickname())
            .phoneNumber(decryptColumnConverter.convert(user.getHashedPhoneNumber()));
    }

    @Override
    public SimpleUserResponse apply(User user) {
        return this.convert(user);
    }
}
