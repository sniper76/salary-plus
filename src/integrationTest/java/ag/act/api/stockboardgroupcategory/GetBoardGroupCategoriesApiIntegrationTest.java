package ag.act.api.stockboardgroupcategory;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.model.BoardCategoryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static ag.act.TestUtil.someAppVersionExcludesWeb;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static ag.act.itutil.authentication.AuthenticationTestUtil.userAgent;
import static ag.act.itutil.authentication.AuthenticationTestUtil.xAppVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@SuppressWarnings("checkstyle:LineLength")
class GetBoardGroupCategoriesApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/categories";

    private String jwt;
    private Stock stock;

    @Autowired
    private GlobalBoardManager globalBoardManager;

    @BeforeEach
    void setUp() {
        itUtil.init();
        mockServerEnvironmentIsProd(Boolean.TRUE);

        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private BoardCategoryDataArrayResponse getResponse(MvcResult response)
        throws JsonProcessingException, UnsupportedEncodingException {

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.BoardCategoryDataArrayResponse.class
        );
    }

    @Nested
    class GetCategoriesInWeb {

        @Nested
        class ForGuest {

            @Nested
            class WhenBoardGroupIsGlobal {
                @BeforeEach
                void setUp() {
                    stock = itUtil.findStock(globalBoardManager.getStockCode());
                }

                @Test
                @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=GLOBALBOARD")
                void shouldReturnSuccessWithGlobalBoardGroup() throws Exception {

                    // Given
                    final String boardGroupName = "GLOBALBOARD";

                    final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                        new BoardCategoryResponse().name("STOCKHOLDER_ACTION").displayName("주주행동"),
                        new BoardCategoryResponse().name("STOCK_ANALYSIS_DATA").displayName("분석자료")
                    );

                    // When
                    final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(status().isOk(), boardGroupName));

                    // Then
                    assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                }
            }

            @Nested
            class WhenBoardGroupIsNotGlobal {

                @Test
                @DisplayName("Should return 401 response code when call " + TARGET_API + " with boardGroupName=ANALYSIS")
                void shouldReturnUnauthorized() throws Exception {

                    // Given
                    final String boardGroupName = "ANALYSIS";

                    // When
                    final MvcResult mvcResult = callApi(status().isUnauthorized(), boardGroupName);

                    // Then
                    itUtil.assertErrorResponse(
                        mvcResult,
                        401,
                        "인가되지 않은 접근입니다."
                    );
                }
            }

            private MvcResult callApi(ResultMatcher resultMatcher, String boardGroupName) throws Exception {
                return mockMvc
                    .perform(
                        get(TARGET_API, stock.getCode(), boardGroupName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .headers(headers(xAppVersion(X_APP_VERSION_WEB), userAgent(USER_AGENT_WEB)))
                    )
                    .andExpect(resultMatcher)
                    .andReturn();
            }
        }

        @Nested
        class ForUser {
            private String boardGroupName;

            @BeforeEach
            void setUp() {
                stock = itUtil.findStock(globalBoardManager.getStockCode());
            }

            @Test
            @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=GLOBALBOARD")
            void shouldReturnSuccessWithGlobalBoardGroup() throws Exception {

                // Given
                boardGroupName = "GLOBALBOARD";

                final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                    new BoardCategoryResponse().name("STOCKHOLDER_ACTION").displayName("주주행동"),
                    new BoardCategoryResponse().name("STOCK_ANALYSIS_DATA").displayName("분석자료")
                );

                // When
                final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(status().isOk()));

                // Then
                assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
            }

            private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
                return mockMvc
                    .perform(
                        get(TARGET_API, stock.getCode(), boardGroupName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .headers(headers(jwt(jwt), xAppVersion(X_APP_VERSION_WEB), userAgent(USER_AGENT_WEB)))
                    )
                    .andExpect(resultMatcher)
                    .andReturn();
            }
        }
    }


    @Nested
    class GetCategoriesInNotWebForUser {

        @Nested
        class WhenBoardGroupNameNotVirtual {
            @Nested
            class WhenAllBoardsExists {

                @BeforeEach
                void setUp() {
                    itUtil.createBoardsForAllCategories(stock);
                }

                @Nested
                class Success {

                    @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=globalevent")
                    @Test
                    void shouldReturnSuccessWithEventBoardGroup() throws Exception {

                        // Given
                        final String boardGroupName = "globalevent";

                        final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                            new BoardCategoryResponse().name("EVENT,CAMPAIGN").displayName("이벤트/캠페인").isGroup(true).subCategories(
                                List.of(
                                    new BoardCategoryResponse().name("EVENT").displayName("이벤트").badgeColor("FF910A"),
                                    new BoardCategoryResponse().name("CAMPAIGN").displayName("캠페인").badgeColor("67C91C")
                                )
                            ),
                            new BoardCategoryResponse().name("NOTICE").displayName("공지사항").badgeColor("355CE9")
                        );

                        // When
                        final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName));

                        // Then
                        assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                    }


                    @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=analysis")
                    @Test
                    void shouldReturnSuccessWithAnalysisBoardGroup() throws Exception {

                        // Given
                        final String boardGroupName = "analysis";

                        final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                            new BoardCategoryResponse().name("DAILY_ACT").displayName("분석자료"),
                            new BoardCategoryResponse().name("SOLIDARITY_LEADER_LETTERS").displayName("주주연대 공지"),
                            new BoardCategoryResponse().name("HOLDER_LIST_READ_AND_COPY").displayName("주주명부 열람/등사")
                        );

                        // When
                        final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName));

                        // Then
                        assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=debate")
                    @Test
                    void shouldReturnSuccessWithDebateBoardGroup() throws Exception {

                        // Given
                        final String boardGroupName = "debate";

                        final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                            new BoardCategoryResponse().name("DEBATE").displayName("토론방")
                        );

                        // When
                        final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName));

                        // Then
                        assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=action")
                    @Test
                    void shouldReturnSuccessWithActionBoardGroup() throws Exception {

                        // Given
                        final String boardGroupName = "action";

                        final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                            new BoardCategoryResponse().name("SURVEYS").displayName("설문"),
                            new BoardCategoryResponse().name("DIGITAL_DELEGATION").displayName("의결권위임"),
                            new BoardCategoryResponse().name("CO_HOLDING_ARRANGEMENTS").displayName("공동보유"),
                            new BoardCategoryResponse().name("ETC").displayName("10초 서명")
                        );

                        // When
                        final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName));

                        // Then
                        assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API + " with boardGroupName=globalboard")
                    @Test
                    void shouldReturnSuccessWithGlobalBoardGroup() throws Exception {

                        // Given
                        final String boardGroupName = "globalboard";

                        final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                            new BoardCategoryResponse().name("STOCKHOLDER_ACTION").displayName("주주행동"),
                            new BoardCategoryResponse().name("STOCK_ANALYSIS_DATA").displayName("분석자료")
                        );

                        // When
                        final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName));

                        // Then
                        assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                    }
                }
            }

            @Nested
            class WhenBoardGroupNameVirtual {
                @Test
                void shouldReturnSuccess() throws Exception {
                    // Given
                    String boardGroupName = "act_best";

                    final List<BoardCategoryResponse> expectedBoardCategories = List.of(
                        new BoardCategoryResponse()
                            .name("ACT_BEST_STOCK")
                            .isExclusiveToHolders(Boolean.TRUE)
                            .displayName("종목"),
                        new BoardCategoryResponse()
                            .name("ACT_BEST_SHAREHOLDER_ACTION_NEWS")
                            .isExclusiveToHolders(Boolean.FALSE)
                            .displayName("주주행동 News"),
                        new BoardCategoryResponse()
                            .name("ACT_BEST_COMMUNITY")
                            .isExclusiveToHolders(Boolean.FALSE)
                            .displayName("자유게시판")
                    );

                    // When
                    final ag.act.model.BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName));

                    // Then
                    assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
                }
            }

            @Nested
            class AndInvalidBoardGroupName {
                @DisplayName("Should return 400 response code when invalid board group name " + TARGET_API)
                @Test
                void shouldReturnBadRequest() throws Exception {

                    // Given
                    final String boardGroupName = someAlphanumericString(10);

                    // When
                    final MvcResult response = mockMvc
                        .perform(
                            get(TARGET_API, stock.getCode(), boardGroupName)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .headers(headers(jwt(jwt)))
                        )
                        .andExpect(status().isBadRequest())
                        .andReturn();

                    // Then
                    itUtil.assertErrorResponse(response, 400, "존재하지 않는 게시판 그룹 이름입니다.");
                }
            }
        }

        private MvcResult callApi(String boardGroupName) throws Exception {
            return mockMvc
                .perform(
                    get(TARGET_API, stock.getCode(), boardGroupName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt), xAppVersion(someAppVersionExcludesWeb()), userAgent(USER_AGENT_NOT_WEB)))
                )
                .andExpect(status().isOk())
                .andReturn();
        }
    }
}
