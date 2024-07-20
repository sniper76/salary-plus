package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.BoardGroup;
import ag.act.facade.post.CommentFacade;
import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCommentService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.util.StatusUtil;
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
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CommentFacadeTest {
    @InjectMocks
    private CommentFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private StockBoardGroupPostCommentService stockBoardGroupPostCommentService;
    @Mock
    private CommentService commentService;
    @Mock
    private BlockedUserService blockedUserService;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        given(blockedUserService.getBlockUserIdListOfMine()).willReturn(List.of());
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenFacadeCreateComment {
        @Mock
        private ag.act.model.CreateCommentRequest createCommentRequest;
        @Mock
        private ag.act.model.CommentDataResponse commentDataResponse;
        @Mock
        private User user;
        @Mock
        private Post post;

        private String stockCode;
        private BoardGroup boardGroup;
        private Long postId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            boardGroup = someBoardGroupForStock();
            postId = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeletedStatusesForPostDetails()
            )).willReturn(post);

            given(stockBoardGroupPostCommentService.createBoardGroupPostComment(
                stockCode, postId, createCommentRequest
            )).willReturn(commentDataResponse);
        }

        @Test
        void shouldCreateComment() {
            ag.act.model.CommentDataResponse response = facade.createBoardGroupPostComment(
                stockCode, boardGroup.name(), postId, createCommentRequest
            );

            assertThat(response, is(commentDataResponse));
        }
    }

    @Nested
    class WhenFacadeUpdateComment {
        @Mock
        private ag.act.model.UpdateCommentRequest updateCommentRequest;
        @Mock
        private ag.act.model.CommentDataResponse commentDataResponse;
        @Mock
        private User user;
        @Mock
        private Post post;

        private String stockCode;
        private BoardGroup boardGroup;
        private Long postId;
        private Long commentId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            boardGroup = someBoardGroupForStock();
            postId = someLong();
            commentId = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeletedStatusesForPostDetails()
            )).willReturn(post);

            given(stockBoardGroupPostCommentService.updateBoardGroupPostCommentAndReply(
                user.getId(), postId, commentId, updateCommentRequest
            )).willReturn(commentDataResponse);
        }

        @Test
        void shouldUpdateComment() {
            ag.act.model.CommentDataResponse response = facade.updateBoardGroupPostComment(
                stockCode, boardGroup.name(), postId, commentId, updateCommentRequest
            );

            assertThat(response, is(commentDataResponse));
        }
    }

    @Nested
    class WhenFacadeDeleteComment {
        @Mock
        private ag.act.model.SimpleStringResponse okayResponse;
        @Mock
        private Post post;

        private String stockCode;
        private BoardGroup boardGroup;
        private Long postId;
        private Long commentId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            boardGroup = someBoardGroupForStock();
            postId = someLong();
            commentId = someLong();

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeletedStatusesForPostDetails()
            )).willReturn(post);

            given(stockBoardGroupPostCommentService.deleteBoardGroupPostComment(
                postId, commentId
            )).willReturn(okayResponse);
        }

        @Test
        void shouldDeleteComment() {
            ag.act.model.SimpleStringResponse response = facade.deleteBoardGroupPostComment(
                stockCode, boardGroup.name(), postId, commentId
            );

            assertThat(response.getStatus(), is(okayResponse.getStatus()));
        }
    }

    @Nested
    class WhenFacadeCreateCommentReply {
        @Mock
        private ag.act.model.CreateCommentRequest createCommentRequest;
        @Mock
        private ag.act.model.CommentDataResponse commentDataResponse;
        @Mock
        private User user;
        @Mock
        private Post post;

        private String stockCode;
        private BoardGroup boardGroup;
        private Long postId;
        private Long parentCommentId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            boardGroup = someBoardGroupForStock();
            postId = someLong();
            parentCommentId = someLong();
            final Comment comment = new Comment();
            comment.setParentId(null);

            given(ActUserProvider.getNoneNull()).willReturn(user);

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeletedStatusesForPostDetails()
            )).willReturn(post);

            given(commentService.getComment(parentCommentId, "답글 대상 댓글이 존재하지 않습니다."))
                .willReturn(comment);

            given(stockBoardGroupPostCommentService.createBoardGroupPostCommentAndReply(
                stockCode, postId, parentCommentId, createCommentRequest
            )).willReturn(commentDataResponse);
        }

        @Test
        void shouldCreateCommentReply() {
            ag.act.model.CommentDataResponse response = facade.createBoardGroupPostCommentReply(
                stockCode, boardGroup.name(), postId, parentCommentId, createCommentRequest
            );

            assertThat(response, is(commentDataResponse));
        }
    }

    @Nested
    class WhenPostDeleted {

        @Mock
        private ag.act.model.CreateCommentRequest createCommentRequest;
        @Mock
        private ag.act.model.CommentDataResponse commentDataResponse;
        @Mock
        private User user;
        @Mock
        private Post post;

        private String stockCode;
        private BoardGroup boardGroup;
        private Long postId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            boardGroup = someBoardGroupForStock();
            postId = someLong();

            given(post.getStatus()).willReturn(Status.DELETED_BY_USER);
            given(ActUserProvider.getNoneNull()).willReturn(user);

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeletedStatusesForPostDetails()
            )).willReturn(post);
        }

        @Nested
        class CreateComment {

            @BeforeEach
            void setUp() {
                given(stockBoardGroupPostCommentService.createBoardGroupPostComment(
                    stockCode, postId, createCommentRequest
                )).willReturn(commentDataResponse);
            }

            @Test
            void shouldBeCreateComment() {
                CommentDataResponse commentResponse = facade.createBoardGroupPostComment(
                    stockCode,
                    boardGroup.name(),
                    postId,
                    createCommentRequest
                );

                assertThat(commentResponse, is(commentDataResponse));
            }
        }

        @Nested
        class DeletedComment {

            @Mock
            private ag.act.model.SimpleStringResponse okayResponse;
            private Long commentId;

            @BeforeEach
            void setUp() {
                commentId = someLong();

                given(stockBoardGroupPostCommentService.deleteBoardGroupPostComment(postId, commentId))
                    .willReturn(okayResponse);
            }

            @Test
            void shouldDeleteComment() {
                SimpleStringResponse response = facade.deleteBoardGroupPostComment(stockCode, boardGroup.name(), postId, commentId);

                assertThat(response.getStatus(), is(okayResponse.getStatus()));
            }
        }

        @Nested
        class UpdateComment {

            @Mock
            private ag.act.model.UpdateCommentRequest updateCommentRequest;
            @Mock
            private ag.act.model.CommentDataResponse commentDataResponse;
            private Long commentId;

            @BeforeEach
            void setUp() {
                commentId = someLong();
                given(stockBoardGroupPostCommentService.updateBoardGroupPostCommentAndReply(
                    user.getId(),
                    postId,
                    commentId,
                    updateCommentRequest
                )).willReturn(commentDataResponse);
            }

            @Test
            void shouldBeUpdateComment() {
                CommentDataResponse response = facade.updateBoardGroupPostComment(
                    stockCode,
                    boardGroup.name(),
                    postId,
                    commentId,
                    updateCommentRequest
                );

                assertThat(response, is(commentDataResponse));
            }
        }

        @Nested
        class GetComments {

            @Mock
            private ag.act.model.CommentPagingResponse commentPagingResponse;
            @Mock
            private PageRequest pageRequest;

            @BeforeEach
            void setUp() {
                given(stockBoardGroupPostCommentService.getBoardGroupPostComments(
                    postId,
                    0L,
                    List.of(),
                    pageRequest
                )).willReturn(commentPagingResponse);
            }

            @Test
            void shouldGetComments() {
                CommentPagingResponse boardGroupPostComments = facade.getBoardGroupPostComments(
                    stockCode,
                    boardGroup.name(),
                    postId,
                    0L,
                    pageRequest
                );

                assertThat(boardGroupPostComments, is(commentPagingResponse));
            }
        }
    }
}