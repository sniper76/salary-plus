package ag.act.service.email;

import ag.act.model.SenderEmailRequest;
import ag.act.module.email.EmailSender;
import ag.act.module.email.SimpleEmailSender;
import ag.act.module.email.builder.RawEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicEmailService {
    private final SimpleEmailSender simpleEmailSender;

    public void sendEmail(SenderEmailRequest senderEmailRequest) {
        final RawEmailRequest rawEmailRequest = RawEmailRequest.of(
            EmailSender.SENDER_NAME,
            senderEmailRequest.getSenderEmail(),
            List.of(senderEmailRequest.getRecipientEmail()),
            senderEmailRequest.getSubject(),
            senderEmailRequest.getContent()
        );

        simpleEmailSender.sendEmail(rawEmailRequest);
    }
}
