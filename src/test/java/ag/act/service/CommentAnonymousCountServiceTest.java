package ag.act.service;

import ag.act.core.configuration.AnonymousUserContentLimits;
import ag.act.entity.User;
import ag.act.entity.UserAnonymousCount;
import ag.act.exception.BadRequestException;
import ag.act.service.stockboardgrouppost.comment.CommentAnonymousCountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class CommentAnonymousCountServiceTest {
    @InjectMocks
    private CommentAnonymousCountService service;
    @Mock
    private AnonymousUserContentLimits anonymousUserContentLimits;
    @Mock
    private UserAnonymousCount userAnonymousCount;
    @Mock
    private User user;

    private Integer commentLimitCount;

    @BeforeEach
    void setUp() {
        commentLimitCount = someIntegerBetween(10, 20);

        given(anonymousUserContentLimits.getCommentLimitCount()).willReturn(commentLimitCount);
        given(user.isAdmin()).willReturn(Boolean.FALSE);
    }


    @Nested
    class ValidateAndIncreaseCount {
        @Nested
        class WhenValidateCommentCount {

            private int currentCommentCount;

            @BeforeEach
            void setUp() {
                currentCommentCount = commentLimitCount - 1;

                given(userAnonymousCount.getCommentCount()).willReturn(currentCommentCount);
            }

            @Test
            void shouldValidateAndIncreaseCount() {

                // When
                final UserAnonymousCount actual = service.validateAndIncreaseCount(user, userAnonymousCount);

                // Then
                assertThat(actual, is(userAnonymousCount));
                then(userAnonymousCount).should().setCommentCount(currentCommentCount + 1);
            }
        }

        @Nested
        class WhenFailToValidate {
            @BeforeEach
            void setUp() {
                given(userAnonymousCount.getCommentCount()).willReturn(commentLimitCount + someIntegerBetween(0, 5));
            }

            @Test
            void shouldThrowBadRequestException() {

                // When
                final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> service.validateAndIncreaseCount(user, userAnonymousCount)
                );

                // Then
                assertThat(exception.getMessage(), is("익명 댓글/답글 일일 작성횟수를 초과하였습니다."));
                then(userAnonymousCount).should(never()).setCommentCount(anyInt());
            }
        }
    }
}