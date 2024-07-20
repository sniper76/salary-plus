package ag.act.service;

import ag.act.dto.GetPostsSearchDto;
import ag.act.entity.DigitalProxy;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.admin.PostSearchType;
import ag.act.facade.user.UserRoleFacade;
import ag.act.repository.PostRepository;
import ag.act.service.digitaldocument.modusign.DigitalProxyModuSignService;
import ag.act.service.post.PostService;
import ag.act.service.stockboardgrouppost.search.StockBoardGroupPostSearchByContentService;
import ag.act.service.stockboardgrouppost.search.StockBoardGroupPostSearchByStockCodeService;
import ag.act.service.stockboardgrouppost.search.StockBoardGroupPostSearchByTitleAndContentService;
import ag.act.service.stockboardgrouppost.search.StockBoardGroupPostSearchByTitleService;
import ag.act.service.stockboardgrouppost.search.StockBoardGroupPostSearchServiceResolver;
import ag.act.util.AppRenewalDateProvider;
import ag.act.util.StatusUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceTest {
    @InjectMocks
    private PostService service;
    @Mock
    private PostRepository postRepository;
    @Mock
    private DigitalProxyModuSignService digitalProxyModuSignService;
    @Mock
    private StockBoardGroupPostSearchServiceResolver stockBoardGroupPostSearchServiceResolver;
    @Mock
    private UserRoleFacade userRoleFacade;
    @Mock
    private PageRequest pageRequest;
    @Mock
    private Page<Post> pagePost;
    @Mock
    private ag.act.model.UserDataResponse userDataResponse;
    @Mock
    private LocalDate appRenewalDate;
    @Mock
    private AppRenewalDateProvider appRenewalDateProvider;

    private Long boardId;

    private List<ag.act.model.Status> statusList;
    private List<Long> blockedUserIdList;

    @BeforeEach
    void setUp() {
        boardId = someLong();
        blockedUserIdList = List.of(somePositiveLong());
        statusList = StatusUtil.getStatusesForPostList();

        given(appRenewalDateProvider.get()).willReturn(appRenewalDate);
        given(userRoleFacade.assignAcceptorRoleToUser(anyLong())).willReturn(userDataResponse);
        given(digitalProxyModuSignService.makeDigitalProxy(any(ag.act.model.CreatePostRequest.class)))
            .willReturn(mock(DigitalProxy.class));
    }

    @Nested
    class WhenTotalElementsIsMoreThanZero {

        private List<Post> posts;

        @BeforeEach
        void setUp() {
            posts = List.of(mock(Post.class), mock(Post.class), mock(Post.class));

            // Given
            given(postRepository.findAllByBoardIdInAndStatusInAndUserIdNotIn(
                List.of(boardId), statusList, blockedUserIdList, appRenewalDate.atTime(0, 0, 0), pageRequest
            )).willReturn(pagePost);
            given(pagePost.getContent()).willReturn(posts);
        }

        @Test
        void shouldReturnPageObjectThatHasThreePosts() {

            // When
            final Page<Post> actual = service.getBoardPosts(List.of(boardId), blockedUserIdList, statusList, pageRequest);

            // Then
            assertThat(actual.getContent(), is(posts));
        }
    }

    @Nested
    class WhenTotalElementsIsZero {
        @BeforeEach
        void setUp() {
            // Given
            given(postRepository.findAllByBoardIdInAndStatusInAndUserIdNotIn(
                List.of(boardId), statusList, blockedUserIdList, appRenewalDate.atTime(0, 0, 0), pageRequest
            )).willReturn(pagePost);
            given(pagePost.getContent()).willReturn(emptyList());
        }

        @Test
        void shouldReturnPageObjectThatHasThreePosts() {

            // When
            final Page<Post> actual = service.getBoardPosts(List.of(boardId), blockedUserIdList, statusList, pageRequest);

            // Then
            assertThat(actual.getContent(), is(emptyList()));
        }
    }

    @Nested
    class GetBoardPosts {

        @Mock
        private GetPostsSearchDto getPostsSearchDto;
        @Mock
        private BoardGroup boardGroup;
        private Page<Post> actualResponse;

        @BeforeEach
        void setUp() {
            final String searchKeyword = someString(10);
            final List<BoardCategory> boardCategories = List.of();
            final List<ag.act.model.Status> statuses = List.of(
                ag.act.model.Status.ACTIVE,
                ag.act.model.Status.DELETED_BY_ADMIN,
                ag.act.model.Status.DELETED_BY_USER
            );

            given(getPostsSearchDto.getSearchKeyword()).willReturn(searchKeyword);
            given(getPostsSearchDto.getBoardGroup()).willReturn(boardGroup);
            given(getPostsSearchDto.getPageRequest()).willReturn(pageRequest);
            given(getPostsSearchDto.getBoardCategories()).willReturn(boardCategories);
            given(getPostsSearchDto.getStatuses()).willReturn(statuses);
        }

        @Nested
        class WhenStockCodeSearch {

            @Mock
            private StockBoardGroupPostSearchByStockCodeService stockBoardGroupPostSearchByStockCodeService;

            @BeforeEach
            void setUp() {
                given(getPostsSearchDto.getPostSearchType()).willReturn(PostSearchType.STOCK_CODE);
                given(stockBoardGroupPostSearchServiceResolver.resolve(PostSearchType.STOCK_CODE))
                    .willReturn(stockBoardGroupPostSearchByStockCodeService);
                given(stockBoardGroupPostSearchByStockCodeService.getBoardPosts(getPostsSearchDto)).willReturn(pagePost);

                actualResponse = service.getBoardPosts(getPostsSearchDto);
            }

            @Test
            void shouldReturnPageObject() {
                assertThat(actualResponse, is(pagePost));
            }

            @Test
            void shouldCallPostRepository() {
                then(stockBoardGroupPostSearchServiceResolver).should().resolve(PostSearchType.STOCK_CODE);
                then(stockBoardGroupPostSearchByStockCodeService).should().getBoardPosts(getPostsSearchDto);
            }
        }

        @Nested
        class WhenTitleSearch {

            @Mock
            private StockBoardGroupPostSearchByTitleService stockBoardGroupPostSearchByTitleService;

            @BeforeEach
            void setUp() {
                given(getPostsSearchDto.getPostSearchType()).willReturn(PostSearchType.TITLE);
                given(stockBoardGroupPostSearchServiceResolver.resolve(PostSearchType.TITLE))
                    .willReturn(stockBoardGroupPostSearchByTitleService);
                given(stockBoardGroupPostSearchByTitleService.getBoardPosts(getPostsSearchDto)).willReturn(pagePost);

                actualResponse = service.getBoardPosts(getPostsSearchDto);
            }

            @Test
            void shouldReturnPageObject() {
                assertThat(actualResponse.getContent(), is(pagePost.getContent()));
            }

            @Test
            void shouldCallPostRepository() {
                then(stockBoardGroupPostSearchServiceResolver).should().resolve(PostSearchType.TITLE);
                then(stockBoardGroupPostSearchByTitleService).should().getBoardPosts(getPostsSearchDto);
            }
        }

        @Nested
        class WhenContentSearch {

            @Mock
            private StockBoardGroupPostSearchByContentService stockBoardGroupPostSearchByContentService;

            @BeforeEach
            void setUp() {
                given(getPostsSearchDto.getPostSearchType()).willReturn(PostSearchType.CONTENT);
                given(stockBoardGroupPostSearchServiceResolver.resolve(PostSearchType.CONTENT))
                    .willReturn(stockBoardGroupPostSearchByContentService);
                given(stockBoardGroupPostSearchByContentService.getBoardPosts(getPostsSearchDto)).willReturn(pagePost);

                actualResponse = service.getBoardPosts(getPostsSearchDto);
            }

            @Test
            void shouldReturnPageObject() {
                assertThat(actualResponse, is(pagePost));
            }

            @Test
            void shouldCallPostRepository() {
                then(stockBoardGroupPostSearchServiceResolver).should().resolve(PostSearchType.CONTENT);
                then(stockBoardGroupPostSearchByContentService).should().getBoardPosts(getPostsSearchDto);
            }
        }

        @Nested
        class WhenTitleAndContentSearch {

            @Mock
            private StockBoardGroupPostSearchByTitleAndContentService stockBoardGroupPostSearchByTitleAndContentService;

            @BeforeEach
            void setUp() {
                given(getPostsSearchDto.getPostSearchType()).willReturn(PostSearchType.TITLE_AND_CONTENT);
                given(stockBoardGroupPostSearchServiceResolver.resolve(PostSearchType.TITLE_AND_CONTENT))
                    .willReturn(stockBoardGroupPostSearchByTitleAndContentService);
                given(stockBoardGroupPostSearchByTitleAndContentService.getBoardPosts(getPostsSearchDto)).willReturn(pagePost);

                actualResponse = service.getBoardPosts(getPostsSearchDto);
            }

            @Test
            void shouldReturnPageObject() {
                assertThat(actualResponse, is(pagePost));
            }

            @Test
            void shouldCallPostRepository() {
                then(stockBoardGroupPostSearchServiceResolver).should().resolve(PostSearchType.TITLE_AND_CONTENT);
                then(stockBoardGroupPostSearchByTitleAndContentService).should().getBoardPosts(getPostsSearchDto);
            }
        }
    }
}
