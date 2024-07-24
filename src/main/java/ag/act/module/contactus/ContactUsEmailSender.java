package ag.act.module.contactus;

import ag.act.core.aop.htmlcontent.HtmlContentEscaperFactory;
import ag.act.model.ContactUsRequest;
import ag.act.module.email.SimpleEmailSender;
import ag.act.module.email.builder.RawEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactUsEmailSender {
    private static final String SUBJECT = "고객 문의사항";
    private final String contactUsToEmail;
    private final SimpleEmailSender simpleEmailSender;
    private final HtmlContentEscaperFactory htmlContentEscaperFactory;

    public ContactUsEmailSender(
        @Value("${aws.ses.contact-us.to-email}") String contactUsToEmail,
        SimpleEmailSender simpleEmailSender,
        HtmlContentEscaperFactory htmlContentEscaperFactory
    ) {
        this.contactUsToEmail = contactUsToEmail;
        this.simpleEmailSender = simpleEmailSender;
        this.htmlContentEscaperFactory = htmlContentEscaperFactory;
    }

    public void sendEmail(ContactUsRequest contactUsRequest) {
        final RawEmailRequest rawEmailRequest = RawEmailRequest.of(
            escapedText(contactUsRequest.getSenderName()),
            contactUsRequest.getSenderEmail(),
            List.of(contactUsToEmail),
            SUBJECT,
            getContent(contactUsRequest)
        );

        simpleEmailSender.sendEmail(rawEmailRequest);
    }

    private String getContent(ContactUsRequest contactUsRequest) {

        return """
            성명: %s<br/>
            휴대번호: %s<br/>
            문의내용: %s<br/>
            """.formatted(
            escapedText(contactUsRequest.getSenderName()),
            contactUsRequest.getPhoneNumber(),
            escapedText(contactUsRequest.getContent())
        );
    }

    private String escapedText(String content) {
        final ContactUsHtmlContent senderNameHtmlContent = new ContactUsHtmlContent(content);
        htmlContentEscaperFactory.getHtmlContentEscaper(senderNameHtmlContent).escapeContent();
        return senderNameHtmlContent.getContent();
    }
}
