package ag.act.converter.mydata;

import ag.act.dto.mydata.MyDataAuthTokenRequestDto;
import ag.act.entity.User;
import ag.act.parser.DateTimeParser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MyDataConverter {

    public MyDataAuthTokenRequestDto convertToRequest(
        User user,
        String ci,
        String phoneNumber,
        String accessToken,
        String clientId,
        String clientSecret
    ) {
        return MyDataAuthTokenRequestDto.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .txId(UUID.randomUUID().toString())
            .userId(user.getId().toString())
            .userEmail(user.getEmail())
            .ci(ci)
            .realName(user.getName())
            .phoneNum(phoneNumber)
            .gender(user.getGender().toString())
            .birthday(DateTimeParser.toDate(user.getBirthDate()))
            .accessToken(accessToken)
            .build();
    }
}
