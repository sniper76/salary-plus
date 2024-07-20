package ag.act.handler;

import ag.act.api.SlackMessageApiDelegate;
import ag.act.enums.SlackChannel;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMessageApiDelegateImpl implements SlackMessageApiDelegate {

    private final SlackMessageSender slackMessageSender;

    @Override
    public ResponseEntity<SimpleStringResponse> sendSlackMessage(ag.act.model.SlackMessageRequest slackMessageRequest) {
        slackMessageSender.sendSlackMessage(slackMessageRequest.getMessage(), SlackChannel.fromValue(slackMessageRequest.getChannel()));
        return SimpleStringResponseUtil.okResponse();
    }

}
