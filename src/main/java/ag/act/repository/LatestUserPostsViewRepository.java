package ag.act.repository;

import ag.act.entity.LatestUserPostsView;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LatestUserPostsViewRepository extends JpaRepository<LatestUserPostsView, Long> {
    Optional<LatestUserPostsView> findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
        String stockCode,
        Long userId,
        BoardGroup boardGroup,
        BoardCategory boardCategory,
        PostsViewType postsViewType
    );
}
