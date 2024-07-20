package ag.act.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record SolidarityLeaderApplicantDto(
    @Nonnull Long solidarityApplicantId,
    @Nonnull Long userId,
    @Nonnull String nickname,
    @Nullable String profileImageUrl,
    @Nonnull Long stockQuantity,
    @Nonnull String commentsForStockHolder
) {
}
