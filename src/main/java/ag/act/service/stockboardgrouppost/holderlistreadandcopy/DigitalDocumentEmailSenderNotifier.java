package ag.act.service.stockboardgrouppost.holderlistreadandcopy;

import ag.act.enums.SlackChannel;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalDocumentEmailSenderNotifier {

    private static final String MESSAGE_TEMPLATE = "주주명부 열람/등사 메일 발송중 오류가 발생하었습니다. id: %s";

    private final SlackMessageSender slackMessageSender;

    public void notify(long digitalDocumentId) {
        sendSlackMessage(MESSAGE_TEMPLATE.formatted(digitalDocumentId));
    }

    private void sendSlackMessage(String message) {
        slackMessageSender.sendSlackMessage(message, SlackChannel.ACT_EMAIL_ALERT);
    }
}
