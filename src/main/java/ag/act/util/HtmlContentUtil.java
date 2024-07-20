package ag.act.util;

import org.apache.commons.lang3.StringUtils;

public class HtmlContentUtil {

    public static String unescape(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }

        return content
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("\n", "")
            .trim();
    }
}
