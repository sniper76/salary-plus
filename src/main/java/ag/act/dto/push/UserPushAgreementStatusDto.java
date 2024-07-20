package ag.act.dto.push;

import ag.act.enums.push.UserPushAgreementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPushAgreementStatusDto {
    private UserPushAgreementType type;
    private boolean agree;
}
