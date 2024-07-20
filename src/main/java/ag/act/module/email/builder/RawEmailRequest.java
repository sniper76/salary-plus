package ag.act.module.email.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class RawEmailRequest implements EmailRequest {
    private String senderName;
    private String senderEmail;
    private List<String> replyToAddresses;
    private List<String> toRecipients;
    private List<String> ccRecipients;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, Object> templateData;
    private List<InputStreamDataSource> inputStreamDataSources;

    public static RawEmailRequest of(
        String senderName, String senderEmail, List<String> recipientEmail, String subject, String content
    ) {
        return new RawEmailRequest(
            senderName,
            senderEmail,
            List.of(),
            recipientEmail,
            List.of(),
            subject,
            content,
            null,
            null,
            null
        );
    }
}
