package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.post.comment.CommentLikeRequestDto;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserLike;
import ag.act.entity.User;
import ag.act.enums.BoardGroup;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.service.stockboardgrouppost.comment.CommentUserLikeService;
import ag.act.service.stockboardgrouppost.comment.ValidCommentForLikeRetriever;
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
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostCommentLikeServiceTest {

    @InjectMocks
    private StockBoardGroupPostCommentLikeService service;

    private List<MockedStatic<?>> statics;
    @Mock
    private CommentService commentService;
    @Mock
    private CommentUserLikeService commentUserLikeService;
    @Mock
    private ValidCommentForLikeRetriever validCommentForLikeRetriever;
    @Mock
    private User user;
    @Mock
    private Comment comment;
    @Mock
    private CommentUserLike commentUserLike;
    private Long postId;
    private Long commentId;
    private CommentLikeRequestDto commentLikeRequestDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        final String stockCode = someString(0);
        final BoardGroup boardGroup = someBoardGroupForStock();
        postId = someLong();
        commentId = someLong();
        commentLikeRequestDto = CommentLikeRequestDto.of(stockCode, boardGroup.name(), postId, commentId);
        userId = someLong();
        final Long sumCount = someLong();

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
        given(validCommentForLikeRetriever.getValidCommentForLike(commentLikeRequestDto)).willReturn(comment);

        given(commentUserLikeService.countByCommentLike(postId, commentId)).willReturn(sumCount);
        given(commentService.save(comment)).willReturn(comment);

        willDoNothing().given(commentUserLikeService).saveCommentUserLike(commentUserLike);
        willDoNothing().given(commentUserLikeService).deleteCommentUserLike(commentUserLike);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class LikeComment {

        @Nested
        class WhenCommentNotExisting {

            @BeforeEach
            void setUp() {
                notExistingCommentUserLike();

                service.like(commentLikeRequestDto);
            }

            @Test
            void shouldSaveCommentUserLike() {
                then(commentUserLikeService).should().saveCommentUserLike(any(CommentUserLike.class));
            }
        }

        @Nested
        class WhenCommentExisting {

            @BeforeEach
            void setUp() {
                existingCommentUserLike();

                service.like(commentLikeRequestDto);
            }

            @Test
            void shouldNotSaveCommentUserLike() {
                then(commentUserLikeService).should(never()).saveCommentUserLike(any(CommentUserLike.class));
            }
        }
    }

    @Nested
    class UnlikeComment {

        @Nested
        class WhenCommentNotExisting {

            @BeforeEach
            void setUp() {
                notExistingCommentUserLike();

                service.unlike(commentLikeRequestDto);
            }

            @Test
            void shouldNotDeleteCommentUserLike() {
                then(commentUserLikeService).should(never()).deleteCommentUserLike(any(CommentUserLike.class));
            }
        }

        @Nested
        class WhenCommentExisting {

            @BeforeEach
            void setUp() {
                existingCommentUserLike();

                service.unlike(commentLikeRequestDto);
            }

            @Test
            void shouldDeleteCommentUserLike() {
                then(commentUserLikeService).should().deleteCommentUserLike(commentUserLike);
            }
        }
    }

    private void existingCommentUserLike() {
        given(commentUserLikeService.findCommentUserLike(postId, userId, commentId)).willReturn(Optional.of(commentUserLike));
    }

    private void notExistingCommentUserLike() {
        given(commentUserLikeService.findCommentUserLike(postId, userId, commentId)).willReturn(Optional.empty());
    }
}
