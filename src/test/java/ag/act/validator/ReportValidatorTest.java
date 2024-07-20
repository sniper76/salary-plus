package ag.act.validator;

import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.enums.ReportType;
import ag.act.exception.NotFoundException;
import ag.act.service.post.PostService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class ReportValidatorTest {

    @InjectMocks
    private ReportValidator validator;

    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;

    @Nested
    class ValidateReport {

        @Mock
        private Post post;
        @Mock
        private Comment comment;
        private Long postId;
        private Long commentId;

        @BeforeEach
        void setUp() {
            postId = someLong();
            commentId = someLong();

            given(postService.findById(postId)).willReturn(Optional.of(post));
            given(commentService.findById(commentId)).willReturn(Optional.of(comment));
        }

        @Nested
        class WhenPostExists {

            @Test
            void shouldNotThrowAnyException() {

                // When
                validator.validate(ReportType.POST, postId);

                // Then
                then(postService).should().findById(postId);
            }
        }

        @Nested
        class WhenCommentExists {

            @Test
            void shouldNotThrowAnyException() {

                // When
                validator.validate(ReportType.COMMENT, commentId);

                // Then
                then(commentService).should().findById(commentId);
            }
        }

        @Nested
        class WhenPostNotFound {

            @Test
            void shouldNotThrowAnyException() {

                // Given
                given(postService.findById(postId)).willReturn(Optional.empty());

                // When
                assertException(
                    NotFoundException.class,
                    () -> validator.validate(ReportType.POST, postId),
                    "신고 대상 게시글이 없습니다."
                );

                // Then
                then(postService).should().findById(postId);
            }
        }

        @Nested
        class WhenCommentNotFound {

            @Test
            void shouldThrowNotFoundException() {

                // Given
                given(commentService.findById(commentId)).willReturn(Optional.empty());

                // When
                assertException(
                    NotFoundException.class,
                    () -> validator.validate(ReportType.COMMENT, commentId),
                    "신고 대상 댓글/답글이 없습니다."
                );

                // Then
                then(commentService).should().findById(commentId);
            }
        }
    }
}
