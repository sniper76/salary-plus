package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.LatestUserPostsView;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.repository.LatestUserPostsViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetBoardGroupPostsAopApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    @Autowired
    private LatestUserPostsViewRepository latestUserPostsViewRepository;
    @Autowired
    private GlobalBoardManager globalBoardManager;
    private String jwt;
    private User user;
    private Stock stock;
    private String stockCode;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;

    @BeforeEach
    void setUp() {
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        stockCode = stock.getCode();
        itUtil.createUserHoldingStock(stockCode, user);
    }

    private void callApi(String boardCategoryName) throws Exception {
        Map<String, Object> params = Map.of("boardCategories", Optional.ofNullable(boardCategoryName).orElse(""));

        mockMvc
            .perform(
                get(TARGET_API, stockCode, boardGroup.name())
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertLatestUserPostsView(
        LatestUserPostsView latestUserPostsView,
        BoardGroup boardGroup,
        BoardCategory boardCategory,
        PostsViewType postsViewType
    ) {
        assertThat(latestUserPostsView.getUser().getId(), is(user.getId()));
        assertThat(latestUserPostsView.getStock().getCode(), is(stockCode));
        assertThat(latestUserPostsView.getBoardGroup(), is(boardGroup));
        assertThat(latestUserPostsView.getBoardCategory(), is(boardCategory));
        assertThat(latestUserPostsView.getPostsViewType(), is(postsViewType));
    }

    @Nested
    class WhenBoardGroupGlobalCommunity {
        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALCOMMUNITY;
            stockCode = globalBoardManager.getStockCode();
        }

        @Nested
        class AndCategoryNotExist {

            @Test
            void shouldCreateLatestUserPostsView() throws Exception {
                final PostsViewType postsViewType = PostsViewType.BOARD_GROUP;
                callApi(null);

                final LatestUserPostsView latestUserPostsView =
                    itUtil.getLatestUserPostsView(stockCode, user.getId(), boardGroup, null, postsViewType);

                assertLatestUserPostsView(
                    latestUserPostsView,
                    boardGroup,
                    null,
                    postsViewType
                );
            }
        }
    }

    @Nested
    class WhenBoardGroupGlobalBoard {
        @BeforeEach
        void setUp() {
            boardGroup = BoardGroup.GLOBALBOARD;
            stockCode = globalBoardManager.getStockCode();
        }

        @Nested
        class AndCategoryNotExist {

            @Test
            void shouldCreateLatestUserPostsView() throws Exception {
                final PostsViewType postsViewType = PostsViewType.BOARD_GROUP;
                callApi(null);

                final LatestUserPostsView latestUserPostsView =
                    itUtil.getLatestUserPostsView(stockCode, user.getId(), boardGroup, null, postsViewType);

                assertLatestUserPostsView(
                    latestUserPostsView,
                    boardGroup,
                    null,
                    postsViewType
                );
            }
        }

        @Nested
        class AndCategoryExist {
            @BeforeEach
            void setUp() {
                boardCategory = someBoardCategory(boardGroup);
            }

            @Test
            void shouldNotCreateLatestUserPostsView() throws Exception {
                final PostsViewType postsViewType = PostsViewType.BOARD_GROUP;
                callApi(boardCategory.getName());

                final LatestUserPostsView latestUserPostsView =
                    itUtil.getLatestUserPostsView(stockCode, user.getId(), boardGroup, null, postsViewType);

                assertLatestUserPostsView(
                    latestUserPostsView,
                    boardGroup,
                    null,
                    postsViewType
                );
            }
        }
    }

    @Nested
    class WhenBoardGroupGlobalEvent {
        private final String boardCategoryName = "EVENT,CAMPAIGN";

        @BeforeEach
        void setUp() {
            stockCode = globalBoardManager.getStockCode();
            boardGroup = BoardGroup.GLOBALEVENT;
        }

        @Nested
        class AndCategoryNotExist {

            @Test
            void shouldCreateLatestUserPostsView() throws Exception {
                final PostsViewType postsViewType = PostsViewType.BOARD_GROUP;
                callApi(boardCategoryName);

                final LatestUserPostsView latestUserPostsView =
                    itUtil.getLatestUserPostsView(stockCode, user.getId(), boardGroup, null, postsViewType);

                assertLatestUserPostsView(
                    latestUserPostsView,
                    boardGroup,
                    null,
                    postsViewType
                );
            }
        }

        @Nested
        class AndCategoryExist {
            @BeforeEach
            void setUp() {
                boardCategory = someBoardCategory(boardGroup);
            }

            @Test
            void shouldCreateLatestUserPostsView() throws Exception {
                final PostsViewType postsViewType = PostsViewType.BOARD_GROUP;
                callApi(boardCategoryName);

                final LatestUserPostsView latestUserPostsView =
                    itUtil.getLatestUserPostsView(stockCode, user.getId(), boardGroup, null, postsViewType);

                assertLatestUserPostsView(
                    latestUserPostsView,
                    boardGroup,
                    null,
                    postsViewType
                );
            }
        }
    }

    @Nested
    class WhenBoardGroupForStock {
        @BeforeEach
        void setUp() {
            boardGroup = someBoardGroupForStock();
            stockCode = globalBoardManager.getStockCode();
        }

        @Nested
        class AndCategoryNotExist {

            @Test
            void shouldCreateLatestUserPostsView() throws Exception {
                final PostsViewType postsViewType = PostsViewType.BOARD_GROUP;
                callApi(null);

                final LatestUserPostsView latestUserPostsView =
                    itUtil.getLatestUserPostsView(stockCode, user.getId(), boardGroup, null, postsViewType);

                assertLatestUserPostsView(
                    latestUserPostsView,
                    boardGroup,
                    null,
                    postsViewType
                );
            }
        }

        @Nested
        class AndCategoryExist {

            @BeforeEach
            void setUp() {
                boardCategory = someBoardCategory(boardGroup);
            }

            @Test
            void shouldNotCreateLatestUserPostsView() throws Exception {
                callApi(boardCategory.getName());

                assertThat(latestUserPostsViewRepository.findAll().size(), is(0));
            }
        }
    }
}
