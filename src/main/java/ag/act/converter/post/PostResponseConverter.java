package ag.act.converter.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.digitaldocument.DigitalDocumentAcceptUserResponseConverter;
import ag.act.converter.digitaldocument.DigitalDocumentStockResponseConverter;
import ag.act.converter.digitaldocument.DigitalProxyModuSignConverter;
import ag.act.converter.post.poll.PollResponseConverter;
import ag.act.converter.stock.StockResponseConverter;
import ag.act.converter.user.UserProfileResponseForDetailsConverter;
import ag.act.core.annotation.ContentOverrider;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
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
import ag.act.service.post.PostUserProfileService;
import ag.act.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import java.util.Optional;

import static ag.act.converter.post.PostResponseDigitalDocumentConverter.ConvertParameter;

@Component
@RequiredArgsConstructor
public class PostResponseConverter implements Converter<Post, PostResponse> {
    private final BoardCategoryResponseConverter boardCategoryResponseConverter;
    private final StockResponseConverter stockResponseConverter;
    private final PostUserLikeService postUserLikeService;
    private final UserProfileResponseForDetailsConverter userProfileResponseConverter;
    private final PollResponseConverter pollResponseConverter;
    private final UserRoleService userRoleService;
    private final DigitalProxyModuSignConverter digitalProxyModuSignConverter;
    private final PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final DigitalDocumentStockResponseConverter digitalDocumentStockResponseConverter;
    private final DigitalDocumentAcceptUserResponseConverter digitalDocumentAcceptUserResponseConverter;
    private final PostPreference postPreference;
    private final PollAnswerService pollAnswerService;
    private final PostUserProfileService postUserProfileService;

    @ContentOverrider
    public PostResponse convert(Post post) {
        return convert(post, Boolean.FALSE);
    }

    @ContentOverrider
    public PostResponse convert(Post post, Boolean isAdmin) {
        if (post.isDeletedByUser()) {
            return getPostResponse(post);
        }

        return convert(post, isAdmin, null);
    }

    @ContentOverrider
    public PostResponse convert(Post post, Boolean isAdmin, @Nullable User acceptorUser) {
        PostResponse postResponse = getPostResponse(post);

        final Poll poll = post.getFirstPoll();
        final DigitalDocument digitalDocument = post.getDigitalDocument();
        final Stock stock = post.getBoard().getStock();

        return postResponse
            .digitalDocument(getDigitalDocument(post, isAdmin, digitalDocument, stock, acceptorUser))
            .digitalProxy(digitalProxyModuSignConverter.convert(post.getDigitalProxy()))
            .poll(getPollResponse(post, poll, isAdmin));
    }

    private PostResponse getPostResponse(Post post) {
        final Board board = post.getBoard();
        final Stock stock = board.getStock();

        return new PostResponse()
            .userProfile(userProfileResponseConverter.convert(post.getUserId(), post.getPostUserProfile()))
            .boardCategory(boardCategoryResponseConverter.convert(post.getBoard()))
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
            .liked(postUserLikeService.isUserLikedPost(post.getId()))
            .isAuthorAdmin(userRoleService.isAdmin(post.getUserId()))
            .isExclusiveToHolders(post.getIsExclusiveToHolders())
            .activeStartDate(DateTimeConverter.convert(post.getActiveStartDate()))
            .thumbnailImageUrl(post.getThumbnailImageUrl())
            .createdAt(DateTimeConverter.convert(post.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(post.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(post.getDeletedAt()))
            .editedAt(DateTimeConverter.convert(post.getEditedAt()));
    }

    private PollResponse getPollResponse(Post post, Poll poll, boolean isAdmin) {
        if (poll == null) {
            return null;
        }

        if (isAdmin) {
            return pollResponseConverter.convertWithAnswer(
                poll,
                pollAnswerService.getPollItemCountsByPollItemId(poll.getId()),
                pollAnswerService.getAllByPollId(poll.getId()),
                post.getId()
            );
        }

        return pollResponseConverter.convert(poll, post.getId());
    }

    private UserDigitalDocumentResponse getDigitalDocument(
        Post post,
        boolean isAdmin,
        DigitalDocument digitalDocument,
        Stock stock,
        @Nullable User acceptorUser
    ) {

        return postResponseDigitalDocumentConverter.convert(
            ConvertParameter.builder()
                .digitalDocument(digitalDocument)
                .acceptUserResponse(digitalDocumentAcceptUserResponseConverter.convert(acceptorUser))
                .answerStatus(
                    getDigitalDocumentUser(post)
                        .map(DigitalDocumentUser::getDigitalDocumentAnswerStatus)
                        .orElse(null)
                )
                .stockResponse(isAdmin
                    ? getDigitalDocumentStockResponse(stock, digitalDocument)
                    : null
                )
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
