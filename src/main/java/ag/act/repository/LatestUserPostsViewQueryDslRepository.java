package ag.act.repository;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;

import java.util.List;

public interface LatestUserPostsViewQueryDslRepository {
    Boolean existsUnreadPost(
        List<String> stockCodes,
        Long userId,
        BoardGroup boardGroup
    );

    Boolean existsUnreadPost(
        List<String> stockCodes,
        Long userId,
        BoardGroup boardGroup,
        BoardCategory boardCategory
    );
}
