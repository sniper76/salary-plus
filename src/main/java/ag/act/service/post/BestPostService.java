package ag.act.service.post;

import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.model.Status;
import ag.act.repository.PostRepository;
import ag.act.service.blockeduser.BlockedUserEnhancer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BestPostService implements BlockedUserEnhancer {

    private static final long BEST_POST_LIKE_COUNT_CRITERIA = 10L;

    private final PostRepository postRepository;

    public Page<Post> getBestPostsForOnlyExclusiveToHolders(
        List<BoardCategory> categories,
        List<Status> statusList,
        List<String> userHoldingStockCodeList,
        List<Long> blockedUserIdList,
        Pageable pageable
    ) {
        return postRepository.getBestPostsForOnlyExclusiveToHolders(
            categories,
            statusList,
            userHoldingStockCodeList,
            refinedBlockedUserIdList(blockedUserIdList),
            BEST_POST_LIKE_COUNT_CRITERIA,
            pageable
        );
    }

    public Page<Post> getBestPostsForOnlyExclusiveToPublic(
        List<BoardCategory> categories,
        List<Status> statusList,
        List<Long> blockedUserIdList,
        Pageable pageable
    ) {
        return postRepository.findAllByBoardCategoryInAndStatusInAndUserIdNotInAndLikeCountGreaterThanEqualAndIsExclusiveToHolders(
            categories,
            statusList,
            refinedBlockedUserIdList(blockedUserIdList),
            BEST_POST_LIKE_COUNT_CRITERIA,
            false,
            pageable
        );
    }

    public Page<Post> getBestPosts(
        List<BoardCategory> categories,
        List<Status> statusList,
        List<Long> blockedUserIdList,
        Pageable pageable
    ) {
        return postRepository.findAllByBoardCategoryInAndStatusInAndUserIdNotInAndLikeCountGreaterThanEqual(
            categories,
            statusList,
            refinedBlockedUserIdList(blockedUserIdList),
            BEST_POST_LIKE_COUNT_CRITERIA,
            pageable
        );
    }
}
