package ag.act.facade.admin;

import ag.act.converter.post.BoardGroupPostResponseConverter;
import ag.act.dto.GetPostsSearchDto;
import ag.act.entity.Post;
import ag.act.facade.admin.post.AdminPostFacade;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminPostFacadeTest {

    @InjectMocks
    private AdminPostFacade facade;
    @Mock
    private PostService postService;
    @Mock
    private StockServiceValidator stockServiceValidator;
    @Mock
    private BoardGroupPostResponseConverter boardGroupPostResponseConverter;

    @Nested
    class GetPosts {

        @Mock
        private GetPostsSearchDto getPostsSearchDto;
        private GetBoardGroupPostResponse actualResponse;
        @Mock
        private GetBoardGroupPostResponse expectedResponse;
        private String searchKeyword;
        @Mock
        private Page<Post> postPage;

        @BeforeEach
        void setUp() {
            searchKeyword = someString(10);

            given(getPostsSearchDto.getSearchKeyword()).willReturn(searchKeyword);
            given(postService.getBoardPosts(getPostsSearchDto)).willReturn(postPage);
            given(boardGroupPostResponseConverter.convert(postPage)).willReturn(expectedResponse);
        }

        @Nested
        class WhenStockCodeSearch extends DefaultTestCases {

            @BeforeEach
            void setUp() {

                given(getPostsSearchDto.isSearchByStockCode()).willReturn(true);
                willDoNothing().given(stockServiceValidator).validateStockCode(searchKeyword);

                actualResponse = facade.getPosts(getPostsSearchDto);
            }

            @Test
            void shouldValidateStockCode() {
                then(stockServiceValidator).should().validateStockCode(searchKeyword);
            }
        }

        @Nested
        class WhenTitleSearch extends DefaultTestCases {

            @BeforeEach
            void setUp() {

                given(getPostsSearchDto.isSearchByStockCode()).willReturn(false);

                actualResponse = facade.getPosts(getPostsSearchDto);
            }

            @Test
            void shouldValidateStockCode() {
                then(stockServiceValidator).should(never()).validateStockCode(searchKeyword);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldGetBoardPosts() {
                then(postService).should().getBoardPosts(getPostsSearchDto);
            }

            @Test
            void shouldConvertPostPage() {
                then(boardGroupPostResponseConverter).should().convert(postPage);
            }

            @Test
            void shouldReturnResponse() {
                assertThat(actualResponse, is(expectedResponse));
            }
        }
    }
}