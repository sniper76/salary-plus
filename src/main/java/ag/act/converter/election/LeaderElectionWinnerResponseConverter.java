package ag.act.converter.election;

import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.LeaderElectionWinnerResponse;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LeaderElectionWinnerResponseConverter {

    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final UserService userService;
    private Boolean isElectedByAdmin = Boolean.FALSE;

    public LeaderElectionWinnerResponse convert(SolidarityLeaderElection election) {
        if (!election.isFinishedElection()) {
            return null;
        }

        return getResponse(election);
    }

    private LeaderElectionWinnerResponse getResponse(SolidarityLeaderElection election) {
        if (election.isFinishedOnTimeWithNoWinnerElection()) {
            return getResponseWithoutWinner();
        }

        if (election.isFinishedByAdminElection()) {
            isElectedByAdmin = Boolean.TRUE;
        }

        return Optional.ofNullable(election.getWinnerApplicantId())
            .flatMap(solidarityLeaderApplicantService::findSolidarityLeaderApplicant)
            .map(SolidarityLeaderApplicant::getUserId)
            .flatMap(userService::findUser)
            .map(this::getResponseWithWinner)
            .orElseGet(this::getResponseWithoutWinner);
    }

    private LeaderElectionWinnerResponse getResponseWithWinner(User user) {
        return new LeaderElectionWinnerResponse()
            .isElectedByAdmin(isElectedByAdmin)
            .isElected(Boolean.TRUE)
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl());
    }

    private LeaderElectionWinnerResponse getResponseWithoutWinner() {
        return new LeaderElectionWinnerResponse()
            .isElectedByAdmin(isElectedByAdmin)
            .isElected(Boolean.FALSE);
    }
}
