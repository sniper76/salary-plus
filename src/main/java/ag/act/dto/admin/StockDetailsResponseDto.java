package ag.act.dto.admin;

import ag.act.dto.user.SolidarityLeaderApplicantUserDto;
import ag.act.dto.user.SolidarityLeaderUserDto;
import ag.act.entity.Solidarity;
import ag.act.entity.User;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public record StockDetailsResponseDto(
    @Nonnull Solidarity solidarity,
    @Nullable SolidarityLeaderUserDto leader,
    @Nullable User acceptUser,
    @Nonnull List<SolidarityLeaderApplicantUserDto> applicants,
    @Nonnull ag.act.model.DashboardResponse todayDelta
) {
}
