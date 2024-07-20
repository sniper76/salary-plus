package ag.act.facade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.converter.home.HomeResponseConverter;
import ag.act.converter.post.PreviewPostResponseConverter;
import ag.act.converter.stock.MySolidarityResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.MySolidarityDto;
import ag.act.dto.SimplePageDto;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.LinkType;
import ag.act.model.HomeResponse;
import ag.act.model.MySolidarityResponse;
import ag.act.module.solidarity.MySolidarityPageableFactory;
import ag.act.service.HomeService;
import ag.act.service.LinkUrlService;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.UnreadPostService;
import ag.act.service.solidarity.MySolidarityService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserService;
import ag.act.service.virtualboard.VirtualBoardGroupPostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.somePage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class HomeFacadeTest {
    @InjectMocks
    private HomeFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private User user;
    @Mock
    private UserService userService;
    @Mock
    private HomeService homeService;
    @Mock
    private LinkUrlService linkUrlService;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private User savedUser;
    @Mock
    private HomeResponseConverter homeResponseConverter;
    @Mock
    private MySolidarityResponseConverter mySolidarityResponseConverter;
    @Mock
    private MySolidarityService mySolidarityService;
    @Mock
    private PageDataConverter pageDataConverter;
    @Mock
    private PreviewPostResponseConverter previewPostResponseConverter;
    @Mock
    private VirtualBoardGroupPostService virtualBoardGroupPostService;
    @Mock
    private UnreadPostService unreadPostService;
    @Mock
    private HomeResponse homeResponse;
    @Mock
    private Page<MySolidarityDto> mySolidarityDtoListPage;
    @Mock
    private List<MySolidarityResponse> mySolidarityResponses;
    @Mock
    private ag.act.model.HomeLinkResponse homeLinkResponse;
    @Mock
    private MySolidarityPageableFactory mySolidarityPageableFactory;
    private Long userId;
    private Page mappedPage;

    private final PageRequest pageable = PageRequest.of(0, 10000);

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class), mockStatic(LocalDateTime.class));

        userId = somePositiveLong();
        mappedPage = somePage(List.<MySolidarityResponse>of());

        given(user.getId()).willReturn(userId);
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(userService.getUser(userId)).willReturn(savedUser);
        given(savedUser.getId()).willReturn(userId);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }


    @Nested
    class WhenGetHome {
        private static final int FIRST_PAGE = 1;
        private static final int ACT_BEST_PREVIEW_SIZE = 2;
        private static final List<String> ACT_BEST_SORTS_QUERIES = List.of("createdAt:desc");
        @Mock
        private PageRequest pageRequest;
        @Mock
        private List<Post> posts;
        @Mock
        private Page<Post> bestPostPage;
        @Mock
        private List<ag.act.model.PostResponse> bestPostResponses;
        @Mock
        private ag.act.model.UnreadPostStatus unreadPostStatus;

        @Test
        void shouldGetHome() {
            Long unreadNotificationsCount = someLong();

            // given
            given(mySolidarityService.getSolidarityResponsesIncludingLinks(savedUser))
                .willReturn(mySolidarityResponses);
            given(notificationService.getUnreadNotificationsCount(userId))
                .willReturn(unreadNotificationsCount);
            given(pageDataConverter.convert(FIRST_PAGE, ACT_BEST_PREVIEW_SIZE, ACT_BEST_SORTS_QUERIES))
                .willReturn(pageRequest);
            given(virtualBoardGroupPostService.getBestPosts(any(GetBoardGroupPostDto.class), eq(pageRequest)))
                .willReturn(bestPostPage);
            given(bestPostPage.getContent()).willReturn(posts);
            given(previewPostResponseConverter.convert(posts))
                .willReturn(bestPostResponses);
            given(unreadPostService.getUnreadPostStatus(userId)).willReturn(unreadPostStatus);
            given(homeResponseConverter.convert(
                    mySolidarityResponses,
                    unreadNotificationsCount,
                    bestPostResponses,
                    unreadPostStatus
                )
            ).willReturn(homeResponse);

            // When
            final HomeResponse actual = facade.getHome();

            // Then
            then(mySolidarityService).should().getSolidarityResponsesIncludingLinks(savedUser);
            then(notificationService).should().getUnreadNotificationsCount(userId);
            then(virtualBoardGroupPostService).should().getBestPosts(any(GetBoardGroupPostDto.class), eq(pageRequest));
            then(previewPostResponseConverter).should().convert(bestPostPage.getContent());
            then(homeResponseConverter).should().convert(
                mySolidarityResponses,
                unreadNotificationsCount,
                bestPostResponses,
                unreadPostStatus
            );

            assertThat(actual, is(homeResponse));
        }
    }

    @Nested
    class WhenGetMySolidarity {
        @Test
        void shouldGetMySolidarity() {
            // Given
            given(userHoldingStockService.getAllSortedMySolidarityList(userId, pageable))
                .willReturn(mySolidarityDtoListPage);
            given(mySolidarityDtoListPage.getContent()).willReturn(List.of());
            given(mySolidarityDtoListPage.map(any())).willReturn(mappedPage);
            given(mySolidarityResponseConverter.convert(any(MySolidarityDto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            // When
            SimplePageDto<MySolidarityResponse> actual = facade.getMySolidarityList(pageable);

            // Then
            then(userHoldingStockService).should().getAllSortedMySolidarityList(userId, pageable);
            assertThat(actual.getContent(), is(mappedPage.getContent()));
        }
    }

    @Nested
    class WhenUpdateMySolidarity {

        @Test
        void shouldUpdateMySolidarity() {

            // Given
            final List<String> stockCodes = List.of(someNumericString(6));
            given(homeService.updateUserHoldingStocks(savedUser, stockCodes, mySolidarityPageableFactory.getPageable()))
                .willReturn(mySolidarityDtoListPage);
            given(mySolidarityDtoListPage.getContent()).willReturn(List.of());
            given(mySolidarityDtoListPage.map(any())).willReturn(mappedPage);
            given(mySolidarityResponseConverter.convert(any(MySolidarityDto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            // When
            final SimplePageDto<MySolidarityResponse> actual = facade.updateMySolidarityListDisplayOrder(
                stockCodes, mySolidarityPageableFactory.getPageable()
            );

            // Then
            then(homeService).should().updateUserHoldingStocks(
                savedUser, stockCodes, mySolidarityPageableFactory.getPageable()
            );
            then(userService).should().saveUser(savedUser);
            assertThat(actual.getContent(), is(mappedPage.getContent()));
        }
    }

    @Nested
    class HomeLinkUrl {
        private LinkType linkType;

        @BeforeEach
        public void setUp() {
            this.linkType = LinkType.RANKING;
        }

        @Nested
        class WhenGetHomeLinkUrl {
            @Test
            void shouldGetHomeNewsLinkUrl() {
                // Given
                given(linkUrlService.findByLinkType(linkType)).willReturn(homeLinkResponse);

                // When
                final ag.act.model.HomeLinkResponse actual = facade.getHomeLink(linkType.name());

                // Then
                then(linkUrlService).should().findByLinkType(linkType);
                assertThat(actual, is(homeLinkResponse));
            }
        }

        @Nested
        class WhenUpdateHomeLinkUrl {
            @Test
            void shouldUpdateHomeNewsLinkUrl() {
                // Given
                String newUrl = someAlphanumericString(10);
                given(linkUrlService.updateByLinkType(linkType, newUrl)).willReturn(homeLinkResponse);

                // When
                final ag.act.model.HomeLinkResponse updated = facade.updateHomeLink(linkType.name(), newUrl);

                // Then
                then(linkUrlService).should().updateByLinkType(linkType, newUrl);
                assertThat(updated, is(homeLinkResponse));
            }
        }
    }
}
