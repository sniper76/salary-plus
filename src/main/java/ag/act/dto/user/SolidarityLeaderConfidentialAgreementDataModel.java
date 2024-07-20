package ag.act.dto.user;


import ag.act.entity.User;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import lombok.Getter;

@Getter
public class SolidarityLeaderConfidentialAgreementDataModel {
    private String currentDate;
    private String userName;
    private String userBirthDate;

    public SolidarityLeaderConfidentialAgreementDataModel(User user) {
        this.currentDate = getCurrentDate();
        this.userName = user.getName();
        this.userBirthDate = DateTimeUtil.getFormattedDateTime(user.getBirthDate(), DateTimeFormatUtil.yyyy_MM_dd_korean());
    }

    public String getCurrentDate() {
        return KoreanDateTimeUtil.getFormattedCurrentDateTime(DateTimeFormatUtil.yyyy_MM_dd_korean());
    }
}
