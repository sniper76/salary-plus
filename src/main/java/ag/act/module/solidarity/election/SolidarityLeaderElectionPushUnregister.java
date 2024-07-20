package ag.act.module.solidarity.election;

import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.push.PushService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Transactional
@Component
public class SolidarityLeaderElectionPushUnregister {
    private final PushService pushService;

    public void unregister(SolidarityLeaderElection election) {
        pushService.unregisterAllReadyPushesByPostId(election.getPostId());
    }
}
