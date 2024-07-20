package ag.act.handler.admin;

import ag.act.api.SolidarityLeaderElectionPollServiceApiDelegate;
import ag.act.entity.Post;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.exception.BadRequestException;
import ag.act.model.SimpleStringResponse;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.service.stockboardgrouppost.SolidarityLeaderElectionPostPollService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminSolidarityLeaderElectionPollServiceApiDelegateImpl implements SolidarityLeaderElectionPollServiceApiDelegate {
    private final SolidarityLeaderElectionPostPollService solidarityLeaderElectionPostPollService;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;

    @Override
    public ResponseEntity<SimpleStringResponse> createElectionPoll(Long solidarityLeaderElectionId) {
        final SolidarityLeaderElection solidarityLeaderElection = solidarityLeaderElectionService
            .findSolidarityLeaderElection(solidarityLeaderElectionId)
            .orElseThrow(() -> new BadRequestException("주주대표 선출 정보를 찾을 수 없습니다."));
        final Post savedPost = solidarityLeaderElectionPostPollService.createBoardGroupPost(solidarityLeaderElection);
        return ResponseEntity.ok(SimpleStringResponseUtil.ok(savedPost.getId().toString()));
    }
}


