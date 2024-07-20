package ag.act.handler.email;

import ag.act.api.PublicEmailApiDelegate;
import ag.act.core.guard.PublicApiKeyGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.SenderEmailRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.service.email.PublicEmailService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@SuppressWarnings("checkstyle:ParameterName")
@Service
@RequiredArgsConstructor
@UseGuards({PublicApiKeyGuard.class})
public class PublicEmailApiDelegateImpl implements PublicEmailApiDelegate {
    private final PublicEmailService publicEmailService;

    @Override
    public ResponseEntity<SimpleStringResponse> sendEmailInPublic(String xApiKey, SenderEmailRequest senderEmailRequest) {
        publicEmailService.sendEmail(senderEmailRequest);
        return SimpleStringResponseUtil.okResponse();
    }
}
