package ag.act.service.post;

import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.repository.PostRepository;
import ag.act.service.blockeduser.BlockedUserEnhancer;
import ag.act.util.StatusUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MostRecentThreePostQueryService implements BlockedUserEnhancer {
    private final PostRepository postRepository;
    private final MostRecentThreePostPageableFactory mostRecentThreePostPageableFactory;

    public Page<Post> getBoardPostsByStockCodeAndBoardGroup(
        String stockCode,
        BoardGroup boardGroup,
        List<Long> blockedUserIdList,
        Pageable pageable
    ) {
        return postRepository.findAllByBoardStockCodeAndBoardCategoryInAndStatusInAndUserIdNotIn(
            stockCode,
            boardGroup.getCategories(),
            StatusUtil.getPostStatusesVisibleToUsers(),
            refinedBlockedUserIdList(blockedUserIdList),
            pageable
        );
    }

    public List<Post> getPostsByStockCodeAndBoardGroup(
        String stockCode,
        BoardGroup boardGroup,
        List<Long> blockedUserIdList
    ) {
        return getBoardPostsByStockCodeAndBoardGroup(
            stockCode,
            boardGroup,
            blockedUserIdList,
            getMostRecentThreePostPageable()
        ).getContent();
    }

    public List<Post> getPostsByStockCodeAndBoardCategory(
        String stockCode,
        BoardCategory category
    ) {
        return postRepository.findAllByBoardStockCodeAndBoardCategoryAndStatusIn(
            stockCode,
            category,
            StatusUtil.getPostStatusesVisibleToUsers(),
            getMostRecentThreePostPageable()
        );
    }

    private Pageable getMostRecentThreePostPageable() {
        return mostRecentThreePostPageableFactory.getPageable();
    }
}
