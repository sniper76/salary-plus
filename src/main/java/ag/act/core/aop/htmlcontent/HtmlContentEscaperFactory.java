package ag.act.core.aop.htmlcontent;

import ag.act.dto.HtmlContent;
import org.springframework.stereotype.Component;

@Component
public class HtmlContentEscaperFactory {
    public HtmlContentEscaper getHtmlContentEscaper(final HtmlContent htmlContent) {
        return new HtmlContentEscaper(htmlContent);
    }
}
