package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.constants.MessageConstants;
import ag.act.converter.ContentUserProfileConverter;
import ag.act.converter.PageDataConverter;
import ag.act.converter.post.comment.CommentPagedResponseConverter;
import ag.act.converter.post.comment.CommentResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.post.comment.GetCommentRequestDto;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserLike;
import ag.act.entity.CommentUserProfile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.facade.user.UserFacade;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CommentResponse;
import ag.act.model.CreateCommentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.model.UpdateCommentRequest;
import ag.act.service.post.PostService;
import ag.act.service.push.AutomatedAuthorPushService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.service.stockboardgrouppost.comment.CommentUserLikeService;
import ag.act.service.stockboardgrouppost.comment.CommentUserProfileService;
import ag.act.service.user.UserAnonymousCountService;
import ag.act.service.user.UserRoleService;
import ag.act.validator.post.StockBoardGroupPostCommentValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class StockBoardGroupPostCommentService implements MessageConstants {

    public static final long NULL_PARENT_COMMENT_ID = 0L;
    private static final int REPLY_DEFAULT_SIZE = 20;

    private final CommentService commentService;
    private final UserFacade userFacade;
    private final CommentUserLikeService commentUserLikeService;
    private final PageDataConverter pageDataConverter;
    private final CommentResponseConverter commentResponseConverter;
    private final PostService postService;
    private final CommentUserProfileService commentUserProfileService;
    private final ContentUserProfileConverter contentUserProfileConverter;
    private final UserAnonymousCountService userAnonymousCountService;
    private final AutomatedAuthorPushService automatedAuthorPushService;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final StockBoardGroupPostCommentValidator stockBoardGroupPostCommentValidator;
    private final SolidarityLeaderService solidarityLeaderService;
    private final UserRoleService userRoleService;
    private final CommentPagedResponseConverter commentPagedResponseConverter;


    public CommentDataResponse createBoardGroupPostComment(
        String stockCode,
        Long postId,
        CreateCommentRequest createCommentRequest
    ) {
        return createBoardGroupPostCommentAndReply(
            stockCode, postId, NULL_PARENT_COMMENT_ID, createCommentRequest
        );
    }

    public CommentDataResponse createBoardGroupPostCommentAndReply(
        String stockCode,
        Long postId,
        Long parentCommentId,
        CreateCommentRequest createCommentRequest
    ) {
        final User currentUser = ActUserProvider.getNoneNull();
        final Boolean isAnonymous = createCommentRequest.getIsAnonymous();

        if (isAnonymous) {
            userAnonymousCountService.validateAndIncreaseCommentCount(currentUser);
        }

        final Post post = postService.getPostNotDeleted(postId);
        final Comment savedComment = commentService.save(
            makeComment(
                post, currentUser.getId(), parentCommentId, createCommentRequest.getContent(), isAnonymous
            )
        );

        final CommentUserProfile commentUserProfile = createCommentUserProfile(
            stockCode, currentUser.getId(), isAnonymous, savedComment.getId()
        );

        postService.updatePostWithCommentCount(postId, commentService.countByPostId(postId));
        createAutomatedAuthorPushForPostCommentCount(post, parentCommentId);

        if (parentCommentId > NULL_PARENT_COMMENT_ID) {
            updateParentCommentReplyCount(postId, parentCommentId);
        }

        final boolean isPostAuthor = Objects.equals(post.getUserId(), currentUser.getId());

        return new CommentDataResponse()
            .data(
                getCommentResponse(
                    savedComment,
                    isPostAuthor,
                    false,
                    commentUserProfile
                )
            );
    }

    private void createAutomatedAuthorPushForPostCommentCount(Post post, Long parentCommentId) {
        automatedAuthorPushService.createAutomatedAuthorPushForPostCommentCount(
            post, parentCommentId, getCommentCountWithoutAuthor(post, parentCommentId)
        );
    }

    private Long getCommentCountWithoutAuthor(Post post, Long parentCommentId) {
        if (parentCommentId > NULL_PARENT_COMMENT_ID) {
            return commentService.countByPostIdAndParentIdWithoutAuthor(post.getId(), parentCommentId, post.getUserId());
        }
        return commentService.countByPostIdWithoutAuthor(post.getId(), post.getUserId());
    }

    private CommentUserProfile createCommentUserProfile(String stockCode, Long userId, Boolean isAnonymous, Long commentId) {
        final Boolean isSolidarityLeader = getIsSolidarityLeader(userId);

        CommentUserProfile commentUserProfile = getCommentUserProfile(stockCode, userId, isAnonymous, isSolidarityLeader);
        commentUserProfile.setCommentId(commentId);
        // TODO 여기서 저장된 객체를 리턴하면 오류가 발생한다. 왜 그런지는 모르겠다.
        commentUserProfileService.save(commentUserProfile);
        return commentUserProfile;
    }

    private Boolean getIsSolidarityLeader(Long userId) {
        return userRoleService.isAdmin(userId) ? Boolean.FALSE : solidarityLeaderService.isLeader(userId);
    }

    public CommentDataResponse updateBoardGroupPostCommentAndReply(
        Long userId, Long postId, Long commentId,
        UpdateCommentRequest updateCommentRequest
    ) {
        stockBoardGroupPostValidator.validateAuthor(ActUserProvider.getNoneNull(), userId, "댓글");
        final Post post = postService.getPostNotDeleted(postId);
        Comment comment = validateAndUpdateComment(commentId, updateCommentRequest);
        Set<Long> userLikedCommentIdSet = getMyLikedCommentIdSet(postId, userId, comment);

        final boolean isPostAuthor = Objects.equals(post.getUserId(), userId);
        final boolean likedByMe = userLikedCommentIdSet.contains(comment.getId());

        return new CommentDataResponse()
            .data(
                getCommentResponse(
                    comment,
                    isPostAuthor,
                    likedByMe,
                    comment.getCommentUserProfile()
                )
            );
    }

    public SimpleStringResponse deleteBoardGroupPostComment(
        Long postId,
        Long commentId
    ) {
        Comment comment = commentService.getComment(commentId, "삭제 %s".formatted(COMMENT_NOT_FOUND_ERROR_MESSAGE));
        validateAndDeleteComment(comment);
        stockBoardGroupPostValidator.validateAuthor(ActUserProvider.getNoneNull(), comment.getUserId(), "댓글");

        //post comment count update
        postService.updatePostWithCommentCount(postId, commentService.countByPostId(postId));

        //comment reply count update
        if (comment.getParentId() != null && comment.getParentId() > NULL_PARENT_COMMENT_ID) {
            updateParentCommentReplyCount(postId, comment.getParentId());
        }

        return new SimpleStringResponse().status("ok");
    }

    public CommentPagingResponse getBoardGroupPostComments(
        Long postId,
        Long parentCommentId,
        List<Long> blockedUserIdList,
        PageRequest pageRequest
    ) {
        Post post = postService.getPostNotDeleted(postId);
        Page<Comment> postComments = commentService.getPostComments(postId, parentCommentId, blockedUserIdList, pageRequest);
        List<Long> replyCommentParentIdList = getCommentIds(postComments.getContent());
        List<Comment> replyComments = commentService.getRepliesByParentIds(
            replyCommentParentIdList,
            blockedUserIdList,
            REPLY_DEFAULT_SIZE
        );

        final GetCommentRequestDto getCommentRequestDto = getGetCommentRequestDto(postId, postComments, replyComments, post);

        SimplePageDto<CommentResponse> commentResponses = new SimplePageDto<>(
            commentPagedResponseConverter.convert(postComments, getCommentRequestDto)
        );

        return pageDataConverter.convert(
            commentResponses, CommentPagingResponse.class
        );
    }

    private GetCommentRequestDto getGetCommentRequestDto(
        Long postId,
        Page<Comment> postComments,
        List<Comment> replyComments,
        Post post
    ) {
        List<Comment> allComments = Stream.of(postComments.getContent(), replyComments)
            .flatMap(List::stream)
            .toList();

        return ActUserProvider.get()
            .map(user -> {
                Set<Long> userLikedCommentIdSet = getMyLikedCommentIdSet(
                    postId,
                    user.getId(),
                    allComments
                );
                return GetCommentRequestDto.of(post, userLikedCommentIdSet, replyComments);
            }).orElseGet(() -> GetCommentRequestDto.of(post, Set.of(), replyComments));
    }

    private List<Long> getCommentIds(List<Comment> comments) {
        return comments.stream()
            .filter(Comment::hasReplyComment)
            .map(Comment::getId)
            .toList();
    }

    private CommentUserProfile getCommentUserProfile(String stockCode, Long userId, Boolean isAnonymous, Boolean isSolidarityLeader) {
        SimpleUserProfileDto simpleUserProfileDto = userFacade.getSimpleUserProfileDto(userId, stockCode);
        return contentUserProfileConverter.convertCommentUserProfile(userId, simpleUserProfileDto, isAnonymous, isSolidarityLeader);
    }

    private void updateParentCommentReplyCount(Long postId, Long parentCommentId) {
        Comment parentComment = getParentComment(parentCommentId);
        parentComment.setReplyCommentCount(commentService.countByParentId(postId, parentCommentId));

        commentService.save(parentComment);
    }

    private Comment getParentComment(Long parentCommentId) {
        return commentService.getComment(parentCommentId, COMMENT_NOT_FOUND_ERROR_MESSAGE);
    }

    private Comment validateAndUpdateComment(Long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = getCommentForUpdate(commentId);
        stockBoardGroupPostCommentValidator.validateStatusNotDeleted(comment.getStatus());

        comment.setContent(updateCommentRequest.getContent());
        comment.setEditedAt(LocalDateTime.now());
        return commentService.save(comment);
    }

    private void validateAndDeleteComment(Comment comment) {
        stockBoardGroupPostCommentValidator.validateStatusNotDeleted(comment.getStatus());

        deleteCommentByUser(comment);
    }

    private Comment deleteCommentByUser(Comment comment) {
        comment.setDeletedAt(LocalDateTime.now());
        comment.setStatus(Status.DELETED_BY_USER);
        return commentService.save(comment);
    }

    private Set<Long> getMyLikedCommentIdSet(Long postId, Long userId, Comment comment) {
        return getMyLikedCommentIdSet(postId, userId, List.of(comment));
    }

    private Set<Long> getMyLikedCommentIdSet(Long postId, Long userId, List<Comment> commentList) {
        List<Long> commentIdList = commentList.stream()
            .map(Comment::getId)
            .toList();

        return toCommentIdSet(
            commentUserLikeService.findAllByCommentIdIn(postId, userId, commentIdList)
        );
    }

    private Set<Long> toCommentIdSet(List<CommentUserLike> likeList) {
        return likeList.stream()
            .map(CommentUserLike::getCommentId)
            .collect(Collectors.toSet());
    }

    private Comment getCommentForUpdate(Long commentId) {
        return commentService.getComment(commentId, "수정 %s".formatted(COMMENT_NOT_FOUND_ERROR_MESSAGE));
    }

    private Comment makeComment(Post post, Long userId, Long parentCommentId, String content, Boolean isAnonymous) {
        return commentService.makeComment(post, userId, parentCommentId, content, isAnonymous);
    }

    public CommentResponse getCommentResponse(
        Comment comment,
        boolean matchAnonymousPostWriter,
        Boolean liked,
        CommentUserProfile commentUserProfile
    ) {
        return commentResponseConverter.convert(
            comment,
            matchAnonymousPostWriter,
            liked,
            commentUserProfile,
            comment.getAnonymousCount()
        );
    }
}