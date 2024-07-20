package ag.act.converter.user;

import ag.act.constants.MessageConstants;
import ag.act.converter.ContentUserProfileAdminLabelsHider;
import ag.act.converter.ContentUserProfileAnonymousFieldsSetter;
import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.entity.ContentUserProfile;
import ag.act.model.UserProfileResponse;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserProfileResponseForDetailsConverter implements MessageConstants {
    private final UserIpConverter userIpConverter;
    private final SolidarityLeaderService solidarityLeaderService;
    private final SimpleStockResponseConverter simpleStockResponseConverter;
    private final ContentUserProfileAnonymousFieldsSetter contentUserProfileAnonymousFieldsSetter;
    private final ContentUserProfileAdminLabelsHider contentUserProfileAdminLabelsHider;

    public UserProfileResponse convert(Long userId, ContentUserProfile contentUserProfile) {
        if (contentUserProfile == null) {
            return null;
        }

        String nickname = contentUserProfile.getNickname();
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setNickname(nickname);
        userProfileResponse.setIndividualStockCountLabel(contentUserProfile.getIndividualStockCountLabel());
        userProfileResponse.setTotalAssetLabel(contentUserProfile.getTotalAssetLabel());
        userProfileResponse.setProfileImageUrl(contentUserProfile.getProfileImageUrl());
        userProfileResponse.setUserIp(userIpConverter.convert(contentUserProfile.getUserIp()));
        userProfileResponse.setIsSolidarityLeader(contentUserProfile.getIsSolidarityLeader());
        userProfileResponse.setLeadingStocks(
            simpleStockResponseConverter.convertSimpleStocks(solidarityLeaderService.findAllLeadingSimpleStocks(userId))
        );

        setAnonymous(nickname, userProfileResponse);
        contentUserProfileAdminLabelsHider.hideAdminLabels(userId, userProfileResponse);
        return userProfileResponse;
    }

    private void setAnonymous(String nickname, UserProfileResponse userProfileResponse) {
        if (nickname != null && nickname.contains(ANONYMOUS_NAME)) {
            contentUserProfileAnonymousFieldsSetter.setFieldsForAnonymous(userProfileResponse, nickname);
        }
    }
}
