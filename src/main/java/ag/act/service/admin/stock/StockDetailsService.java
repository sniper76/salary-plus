package ag.act.service.admin.stock;


import ag.act.converter.stock.StockDetailsResponseConverter;
import ag.act.dto.admin.StockDetailsResponseDto;
import ag.act.dto.user.SolidarityLeaderApplicantUserDto;
import ag.act.dto.user.SolidarityLeaderUserDto;
import ag.act.entity.Solidarity;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.User;
import ag.act.model.StockDetailsResponse;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserService;
import ag.act.service.solidarity.SolidarityDashboardService;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockDetailsService {
    private static final List<SolidarityLeaderApplicantUserDto> NO_APPLICANTS = List.of();

    private final SolidarityService solidarityService;
    private final StockDetailsResponseConverter stockDetailsResponseConverter;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final UserService userService;
    private final SolidarityDashboardService solidarityDashboardService;
    private final StockAcceptorUserService stockAcceptUserService;

    @Nullable
    private static SolidarityLeaderUserDto getSolidarityLeaderUserDto(Solidarity solidarity, Optional<User> leaderUser) {
        return leaderUser
            .map(user -> new SolidarityLeaderUserDto(solidarity.getSolidarityLeader(), user))
            .orElse(null);
    }

    public StockDetailsResponse getStockDetails(String stockCode) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);
        final Optional<User> leaderUser = getLeaderUser(solidarity);
        final List<SolidarityLeaderApplicantUserDto> applicantUsers = leaderUser.map(leader -> NO_APPLICANTS)
            .orElseGet(() -> getApplicantUserDtoList(solidarity));
        final ag.act.model.DashboardResponse dashboard = solidarityDashboardService.getDashboard(stockCode);
        final SolidarityLeaderUserDto solidarityLeaderUserDto = getSolidarityLeaderUserDto(solidarity, leaderUser);
        final User acceptorUser = getAcceptorUser(stockCode);

        return stockDetailsResponseConverter.convert(
            new StockDetailsResponseDto(
                solidarity,
                solidarityLeaderUserDto,
                acceptorUser,
                applicantUsers,
                dashboard
            )
        );
    }

    @Nullable
    private User getAcceptorUser(String stockCode) {
        return stockAcceptUserService.findByStockCode(stockCode)
            .map(this::getUserNullable)
            .orElse(null);
    }

    @Nullable
    private User getUserNullable(StockAcceptorUser acceptUser) {
        return userService.findUser(acceptUser.getUserId())
            .orElse(null);
    }

    private List<SolidarityLeaderApplicantUserDto> getApplicantUserDtoList(Solidarity solidarity) {
        return solidarityLeaderElectionService.findOnGoingSolidarityLeaderElection(solidarity.getStockCode())
            .map(solidarityLeaderElection ->
                solidarityLeaderApplicantService.getSolidarityLeaderApplicantUsers(solidarityLeaderElection.getId())
            ).orElse(NO_APPLICANTS);
    }

    private Optional<User> getLeaderUser(Solidarity solidarity) {
        return Optional.ofNullable(solidarity.getSolidarityLeader())
            .flatMap(it -> userService.findUser(it.getUserId()));
    }
}
