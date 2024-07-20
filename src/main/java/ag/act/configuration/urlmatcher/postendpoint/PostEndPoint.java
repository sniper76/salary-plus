package ag.act.configuration.urlmatcher.postendpoint;

import ag.act.enums.BoardCategory;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.function.Predicate;

public record PostEndPoint(
    AntPathRequestMatcher antPathRequestMatcher,
    HttpMethod httpMethod,
    Predicate<BoardCategory> boardCategoryPredicate
) {
    private static final Predicate<BoardCategory> ALL_ALLOWED_BOARD_CATEGORY_PREDICATE = boardCategory -> true;

    public static PostEndPoint of(String url, HttpMethod httpMethod, Predicate<BoardCategory> boardCategoryPredicate) {
        return new PostEndPoint(new AntPathRequestMatcher(url), httpMethod, boardCategoryPredicate);
    }

    public static PostEndPoint of(String url, Predicate<BoardCategory> boardCategoryPredicate) {
        return new PostEndPoint(new AntPathRequestMatcher(url), HttpMethod.GET, boardCategoryPredicate);
    }

    public static PostEndPoint of(String url) {
        return new PostEndPoint(new AntPathRequestMatcher(url), HttpMethod.GET, ALL_ALLOWED_BOARD_CATEGORY_PREDICATE);
    }
}
