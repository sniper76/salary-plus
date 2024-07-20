package ag.act.module.email.builder;

import ag.act.exception.ActEmailException;
import ag.act.module.email.template.EmailTemplateRenderer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SendEmailRequestBuilder {
    private static final String EMAIL_HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String MULTIPART_ALTERNATIVE_CHILD_CONTAINER_TYPE = "alternative";
    private static final String MULTIPART_MIXED_PARENT_CONTAINER_TYPE = "mixed";
    private final MimeMessageFactory mimeMessageFactory;
    private final EmailTemplateRenderer emailTemplateRenderer;

    public SendRawEmailRequest buildRawEmailRequest(EmailRequest emailRequest) {
        try {
            final MimeMessage message = getMimeMessage(emailRequest);
            final RawMessage rawMessage = toRawMessage(message);

            return SendRawEmailRequest.builder()
                .rawMessage(rawMessage)
                .build();

        } catch (ActEmailException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ActEmailException("Error while sending raw email", ex);
        }
    }

    @NotNull
    private MimeMessage getMimeMessage(EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = mimeMessageFactory.create();

        message.setSubject(emailRequest.getSubject());
        message.setFrom(getFormattedFromEmail(emailRequest));
        message.setRecipients(Message.RecipientType.TO, toInternetAddressRecipients(emailRequest.getToRecipients()));
        message.setRecipients(Message.RecipientType.CC, toInternetAddressRecipients(emailRequest.getCcRecipients()));
        message.setContent(getMessageContent(emailRequest));
        message.setReplyTo(toInternetAddressRecipients(emailRequest.getReplyToAddresses()));

        return message;
    }

    @NotNull
    private InternetAddress[] toInternetAddressRecipients(List<String> recipients) throws AddressException {
        return InternetAddress.parse(String.join(",", recipients));
    }

    private RawMessage toRawMessage(MimeMessage message) throws IOException, MessagingException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);

        return RawMessage.builder()
            .data(SdkBytes.fromByteArray(outputStream.toByteArray()))
            .build();
    }

    private void addBodyPart(MimeBodyPart att, MimeMultipart msg) {
        try {
            // Add the attachment to the message.
            msg.addBodyPart(att);
        } catch (MessagingException ex) {
            throw new ActEmailException("Error while adding attachment", ex);
        }
    }

    private List<MimeBodyPart> getAttachments(EmailRequest emailRequest) {
        return emailRequest.getInputStreamDataSources()
            .stream()
            .map(this::toMimeBodyPart)
            .toList();
    }

    private MimeBodyPart toMimeBodyPart(InputStreamDataSource inputStreamDataSource) throws ActEmailException {
        try {
            MimeBodyPart att = new MimeBodyPart();
            att.setDataHandler(new DataHandler(inputStreamDataSource));
            att.setFileName(inputStreamDataSource.getName());
            return att;
        } catch (MessagingException ex) {
            throw new ActEmailException("Error while creating attachment", ex);
        }
    }

    @NotNull
    private MimeMultipart getMessageContent(EmailRequest emailRequest) throws MessagingException {

        // Define the HTML part.
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(getEmailBody(emailRequest), EMAIL_HTML_CONTENT_TYPE);

        // Create a multipart/alternative child container.
        MimeMultipart msgBody = new MimeMultipart(MULTIPART_ALTERNATIVE_CHILD_CONTAINER_TYPE);
        // Add the text and HTML parts to the child container.
        msgBody.addBodyPart(htmlPart);

        // Create a wrapper for the HTML and text parts.
        MimeBodyPart wrap = new MimeBodyPart();
        // Add the child container to the wrapper object.
        wrap.setContent(msgBody);

        // Create a multipart/mixed parent container.
        MimeMultipart msg = new MimeMultipart(MULTIPART_MIXED_PARENT_CONTAINER_TYPE);

        // Add the multipart/alternative part to the message.
        msg.addBodyPart(wrap);

        getAttachments(emailRequest).forEach(att -> addBodyPart(att, msg));

        return msg;
    }

    public SendEmailRequest build(EmailRequest emailRequest) {
        final SendEmailRequest.Builder sendEmailRequestBuilder = SendEmailRequest.builder()
            .source(getFormattedFromEmail(emailRequest))
            .destination(builder -> builder.toAddresses(emailRequest.getToRecipients()).build())
            .message(messageBuilder ->
                messageBuilder
                    .subject(builder -> builder.data(emailRequest.getSubject()).build())
                    .body(builder -> builder.html(contentBuilder -> contentBuilder.data(emailRequest.getContent()).build()).build())
                    .build()
            );

        if (CollectionUtils.isNotEmpty(emailRequest.getReplyToAddresses())) {
            sendEmailRequestBuilder.replyToAddresses(emailRequest.getReplyToAddresses());
        }

        return sendEmailRequestBuilder.build();
    }

    private String getFormattedFromEmail(EmailRequest emailRequest) {
        return Optional.ofNullable(emailRequest.getSenderName())
            .map(senderName -> "\"%s\" <%s>".formatted(senderName, emailRequest.getSenderEmail()))
            .orElse(emailRequest.getSenderEmail());
    }

    private String getEmailBody(EmailRequest emailRequest) {
        return emailTemplateRenderer.render(emailRequest.getTemplateName(), emailRequest.getTemplateData());
    }
}
