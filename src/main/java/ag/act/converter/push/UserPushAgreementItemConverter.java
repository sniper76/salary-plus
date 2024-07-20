package ag.act.converter.push;

import ag.act.dto.push.UserPushAgreementStatusDto;
import ag.act.enums.push.UserPushAgreementType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserPushAgreementItemConverter {
    public List<UserPushAgreementStatusDto> convert(
        List<ag.act.model.UserPushAgreementItem> userPushAgreementItems
    ) {
        return userPushAgreementItems.stream()
            .flatMap(item ->
                item.getAgreementTypes().stream()
                    .map(type ->
                        new UserPushAgreementStatusDto(
                            UserPushAgreementType.fromValue(type),
                            item.getValue()
                        )
                    )
            )
            .toList();
    }
}
