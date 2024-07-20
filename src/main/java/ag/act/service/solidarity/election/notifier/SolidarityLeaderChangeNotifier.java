package ag.act.service.solidarity.election.notifier;

import ag.act.enums.SlackChannel;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolidarityLeaderChangeNotifier {

    private final SlackMessageSender slackMessageSender;
    private static final String ELECT_LEADER = "[%s] 주주연대 대표(%s)가 %s에 의해 선정되었습니다.";
    private static final String DISMISS_LEADER = "[%s] 주주연대 대표(%s)가 %s에 의해 해임되었습니다.";
    private static final String UPDATE_LEADER_MESSAGE = "[%s] 주주연대 대표(%s)\n나도 한마디 수정\n==========\n%s";

    public void notifyElectedLeader(String stockName, String leaderName, String updatedBy) {
        sendSlackMessage(ELECT_LEADER.formatted(stockName, leaderName, updatedBy));
    }

    public void notifyDismissedLeader(String stockName, String leaderName, String updatedBy) {
        sendSlackMessage(DISMISS_LEADER.formatted(stockName, leaderName, updatedBy));
    }

    public void notifyUpdatedLeaderMessage(String stockName, String leaderName, String newMessage) {
        sendSlackMessage(UPDATE_LEADER_MESSAGE.formatted(stockName, leaderName, newMessage));
    }

    private void sendSlackMessage(String message) {
        slackMessageSender.sendSlackMessage(message, SlackChannel.ACT_SOLIDARITY_LEADER_ALERT);
    }

}
