package ag.act.module.email.builder;

import java.util.List;
import java.util.Map;

public interface EmailRequest {

    String getSenderName();

    String getSenderEmail();

    List<String> getReplyToAddresses();

    List<String> getToRecipients();

    List<String> getCcRecipients();

    String getSubject();

    String getContent();

    String getTemplateName();

    Map<String, Object> getTemplateData();

    default List<InputStreamDataSource> getInputStreamDataSources() {
        return List.of();
    }
}
