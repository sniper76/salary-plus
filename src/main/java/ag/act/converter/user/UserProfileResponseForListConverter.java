package ag.act.converter.user;

import ag.act.constants.MessageConstants;
import ag.act.converter.ContentUserProfileAdminLabelsHider;
import ag.act.converter.ContentUserProfileAnonymousFieldsSetter;
import ag.act.core.annotation.ContentOverrider;
import ag.act.entity.ContentUserProfile;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.util.badge.StockCountLabelGenerator;
import ag.act.util.badge.TotalAssetLabelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class UserProfileResponseForListConverter implements MessageConstants {
    private final UserIpConverter userIpConverter;
    private final TotalAssetLabelGenerator totalAssetLabelGenerator;
    private final StockCountLabelGenerator stockCountLabelGenerator;
    private final ContentUserProfileAdminLabelsHider contentUserProfileAdminLabelsHider;
    private final ContentUserProfileAnonymousFieldsSetter contentUserProfileAnonymousFieldsSetter;

    @ContentOverrider
    public ag.act.model.UserProfileResponse convert(
        User user,
        String individualStockCountLabel,
        String totalAssetLabel
    ) {
        ag.act.model.UserProfileResponse userProfileResponse = new ag.act.model.UserProfileResponse()
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl())
            .individualStockCountLabel(individualStockCountLabel)
            .totalAssetLabel(totalAssetLabel);

        contentUserProfileAdminLabelsHider.hideAdminLabels(user, userProfileResponse);

        return userProfileResponse;
    }

    @ContentOverrider
    public ag.act.model.UserProfileResponse convert(
        User user,
        UserHoldingStock queriedUserHoldingStock,
        List<UserHoldingStock> userHoldingStocks
    ) {
        return convert(
            user,
            stockCountLabelGenerator.generate(queriedUserHoldingStock),
            totalAssetLabelGenerator.generate(userHoldingStocks)
        );
    }

    public ag.act.model.UserProfileResponse convertUserProfile(Long userId, ContentUserProfile contentUserProfile) {
        if (contentUserProfile == null) {
            return null;
        }

        String nickname = contentUserProfile.getNickname();
        ag.act.model.UserProfileResponse userProfileResponse = new ag.act.model.UserProfileResponse();
        userProfileResponse.setNickname(nickname);
        userProfileResponse.setIndividualStockCountLabel(contentUserProfile.getIndividualStockCountLabel());
        userProfileResponse.setTotalAssetLabel(contentUserProfile.getTotalAssetLabel());
        userProfileResponse.setProfileImageUrl(contentUserProfile.getProfileImageUrl());
        userProfileResponse.setUserIp(userIpConverter.convert(contentUserProfile.getUserIp()));
        userProfileResponse.setIsSolidarityLeader(contentUserProfile.getIsSolidarityLeader());

        setAnonymous(nickname, userProfileResponse);
        contentUserProfileAdminLabelsHider.hideAdminLabels(userId, userProfileResponse);

        return userProfileResponse;
    }

    private void setAnonymous(String nickname, ag.act.model.UserProfileResponse userProfileResponse) {
        if (nickname != null && nickname.contains(ANONYMOUS_NAME)) {
            contentUserProfileAnonymousFieldsSetter.setFieldsForAnonymous(userProfileResponse);
        }
    }
}
