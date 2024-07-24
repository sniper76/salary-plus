package ag.act.module.contactus;

import ag.act.api.ContactUsApiDelegate;
import ag.act.core.guard.IsPublicWebGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.ContactUsRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UseGuards(IsPublicWebGuard.class)
public class ContactUsApiDelegateImpl implements ContactUsApiDelegate {
    private final ContactUsService contactUsService;

    @Override
    public ResponseEntity<SimpleStringResponse> contactUs(ContactUsRequest contactUsRequest) {
        contactUsService.contactUs(contactUsRequest);
        return SimpleStringResponseUtil.okResponse();
    }
}
