package ag.act.dto.user;

import ag.act.entity.ContentUserProfile;
import ag.act.model.SimpleUserProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SimpleUserProfileDto implements ContentUserProfile {

    private String nickname;

    private String profileImageUrl;

    private String individualStockCountLabel;

    private String totalAssetLabel;

    public SimpleUserProfileResponse toResponse() {
        return new SimpleUserProfileResponse()
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .individualStockCountLabel(individualStockCountLabel)
            .totalAssetLabel(totalAssetLabel);
    }
}
