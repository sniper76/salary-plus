package ag.act.configuration.urlmatcher.postendpoint;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PostIdExtractor {
    private static final Pattern POST_ID_PATTERN = Pattern.compile("posts/(\\d+).*");

    public Optional<Long> findPostIdInUrl(String requestURI) {
        Matcher matcher = POST_ID_PATTERN.matcher(requestURI);

        if (matcher.find()) {
            return Optional.of(Long.valueOf(matcher.group(1)));
        }
        return Optional.empty();
    }
}
