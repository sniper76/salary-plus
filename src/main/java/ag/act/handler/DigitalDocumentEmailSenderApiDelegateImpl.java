package ag.act.handler;

import ag.act.api.DigitalDocumentEmailSenderApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.SendEmailRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.DigitalDocumentEmailSenderService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class})
public class DigitalDocumentEmailSenderApiDelegateImpl implements DigitalDocumentEmailSenderApiDelegate {
    private final DigitalDocumentEmailSenderService digitalDocumentEmailSenderService;

    @Override
    public ResponseEntity<SimpleStringResponse> sendEmailForDigitalDocument(
        Long digitalDocumentId, SendEmailRequest sendEmailRequest
    ) {
        digitalDocumentEmailSenderService.sendEmailForDigitalDocument(digitalDocumentId, sendEmailRequest);
        return SimpleStringResponseUtil.okResponse();
    }
}
