package ag.act.entity;

public interface ContentUserProfile extends ActEntity {
    default String getNickname() {
        return null;
    }

    default void setNickname(String nickname) {
    }

    default String getIndividualStockCountLabel() {
        return null;
    }

    default void setIndividualStockCountLabel(String individualStockCountLabel) {
    }

    default String getTotalAssetLabel() {
        return null;
    }

    default void setTotalAssetLabel(String totalAssetLabel) {
    }

    default String getProfileImageUrl() {
        return null;
    }

    default void setProfileImageUrl(String profileImageUrl) {
    }

    default String getUserIp() {
        return null;
    }

    default void setUserIp(String userIp) {
    }

    default Boolean getIsSolidarityLeader() {
        return null;
    }

    default void setIsSolidarityLeader(Boolean isSolidarityLeader) {
    }

}
