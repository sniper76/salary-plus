package ag.act.service.push;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.push.UserPushAgreementItemGenerator;
import ag.act.dto.push.UserPushAgreementStatusDto;
import ag.act.entity.User;
import ag.act.entity.UserPushAgreement;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.enums.push.UserPushAgreementItemType;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.model.UserPushAgreementItem;
import ag.act.model.UserPushAgreementItemsDataResponse;
import ag.act.repository.UserPushAgreementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserPushAgreementService {
    private final UserPushAgreementItemGenerator userPushAgreementItemGenerator;
    private final UserPushAgreementChecker userPushAgreementChecker;
    private final UserPushAgreementRepository userPushAgreementRepository;

    public UserPushAgreementItemsDataResponse getUserPushAgreements() {
        User user = ActUserProvider.getNoneNull();

        List<ag.act.model.UserPushAgreementItem> result = getPushAgreementItemsForSubItemType(user.getId());

        ArrayList<UserPushAgreementItem> mutableResult = new ArrayList<>(result);
        addPushAgreementItemForAllItemType(mutableResult);
        List<ag.act.model.UserPushAgreementItem> finalResult = Collections.unmodifiableList(mutableResult);

        return new ag.act.model.UserPushAgreementItemsDataResponse().data(finalResult);
    }

    private List<ag.act.model.UserPushAgreementItem> getPushAgreementItemsForSubItemType(Long userId) {
        return Arrays.stream(UserPushAgreementGroupType.values())
            .map(groupType -> {
                List<UserPushAgreementType> agreementTypes = groupType.getAgreementTypes();

                return userPushAgreementItemGenerator.generate(
                    groupType.getTitle(),
                    agreementTypes,
                    userPushAgreementChecker.isAgreeToReceive(userId, groupType),
                    UserPushAgreementItemType.SUB
                );
            })
            .toList();
    }

    private void addPushAgreementItemForAllItemType(List<ag.act.model.UserPushAgreementItem> userPushAgreementItems) {
        boolean anyAgreement = userPushAgreementItems.stream().anyMatch(ag.act.model.UserPushAgreementItem::getValue);

        userPushAgreementItems.add(
            0,
            userPushAgreementItemGenerator.generate(
                UserPushAgreementItemType.ALL.getTitle(),
                Arrays.stream(UserPushAgreementGroupType.values())
                    .flatMap(it -> it.getAgreementTypes().stream())
                    .toList(),
                anyAgreement,
                UserPushAgreementItemType.ALL
            )
        );
    }

    public void updateUserPushAgreementStatus(
        List<UserPushAgreementStatusDto> userPushAgreementStatusDtoList
    ) {
        User user = ActUserProvider.getNoneNull();

        userPushAgreementStatusDtoList.forEach(dto -> upsert(user, dto.getType(), dto.isAgree()));
    }

    private void upsert(
        User user,
        UserPushAgreementType type,
        boolean agreeToReceive
    ) {
        UserPushAgreement agreement =
            userPushAgreementRepository.findByUserIdAndType(user.getId(), type)
                .map(it -> {
                    it.setAgreeToReceive(agreeToReceive);
                    return it;
                })
                .orElseGet(() -> {
                    UserPushAgreement newAgreement = new UserPushAgreement();
                    newAgreement.setUser(user);
                    newAgreement.setType(type);
                    newAgreement.setAgreeToReceive(agreeToReceive);
                    newAgreement.setStatus(ag.act.model.Status.ACTIVE);

                    return newAgreement;
                });

        userPushAgreementRepository.save(agreement);
    }
}
