package ag.act.core.aop.htmlcontent;

import ag.act.dto.HtmlContent;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlContentEscaper {
    private static final String URL_PATTERN = "http[s]?://[^\\s<>\"]+";
    private final String urlPlaceholder = generateUrlPlaceholder();
    private final List<String> urls = new ArrayList<>();
    private final HtmlContent htmlContent;

    public HtmlContentEscaper(final HtmlContent htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String escapeContent() {
        if (htmlContent.getIsEd()) {
            return toSafeHtml();
        } else {
            return toEscapedSafeHtml();
        }
    }

    private String toEscapedSafeHtml() {
        String tempContent = convertUrls();
        tempContent = getEscapedContent(tempContent);
        tempContent = toSafeHtml(tempContent);
        tempContent = restoreUrls(tempContent);

        return tempContent;
    }

    private String restoreUrls(String tempContent) {
        for (int i = 0; i < urls.size(); i++) {
            tempContent = tempContent.replace(urlPlaceholder + i, urls.get(i));
        }
        return tempContent;
    }

    private String convertUrls() {
        String tempContent = htmlContent.getContent();
        final Pattern urlPattern = Pattern.compile(URL_PATTERN);
        final Matcher matcher = urlPattern.matcher(tempContent);

        int index = 0;
        while (matcher.find()) {
            urls.add(matcher.group());
            tempContent = tempContent.replace(matcher.group(), urlPlaceholder + index++);
        }

        return tempContent;
    }

    private String generateUrlPlaceholder() {
        return "URL_PLACEHOLDER_" + new Random(System.currentTimeMillis()).nextInt(100000) + "_";
    }

    private String getEscapedContent(final String content) {
        return StringEscapeUtils.escapeHtml3(content)
            .replace("&quot;", "\"")
            .replaceAll(" {2}", "&nbsp; ");
    }

    private String toSafeHtml(String htmlContent) {
        return htmlContent.replaceAll("(?i)<script[^>]*>[\\s\\S]*?</script>", "");
    }

    private String toSafeHtml() {
        return toSafeHtml(htmlContent.getContent());
    }
}
