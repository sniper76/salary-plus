package ag.act.module.markany.dna;

import ag.act.enums.SlackChannel;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkAnyNotifier {

    private static final String DIGITAL_DOCUMENT_ERROR_MESSAGE = "[MarkAnyDNA] docId:(%s) userId:(%s) issuedNumber:(%s) errorMessage:%s %s";
    private static final String CONFIDENTIAL_AGREEMENT_ERROR_MESSAGE = "[MarkAnyDNA] userId:(%s) errorMessage:%s %s";

    private final SlackMessageSender slackMessageSender;

    public void notify(MarkAnyDigitalDocument markAnyDigitalDocument, Exception exception) {
        sendSlackMessage(
            DIGITAL_DOCUMENT_ERROR_MESSAGE.formatted(
                markAnyDigitalDocument.documentId(),
                markAnyDigitalDocument.userId(),
                markAnyDigitalDocument.issuedNumber(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
            )
        );
    }

    public void notify(MarkAnyConfidentialAgreement markAnyConfidentialAgreement, Exception exception) {
        sendSlackMessage(
            CONFIDENTIAL_AGREEMENT_ERROR_MESSAGE.formatted(
                markAnyConfidentialAgreement.userId(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
            )
        );
    }

    private void sendSlackMessage(String message) {
        slackMessageSender.sendSlackMessage(message, SlackChannel.ACT_MARKANY_ALERT);
    }
}
