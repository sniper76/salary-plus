package ag.act.itutil.dto;

import ag.act.entity.PostUserProfile;

@SuppressWarnings("LombokSetterMayBeUsed")
public class PostUserProfileDto {

    private Long postId;
    private final String profileImageUrl;
    private final String nickname;
    private final String totalAssetLabel;
    private final String individualStockCountLabel;

    public PostUserProfileDto(Long postId, String profileImageUrl, String nickname, String totalAssetLabel, String individualStockCountLabel) {
        this.postId = postId;
        this.profileImageUrl = profileImageUrl;
        this.nickname = nickname;
        this.totalAssetLabel = totalAssetLabel;
        this.individualStockCountLabel = individualStockCountLabel;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public PostUserProfile toEntity() {
        PostUserProfile postUserProfile = new PostUserProfile();
        postUserProfile.setPostId(postId);
        postUserProfile.setProfileImageUrl(profileImageUrl);
        postUserProfile.setNickname(nickname);
        postUserProfile.setTotalAssetLabel(totalAssetLabel);
        postUserProfile.setIndividualStockCountLabel(individualStockCountLabel);
        return postUserProfile;
    }
}
