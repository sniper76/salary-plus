package ag.act.service.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse;

@Service
@RequiredArgsConstructor
public class SesService {

    private final SesClient sesClient;

    public SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
        return sesClient.sendEmail(sendEmailRequest);
    }

    public SendRawEmailResponse sendRawEmail(SendRawEmailRequest sendRawEmailRequest) {
        return sesClient.sendRawEmail(sendRawEmailRequest);
    }
}


