package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.constants.MessageConstants;
import ag.act.converter.ContentUserProfileConverter;
import ag.act.converter.post.comment.CommentResponseConverter;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserLike;
import ag.act.entity.CommentUserProfile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.facade.user.UserFacade;
import ag.act.service.post.PostService;
import ag.act.service.push.AutomatedAuthorPushService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCommentService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.service.stockboardgrouppost.comment.CommentUserLikeService;
import ag.act.service.stockboardgrouppost.comment.CommentUserProfileService;
import ag.act.service.user.UserRoleService;
import ag.act.validator.post.StockBoardGroupPostCommentValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostCommentServiceTest {
    @InjectMocks
    private StockBoardGroupPostCommentService service;

    @Mock
    private CommentService commentService;
    @Mock
    private UserFacade userFacade;
    @Mock
    private CommentUserLikeService commentUserLikeService;
    @Mock
    private CommentResponseConverter commentResponseConverter;
    @Mock
    private PostService postService;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private CommentUserProfileService commentUserProfileService;
    @Mock
    private ContentUserProfileConverter contentUserProfileConverter;
    @Mock
    private AutomatedAuthorPushService automatedAuthorPushService;
    @Mock
    private StockBoardGroupPostCommentValidator stockBoardGroupPostCommentValidator;
    @Mock
    private SolidarityLeaderService solidarityLeaderService;
    @Mock
    private UserRoleService userRoleService;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenCreateComment {
        @Mock
        private ag.act.model.CreateCommentRequest createCommentRequest;
        @Mock
        private SimpleUserProfileDto simpleUserProfileDto;
        @Mock
        private CommentUserProfile commentUserProfile;
        @Mock
        private Comment comment;
        @Mock
        private Comment savedComment;
        @Mock
        private ag.act.model.CommentResponse commentResponse;
        @Mock
        private Post post;
        @Mock
        private User user;

        private String stockCode;
        private Long postId;
        private Long parentCommentId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            postId = someLong();
            parentCommentId = 0L;
            final Long userId = someLong();
            final Boolean isAnonymous = Boolean.FALSE;
            final Boolean isSolidarityLeader = Boolean.FALSE;
            final String content = someString(10);
            final Integer anonymousCount = someInteger();
            final Long commentCount = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(createCommentRequest.getContent()).willReturn(content);
            given(createCommentRequest.getIsAnonymous()).willReturn(isAnonymous);

            given(postService.getPostNotDeleted(postId)).willReturn(post);
            given(commentService.makeComment(post, userId, parentCommentId, content, isAnonymous)).willReturn(comment);
            given(commentService.save(comment)).willReturn(savedComment);
            given(savedComment.getAnonymousCount()).willReturn(anonymousCount);

            given(userFacade.getSimpleUserProfileDto(userId, stockCode))
                .willReturn(simpleUserProfileDto);
            given(contentUserProfileConverter.convertCommentUserProfile(userId, simpleUserProfileDto, isAnonymous, isSolidarityLeader))
                .willReturn(commentUserProfile);

            given(commentUserProfileService.save(commentUserProfile)).willReturn(commentUserProfile);

            given(commentService.countByPostId(postId)).willReturn(commentCount);
            willDoNothing().given(postService).updatePostWithCommentCount(postId, commentCount);

            given(commentResponseConverter.convert(
                savedComment, false, false, commentUserProfile, anonymousCount
            )).willReturn(commentResponse);
        }

        @Test
        void shouldCreateComment() {
            ag.act.model.CommentDataResponse commentCreateResponse = service.createBoardGroupPostCommentAndReply(
                stockCode, postId, parentCommentId, createCommentRequest
            );

            assertThat(commentCreateResponse.getData(), is(commentResponse));
        }
    }

    @Nested
    class WhenUpdateComment {
        @Mock
        private ag.act.model.UpdateCommentRequest updateCommentRequest;
        @Mock
        private CommentUserProfile commentUserProfile;
        @Mock
        private Comment comment;
        @Mock
        private ag.act.model.CommentResponse commentResponse;
        @Mock
        private List<CommentUserLike> likeList;
        @Mock
        private User user;
        @Mock
        private Post post;

        private Long userId;
        private Long postId;
        private Long commentId;

        @BeforeEach
        void setUp() {
            final String content = someString(10);
            userId = someLong();
            postId = someLong();
            commentId = someLong();

            given(updateCommentRequest.getContent()).willReturn(content);
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(ActUserProvider.getNoneNull().getId()).willReturn(userId);
            willDoNothing().given(stockBoardGroupPostValidator).validateAuthor(user, userId, "댓글");

            given(postService.getPostNotDeleted(postId)).willReturn(post);
            given(commentService.getComment(commentId, "수정 %s".formatted(MessageConstants.COMMENT_NOT_FOUND_ERROR_MESSAGE)))
                .willReturn(comment);

            willDoNothing().given(stockBoardGroupPostCommentValidator).validateStatusNotDeleted(any(ag.act.model.Status.class));
            given(commentService.save(comment)).willReturn(comment);

            given(commentUserLikeService.findAllByCommentIdIn(postId, userId, List.of(commentId))).willReturn(likeList);

            given(comment.getId()).willReturn(commentId);
            given(comment.getCommentUserProfile()).willReturn(commentUserProfile);
            given(commentResponseConverter.convert(
                comment, false, false, commentUserProfile, 0
            )).willReturn(commentResponse);
        }

        @Test
        void shouldUpdateComment() {
            ag.act.model.CommentDataResponse commentUpdateResponse = service.updateBoardGroupPostCommentAndReply(
                userId, postId, commentId, updateCommentRequest
            );

            assertThat(commentUpdateResponse.getData(), is(commentResponse));
        }
    }

    @Nested
    class WhenDeleteComment {
        @Mock
        private Comment comment;
        @Mock
        private Comment savedComment;
        @Mock
        private User user;
        @Mock
        private Post post;

        private Long postId;
        private Long commentId;

        @BeforeEach
        void setUp() {
            postId = someLong();
            commentId = someLong();
            final Long userId = someLong();
            final Long commentCount = someLong();

            given(comment.getParentId()).willReturn(0L);
            given(commentService.getComment(commentId, "삭제 %s".formatted(MessageConstants.COMMENT_NOT_FOUND_ERROR_MESSAGE)))
                .willReturn(comment);
            willDoNothing().given(stockBoardGroupPostCommentValidator).validateStatusNotDeleted(any(ag.act.model.Status.class));
            given(commentService.save(comment)).willReturn(savedComment);

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(ActUserProvider.getNoneNull().getId()).willReturn(userId);
            willDoNothing().given(stockBoardGroupPostValidator).validateAuthor(user, userId, "댓글");

            given(postService.getPostNotDeleted(postId)).willReturn(post);
            given(commentService.countByParentId(postId, commentId)).willReturn(commentCount);
            given(commentService.getComment(commentId, MessageConstants.COMMENT_NOT_FOUND_ERROR_MESSAGE))
                .willReturn(comment);
            given(commentService.save(comment)).willReturn(savedComment);
        }

        @Test
        void shouldDeleteComment() {
            ag.act.model.SimpleStringResponse commentDeleteResponse = service.deleteBoardGroupPostComment(
                postId, commentId
            );

            assertThat(commentDeleteResponse.getStatus(), is("ok"));
        }
    }
}