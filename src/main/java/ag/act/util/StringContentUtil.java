package ag.act.util;

import org.apache.commons.lang3.StringUtils;

public class StringContentUtil {

    public static String removeSpecialCharacters(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }

        return content
            .replaceAll("['\"\n]", "")
            .replaceAll("\\s+", " ")
            .trim();
    }
}
