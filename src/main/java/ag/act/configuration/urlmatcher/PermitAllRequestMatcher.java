package ag.act.configuration.urlmatcher;

import ag.act.configuration.urlmatcher.postendpoint.BoardCategoryFinder;
import ag.act.configuration.urlmatcher.postendpoint.PostEndPoint;
import ag.act.configuration.urlmatcher.postendpoint.PostIdExtractor;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.core.holder.RequestContextHolder;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class PermitAllRequestMatcher implements RequestMatcher {
    private final List<AntPathRequestMatcher> allowedAnyMethodPathRequestMatchers;
    private final List<PostEndPoint> postEndPointList;
    private final BoardCategoryFinder boardCategoryFinder;
    private final PostIdExtractor postIdExtractor;

    public PermitAllRequestMatcher(
        AntPathRequestMatcher[] permitAllPathRequestMatchers,
        final GlobalBoardManager globalBoardManager,
        BoardCategoryFinder boardCategoryFinder,
        PostIdExtractor postIdExtractor
    ) {
        this.allowedAnyMethodPathRequestMatchers = Arrays.asList(permitAllPathRequestMatchers);
        this.boardCategoryFinder = boardCategoryFinder;
        this.postIdExtractor = postIdExtractor;

        final String globalStockCode = globalBoardManager.getStockCode();

        postEndPointList = List.of(
            // 종목 랭킹 조회
            PostEndPoint.of("/api/stock-rankings"),

            // 글로벌 카테고리 목록 조회
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/categories".formatted(globalStockCode, BoardGroup.GLOBALBOARD.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/categories".formatted(globalStockCode, BoardGroup.GLOBALEVENT.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/categories".formatted(globalStockCode, BoardGroup.GLOBALCOMMUNITY.name())),

            // 글로벌 및 베스트 - 게시글 목록 조회
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts".formatted(globalStockCode, BoardGroup.GLOBALBOARD.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts".formatted(globalStockCode, BoardGroup.GLOBALEVENT.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts".formatted(globalStockCode, BoardGroup.GLOBALCOMMUNITY.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts".formatted(globalStockCode, VirtualBoardGroup.ACT_BEST.name())),

            // 글로벌 및 베스트 - 게시글 상세, 댓글, 답글 조회
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts/**".formatted(globalStockCode, BoardGroup.GLOBALBOARD.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts/**".formatted(globalStockCode, BoardGroup.GLOBALEVENT.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts/**".formatted(globalStockCode, BoardGroup.GLOBALCOMMUNITY.name())),
            PostEndPoint.of("/api/stocks/%s/board-groups/%s/posts/**".formatted(globalStockCode, VirtualBoardGroup.ACT_BEST.name())),

            // 종목 - 게시글 상세, 댓글, 답글 조회
            PostEndPoint.of("/api/stocks/*/board-groups/%s/posts/**"
                .formatted(BoardGroup.DEBATE.name()), boardCategory -> BoardCategory.DEBATE == boardCategory),
            PostEndPoint.of("/api/stocks/*/board-groups/%s/posts/**"
                .formatted(BoardGroup.ANALYSIS.name()), boardCategory -> BoardCategory.SOLIDARITY_LEADER_LETTERS == boardCategory)
        );
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (isMatchedInWhiteList(request, allowedAnyMethodPathRequestMatchers)) {
            return true;
        }

        if (!RequestContextHolder.isWebAppVersion()) {
            return false;
        }

        return isMatchedInWhitePostEndPoint(request, postEndPointList);
    }

    private boolean isMatchedInWhitePostEndPoint(HttpServletRequest request, List<PostEndPoint> postEndPointList) {
        return postEndPointList.stream()
            .anyMatch(postEndPoint -> {
                return postEndPoint.antPathRequestMatcher().matches(request)
                       && postEndPoint.httpMethod().matches(request.getMethod())
                       && postEndPoint.boardCategoryPredicate().test(findBoardCategory(request).orElse(null));
            });
    }

    private boolean isMatchedInWhiteList(final HttpServletRequest request, List<AntPathRequestMatcher> allowedPathRequestMatchers) {
        return allowedPathRequestMatchers.stream()
            .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
    }

    private Optional<BoardCategory> findBoardCategory(final HttpServletRequest request) {
        return postIdExtractor.findPostIdInUrl(request.getRequestURI())
            .map(boardCategoryFinder::findBoardCategory);
    }
}