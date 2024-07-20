package ag.act.service;

import ag.act.core.configuration.AnonymousUserContentLimits;
import ag.act.entity.UserAnonymousCount;
import ag.act.exception.BadRequestException;
import ag.act.service.post.PostAnonymousCountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostAnonymousCountServiceTest {
    @InjectMocks
    private PostAnonymousCountService service;
    @Mock
    private AnonymousUserContentLimits anonymousUserContentLimits;
    @Mock
    private UserAnonymousCount userAnonymousCount;

    private Integer postLimitCount;

    @BeforeEach
    void setUp() {
        postLimitCount = someIntegerBetween(10, 20);

        given(anonymousUserContentLimits.getPostLimitCount()).willReturn(postLimitCount);
    }

    @Nested
    class ValidateAndIncreaseCount {
        @Nested
        class WhenValidatePostCount {

            private int currentPostCount;

            @BeforeEach
            void setUp() {
                currentPostCount = postLimitCount - 1;

                given(userAnonymousCount.getPostCount()).willReturn(currentPostCount);
            }

            @Test
            void shouldValidateAndIncreaseCount() {

                // When
                final UserAnonymousCount actual = service.validateAndIncreaseCount(userAnonymousCount);

                // Then
                assertThat(actual, is(userAnonymousCount));
                then(userAnonymousCount).should().setPostCount(currentPostCount + 1);
            }
        }

        @Nested
        class WhenFailToValidate {
            @BeforeEach
            void setUp() {
                given(userAnonymousCount.getPostCount()).willReturn(postLimitCount + someIntegerBetween(0, 5));
            }

            @Test
            void shouldThrowBadRequestException() {

                // When
                assertException(
                    BadRequestException.class,
                    () -> service.validateAndIncreaseCount(userAnonymousCount),
                    "익명 게시글 일일 작성횟수를 초과하였습니다."
                );

                // Then
                then(userAnonymousCount).should(never()).setPostCount(anyInt());
            }
        }
    }
}