package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.configuration.AnonymousUserContentLimits;
import ag.act.entity.User;
import ag.act.entity.UserAnonymousCount;
import ag.act.repository.UserAnonymousCountRepository;
import ag.act.service.post.PostAnonymousCountService;
import ag.act.service.stockboardgrouppost.comment.CommentAnonymousCountService;
import ag.act.service.user.UserAnonymousCountService;
import ag.act.util.DateTimeUtil;
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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserAnonymousCountServiceTest {
    @InjectMocks
    private UserAnonymousCountService service;
    @Mock
    private UserAnonymousCountRepository userAnonymousCountRepository;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Mock
    private Optional<UserAnonymousCount> optionalUserAnonymousCount;
    @Mock
    private AnonymousUserContentLimits anonymousUserContentLimits;
    @Mock
    private PostAnonymousCountService postAnonymousCountService;
    @Mock
    private CommentAnonymousCountService commentAnonymousCountService;
    @Mock
    private User user;

    private Long userId;
    private String currentDate;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        userId = someLong();

        statics = List.of(mockStatic(ActUserProvider.class), mockStatic(DateTimeUtil.class));

        given(anonymousUserContentLimits.getPostLimitCount()).willReturn(someIntegerBetween(1, 100));
        given(anonymousUserContentLimits.getCommentLimitCount()).willReturn(someIntegerBetween(1, 100));
        given(user.isAdmin()).willReturn(Boolean.FALSE);
        given(user.getId()).willReturn(userId);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenGetAnonymousCount {

        @BeforeEach
        void setUp() {
            currentDate = someString(10);

            // Given
            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(userAnonymousCountRepository.findByUserIdAndWriteDate(user.getId(), currentDate))
                .willReturn(optionalUserAnonymousCount);
        }

        @Test
        void shouldReturnAnonymousCount() {

            // When
            final ag.act.model.GetAnonymousCountResponse actual = service.getUserAnonymousCount(currentDate);

            // Then
            assertThat(actual.getData(), is(notNullValue()));
        }
    }

    @Nested
    class ValidateAndIncreasePostOrCommentCount {

        @Mock
        private UserAnonymousCount userAnonymousCount;
        private UserAnonymousCount actualUserAnonymousCount;

        @BeforeEach
        void setUp() {
            optionalUserAnonymousCount = Optional.of(userAnonymousCount);

            given(DateTimeUtil.getFormattedCurrentTimeInKorean("yyyyMMdd")).willReturn(currentDate);
            given(userAnonymousCountRepository.findByUserIdAndWriteDate(userId, currentDate))
                .willReturn(optionalUserAnonymousCount);
            given(userAnonymousCountRepository.save(userAnonymousCount)).willReturn(userAnonymousCount);
        }

        @Nested
        class ValidateAndIncreaseCommentCount extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                given(commentAnonymousCountService.validateAndIncreaseCount(user, userAnonymousCount)).willReturn(userAnonymousCount);

                actualUserAnonymousCount = service.validateAndIncreaseCommentCount(user);
            }

            @Test
            void shouldCallCommentAnonymousCountService() {
                then(commentAnonymousCountService).should().validateAndIncreaseCount(user, userAnonymousCount);
            }
        }

        @Nested
        class ValidateAndIncreasePostCount extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                given(postAnonymousCountService.validateAndIncreaseCount(userAnonymousCount)).willReturn(userAnonymousCount);

                actualUserAnonymousCount = service.validateAndIncreasePostCount(userId);
            }

            @Test
            void shouldCallCommentAnonymousCountService() {
                then(postAnonymousCountService).should().validateAndIncreaseCount(userAnonymousCount);
            }
        }

        @Nested
        class WhenNotFoundExitingUserAnonymousCount extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                given(postAnonymousCountService.validateAndIncreaseCount(userAnonymousCount)).willReturn(userAnonymousCount);

                actualUserAnonymousCount = service.validateAndIncreasePostCount(userId);
            }

            @Test
            void shouldCallCommentAnonymousCountService() {
                then(postAnonymousCountService).should().validateAndIncreaseCount(userAnonymousCount);
            }
        }

        @SuppressWarnings("unused")
        class DefaultTestCases {

            @Test
            void shouldReturnUserAnonymousCount() {
                assertThat(actualUserAnonymousCount, is(userAnonymousCount));
            }

            @Test
            void shouldCallUserAnonymousCountRepository() {
                then(userAnonymousCountRepository).should().save(userAnonymousCount);
            }

            @Test
            void shouldCallFindByUserIdAndWriteDate() {
                then(userAnonymousCountRepository).should().findByUserIdAndWriteDate(userId, currentDate);
            }
        }
    }
}
