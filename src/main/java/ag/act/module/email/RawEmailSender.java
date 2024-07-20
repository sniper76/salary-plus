package ag.act.module.email;

import ag.act.module.email.builder.EmailRequest;
import ag.act.module.email.builder.SendEmailRequestBuilder;
import ag.act.service.aws.SesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.model.SesResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawEmailSender implements EmailSender {

    private final SesService sesService;
    private final SendEmailRequestBuilder sendEmailRequestBuilder;

    public SesResponse sendEmail(EmailRequest emailRequest) {
        log.info("Email request: {}", emailRequest);

        return sesService.sendRawEmail(
            sendEmailRequestBuilder.buildRawEmailRequest(emailRequest)
        );
    }
}
