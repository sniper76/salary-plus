package ag.act.service.solidarity.election.notifier;

import ag.act.enums.SlackChannel;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolidarityLeaderApplicantChangeNotifier {

    private final SlackMessageSender slackMessageSender;
    private static final String APPLY_LEADER = "[%s] 주주연대에 [%s]님이 주주대표로 지원하였습니다.";

    public void notifyAppliedLeader(String stockName, String userName) {
        sendSlackMessage(APPLY_LEADER.formatted(stockName, userName));
    }

    private void sendSlackMessage(String message) {
        slackMessageSender.sendSlackMessage(message, SlackChannel.ACT_SOLIDARITY_LEADER_APPLICANT_ALERT);
    }
}
