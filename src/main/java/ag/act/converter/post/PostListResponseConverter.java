package ag.act.converter.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.digitaldocument.DigitalDocumentStockResponseConverter;
import ag.act.converter.digitaldocument.DigitalProxyModuSignConverter;
import ag.act.converter.stock.StockResponseConverter;
import ag.act.converter.user.UserProfileResponseForListConverter;
import ag.act.core.annotation.ContentOverrider;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.model.DigitalDocumentStockResponse;
import ag.act.model.PollResponse;
import ag.act.model.PostResponse;
import ag.act.model.Status;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.module.cache.PostPreference;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.post.PostUserLikeService;
import ag.act.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ag.act.converter.post.PostResponseDigitalDocumentConverter.ConvertParameter;

@Component
@RequiredArgsConstructor
public class PostListResponseConverter implements Converter<Post, PostResponse> {
    private final BoardCategoryResponseConverter boardCategoryResponseConverter;
    private final StockResponseConverter stockResponseConverter;
    private final PostUserLikeService postUserLikeService;
    private final UserProfileResponseForListConverter userProfileResponseForListConverter;
    private final UserRoleService userRoleService;
    private final DigitalProxyModuSignConverter digitalProxyModuSignConverter;
    private final PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final DigitalDocumentStockResponseConverter digitalDocumentStockResponseConverter;
    private final PostPreference postPreference;
    private final PollAnswerService pollAnswerService;

    @ContentOverrider
    public PostResponse convert(Post post) {
        if (post.isDeletedByUser()) {
            return getPostResponse(post);
        }

        PostResponse postResponse = getPostResponse(post);

        final DigitalDocument digitalDocument = post.getDigitalDocument();

        return postResponse
            .digitalDocument(getDigitalDocument(post, digitalDocument))
            .digitalProxy(digitalProxyModuSignConverter.convert(post.getDigitalProxy()))
            .polls(getPollResponse(post));
    }

    private PostResponse getPostResponse(Post post) {
        final Board board = post.getBoard();
        final Stock stock = board.getStock();

        return new PostResponse()
            .userProfile(userProfileResponseForListConverter.convertUserProfile(post.getUserId(), post.getPostUserProfile()))
            .boardCategory(boardCategoryResponseConverter.convert(board))
            .boardGroup(board.getGroup().name())
            .poll(null)
            .digitalProxy(null)
            .digitalDocument(null)
            .stock(stockResponseConverter.convert(stock))
            .id(post.getId())
            .title(post.getTitle())
            .boardId(post.getBoardId())
            .content(post.getContent())
            .status(post.getStatus())
            .userId(post.getUserId())
            .likeCount(post.getLikeCount())
            .commentCount(post.getCommentCount())
            .viewCount(post.getViewCount())
            .isActive(Status.ACTIVE == post.getStatus())
            .isNew(postPreference.isNew(post))
            .isExclusiveToHolders(post.getIsExclusiveToHolders())
            .isPinned(post.getIsPinned())
            .activeStartDate(DateTimeConverter.convert(post.getActiveStartDate()))
            .activeEndDate(DateTimeConverter.convert(post.getActiveEndDate()))
            .liked(postUserLikeService.isUserLikedPost(post.getId()))
            .isAuthorAdmin(userRoleService.isAdmin(post.getUserId()))
            .thumbnailImageUrl(post.getThumbnailImageUrl())
            .createdAt(DateTimeConverter.convert(post.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(post.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(post.getDeletedAt()))
            .editedAt(DateTimeConverter.convert(post.getEditedAt()));
    }

    private List<PollResponse> getPollResponse(Post post) {
        final List<Poll> polls = post.getPolls();
        if (CollectionUtils.isEmpty(polls)) {
            return null;
        }
        final Poll poll = post.getFirstPoll();
        final List<PollAnswer> answerList = pollAnswerService.getPollAnswersByPostId(post.getId());

        final Map<Long, List<PollAnswer>> answerUserMap = answerList
            .stream()
            .collect(Collectors.groupingBy(PollAnswer::getUserId));

        // 멀티 설문, 중복 답변 상관없이 사용자 하나만 조회해서 계산한다. 2024.03.21
        long stockQuantity = answerUserMap.values().stream()
            .mapToLong(value -> value.get(0).getStockQuantity())
            .sum();

        return List.of(
            new PollResponse()
                .id(poll.getId())
                .postId(post.getId())
                .title(poll.getTitle())
                .status(poll.getStatus().name())
                .voteType(poll.getVoteType().name())
                .selectionOption(poll.getSelectionOption().name())
                .targetEndDate(DateTimeConverter.convert(poll.getTargetEndDate()))
                .targetStartDate(DateTimeConverter.convert(poll.getTargetStartDate()))
                .voteTotalCount(answerUserMap.size())
                .voteTotalStockSum(stockQuantity)
                .createdAt(DateTimeConverter.convert(poll.getCreatedAt()))
                .updatedAt(DateTimeConverter.convert(poll.getUpdatedAt()))
        );
    }

    private UserDigitalDocumentResponse getDigitalDocument(
        Post post,
        DigitalDocument digitalDocument
    ) {

        return postResponseDigitalDocumentConverter.convert(
            ConvertParameter.builder()
                .digitalDocument(digitalDocument)
                .acceptUserResponse(null)
                .answerStatus(
                    getDigitalDocumentUser(post)
                        .map(DigitalDocumentUser::getDigitalDocumentAnswerStatus)
                        .orElse(null)
                )
                .stockResponse(getDigitalDocumentStockResponse(post.getBoard().getStock(), digitalDocument))
                .build()
        );
    }

    private DigitalDocumentStockResponse getDigitalDocumentStockResponse(Stock stock, DigitalDocument digitalDocument) {
        if (digitalDocument == null) {
            return null;
        }

        return digitalDocumentStockResponseConverter.convert(stock, digitalDocument);
    }

    private Optional<DigitalDocumentUser> getDigitalDocumentUser(Post post) {
        if (post.getDigitalDocument() == null) {
            return Optional.empty();
        }

        return digitalDocumentUserService.findByDigitalDocumentIdAndUserId(
            post.getDigitalDocument().getId(),
            ActUserProvider.getNoneNull().getId()
        );
    }

    @Override
    public PostResponse apply(Post post) {
        return convert(post);
    }
}
