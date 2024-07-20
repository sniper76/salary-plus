package ag.act.service.post.duplication;

import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.NotFoundException;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserProfileService;
import ag.act.service.stock.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@RequiredArgsConstructor
@Service
@Transactional
public class PostDuplicateProcessor {
    private final PostService postService;
    private final PostUserProfileService postUserProfileService;
    private final PostImageService postImageService;
    private final StockService stockService;
    private final NotificationService notificationService;

    public Post copyPost(Post post, PostUserProfile postUserProfile, List<PostImage> postImages, Board newBoard) {
        final Post newPost = postService.savePost(post.copyOf(newBoard.getId()));
        final PostUserProfile newPostUserProfile = postUserProfileService.save(postUserProfile.copyOf(newPost.getId()));

        newPost.setBoard(newBoard);
        newPost.setPostUserProfile(newPostUserProfile);

        duplicatePostImageList(postImages, newPost);
        duplicatePollAndPollItems(post, newPost);
        duplicateDigitalDocument(post, newPost, newBoard.getStockCode());

        return savePostAndNotification(newPost);
    }

    private Post savePostAndNotification(Post newPost) {
        final Post savedPost = postService.savePost(newPost);

        notificationService.createNotification(savedPost);

        return savedPost;
    }

    private List<PostImage> duplicatePostImageList(List<PostImage> postImages, Post newPost) {
        return postImageService.savePostImageList(newPost.getId(), postImages.stream().map(PostImage::getImageId).toList());
    }

    private Post duplicatePollAndPollItems(
        Post post,
        Post newPost
    ) {
        final List<Poll> polls = post.getPolls();
        if (polls == null) {
            return null;
        }

        for (Poll poll : polls) {
            final Poll newPoll = poll.copyOf(newPost);
            newPoll.setPollItemList(
                poll
                    .getPollItemList()
                    .stream()
                    .map(pollItem -> pollItem.copyOf(newPoll))
                    .toList()
            );
            newPost.addPoll(newPoll);
        }

        return newPost;
    }

    private Post duplicateDigitalDocument(
        Post post,
        Post newPost,
        String stockCode
    ) {
        if (post.getDigitalDocument() == null) {
            return null;
        }

        // 만약 기타문서가 아닌 다른 전자문서도 복제해야 하는 경우 기준일자 테이블도 등록해야함
        post.getDigitalDocument().copyOf(newPost, stockCode);

        if (newPost.getDigitalDocument().getType() == DigitalDocumentType.ETC_DOCUMENT) {
            newPost.getDigitalDocument().setCompanyName(findStockName(stockCode));
        }

        return newPost;
    }

    private String findStockName(String stockCode) {
        return stockService.findByCode(stockCode)
            .map(Stock::getName)
            .orElseThrow(() -> new NotFoundException("종목을 찾을 수 없습니다. (%s)".formatted(stockCode)));
    }
}
