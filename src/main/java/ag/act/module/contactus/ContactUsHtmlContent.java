package ag.act.module.contactus;

import ag.act.dto.HtmlContent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactUsHtmlContent implements HtmlContent {

    private String content;

    public ContactUsHtmlContent(String content) {
        this.content = content;
    }
}
