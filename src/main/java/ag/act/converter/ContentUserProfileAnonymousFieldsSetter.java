package ag.act.converter;

import ag.act.constants.MessageConstants;
import ag.act.entity.ContentUserProfile;
import org.springframework.stereotype.Component;

@Component
public class ContentUserProfileAnonymousFieldsSetter implements MessageConstants {

    public void setFieldsForAnonymous(ContentUserProfile contentUserProfile) {
        setFieldsForAnonymous(contentUserProfile, ANONYMOUS_NAME);
    }

    public void setFieldsForAnonymous(ContentUserProfile contentUserProfile, String nickname) {
        contentUserProfile.setNickname(nickname);
        contentUserProfile.setIndividualStockCountLabel(null);
        contentUserProfile.setTotalAssetLabel(null);
        contentUserProfile.setProfileImageUrl(null);
        contentUserProfile.setIsSolidarityLeader(false);
    }
}
