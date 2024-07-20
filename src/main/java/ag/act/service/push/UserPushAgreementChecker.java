package ag.act.service.push;

import ag.act.entity.UserPushAgreement;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.repository.UserPushAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserPushAgreementChecker {
    private final UserPushAgreementRepository userPushAgreementRepository;

    public boolean isAgreeToReceive(Long userId, UserPushAgreementGroupType groupType) {
        List<UserPushAgreementType> agreementTypes = groupType.getAgreementTypes();

        return agreementTypes.stream()
            .anyMatch(it -> isAgreeToReceive(userId, it));
    }

    private Boolean isAgreeToReceive(
        Long userId,
        UserPushAgreementType userPushAgreementType
    ) {
        return userPushAgreementRepository.findByUserIdAndType(
                userId, userPushAgreementType
            )
            .map(UserPushAgreement::getAgreeToReceive)
            .orElse(true);
    }
}
