package ag.act.module.email.builder;

import org.springframework.stereotype.Component;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

@Component
public class MimeMessageFactory {

    public MimeMessage create() {
        return new MimeMessage(Session.getDefaultInstance(System.getProperties()));
    }
}
