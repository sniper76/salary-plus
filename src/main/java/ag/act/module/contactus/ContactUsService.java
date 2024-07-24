package ag.act.module.contactus;

import ag.act.exception.BadRequestException;
import ag.act.model.ContactUsRequest;
import ag.act.service.recaptcha.RecaptchaVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ContactUsService {
    private final ContactUsEmailSender contactUsEmailSender;
    private final RecaptchaVerifier recaptchaVerifier;


    public void contactUs(ContactUsRequest contactUsRequest) {
        boolean isRecaptchaValid = recaptchaVerifier.verifyCaptcha(contactUsRequest.getRecaptchaResponse());
        if (!isRecaptchaValid) {
            throw new BadRequestException("잘못된 Captcha 인증 요청입니다.");
        }

        contactUsEmailSender.sendEmail(contactUsRequest);
    }
}
