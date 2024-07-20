package ag.act.converter.push;

import ag.act.enums.push.UserPushAgreementItemType;
import ag.act.enums.push.UserPushAgreementType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserPushAgreementItemGenerator {
    public ag.act.model.UserPushAgreementItem generate(
        String title,
        List<UserPushAgreementType> agreementTypes,
        Boolean value,
        UserPushAgreementItemType itemType
    ) {
        return new ag.act.model.UserPushAgreementItem()
            .title(title)
            .agreementTypes(
                agreementTypes.stream()
                    .map(UserPushAgreementType::name)
                    .toList()
            )
            .value(value)
            .itemType(itemType.name());
    }

}
