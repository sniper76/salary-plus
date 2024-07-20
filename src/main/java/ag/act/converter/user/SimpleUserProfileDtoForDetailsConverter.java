package ag.act.converter.user;

import ag.act.converter.ContentUserProfileAdminLabelsHider;
import ag.act.core.annotation.ContentOverrider;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.util.badge.StockCountLabelGenerator;
import ag.act.util.badge.TotalAssetLabelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SimpleUserProfileDtoForDetailsConverter {
    private final TotalAssetLabelGenerator totalAssetLabelGenerator;
    private final StockCountLabelGenerator stockCountLabelGenerator;
    private final ContentUserProfileAdminLabelsHider contentUserProfileAdminLabelsHider;

    @ContentOverrider
    public SimpleUserProfileDto convert(
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

    @ContentOverrider
    public SimpleUserProfileDto convert(
        User user,
        String individualStockCountLabel,
        String totalAssetLabel
    ) {
        SimpleUserProfileDto userProfileResponse = SimpleUserProfileDto.builder()
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl())
            .individualStockCountLabel(individualStockCountLabel)
            .totalAssetLabel(totalAssetLabel)
            .build();

        contentUserProfileAdminLabelsHider.hideAdminLabels(user.getId(), userProfileResponse);

        return userProfileResponse;
    }
}
