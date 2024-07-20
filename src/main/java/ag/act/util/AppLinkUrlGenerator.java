package ag.act.util;

import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import org.springframework.stereotype.Component;

@Component
public class AppLinkUrlGenerator {
    private static final String STOCK_URL_FORMAT = "/stock/%s";
    private static final String BOARD_GROUP_URL_FORMAT = STOCK_URL_FORMAT + "/%s";
    private static final String BOARD_CATEGORY_URL_FORMAT = BOARD_GROUP_URL_FORMAT + "?boardCategory=%s";
    private static final String POST_URL_FORMAT = STOCK_URL_FORMAT + "/board/%s/post/%d";

    public String generateStockHomeLinkUrl(String stockCode) {
        return String.format(STOCK_URL_FORMAT, stockCode);
    }

    // ex) /stock/005930/action
    public String generateBoardGroupLinkUrl(String stockCode, BoardGroup boardGroup) {
        return String.format(BOARD_GROUP_URL_FORMAT, stockCode, boardGroup.name().toLowerCase());
    }

    // ex) /stock/005930/action?boardCategory=digital_delegation
    public String generateBoardGroupCategoryLinkUrl(String stockCode, BoardCategory boardCategory) {
        return String.format(
            BOARD_CATEGORY_URL_FORMAT,
            stockCode,
            boardCategory.getBoardGroup().name().toLowerCase(),
            boardCategory.name().toLowerCase()
        );
    }

    // ex) /stock/005930/board/action/post/123
    public String generateBoardGroupPostLinkUrl(String stockCode, String groupName, Long postId) {
        return String.format(POST_URL_FORMAT, stockCode, groupName.toLowerCase(), postId);
    }

    public String generateBoardGroupPostLinkUrl(Post post) {
        return generateBoardGroupPostLinkUrl(
            post.getBoard().getStockCode(),
            post.getBoard().getGroup().name(),
            post.getId()
        );
    }


}
