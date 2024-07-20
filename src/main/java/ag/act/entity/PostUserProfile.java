package ag.act.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_user_profiles")
@Getter
@Setter
public class PostUserProfile implements ContentUserProfile {
    @Id
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "individual_stock_count_label")
    private String individualStockCountLabel;

    @Column(name = "total_asset_label")
    private String totalAssetLabel;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "is_solidarity_leader")
    private Boolean isSolidarityLeader;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public PostUserProfile copyOf(Long postId) {
        PostUserProfile postUserProfile = new PostUserProfile();
        postUserProfile.setPostId(postId);
        postUserProfile.setNickname(nickname);
        postUserProfile.setIndividualStockCountLabel(individualStockCountLabel);
        postUserProfile.setTotalAssetLabel(totalAssetLabel);
        postUserProfile.setProfileImageUrl(profileImageUrl);
        postUserProfile.setUserIp(userIp);
        postUserProfile.setIsSolidarityLeader(isSolidarityLeader);
        return postUserProfile;
    }
}
