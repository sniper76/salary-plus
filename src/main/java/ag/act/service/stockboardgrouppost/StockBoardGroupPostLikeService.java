package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Post;
import ag.act.entity.PostUserLike;
import ag.act.exception.BadRequestException;
import ag.act.model.PostDataResponse;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserLikeService;
import ag.act.service.push.AutomatedAuthorPushService;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockBoardGroupPostLikeService {
    private final PostService postService;
    private final PostUserLikeService postUserLikeService;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final AutomatedAuthorPushService automatedAuthorPushService;
    private final StockBoardGroupPostService stockBoardGroupPostService;

    public PostDataResponse likeBoardGroupPost(
        String stockCode,
        String boardGroupName,
        Long postId,
        boolean likeFlag
    ) {

        final Post post = stockBoardGroupPostValidator.validateBoardGroupPost(
            postId, stockCode, boardGroupName, StatusUtil.getDeleteStatuses()
        );

        final Long likedUserId = ActUserProvider.getNoneNull().getId();

        //before user like table delete
        postUserLikeService.deletePostUserLikeList(postUserLikeService.findAllByPostIdAndUserId(postId, likedUserId));

        if (likeFlag) {
            savePostUserLike(postId, likedUserId);
        }

        final long likeCount = postUserLikeService.countByPostId(postId);
        updatePostLikeCount(post, likeCount);
        automatedAuthorPushService.createAutomatedAuthorPushForPostUserLike(post, likeCount);

        return stockBoardGroupPostService.getBoardGroupPostDetail(post);
    }

    private void updatePostLikeCount(Post post, long count) {
        post.setLikeCount(count);
        postService.savePost(post);
    }

    private void savePostUserLike(Long postId, Long likedUserId) {
        PostUserLike postUserLike = new PostUserLike();
        postUserLike.setPostId(postId);
        postUserLike.setUserId(likedUserId);

        //new post user like insert
        try {
            postUserLikeService.savePostUserLike(postUserLike);
        } catch (Exception e) {
            throw new BadRequestException("좋아요 등록중 오류가 발생하였습니다.", e);
        }
    }
}
