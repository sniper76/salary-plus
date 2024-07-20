package ag.act.api.admin.category;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.model.BoardCategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class GetAdminBoardGroupCategoryApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/board-groups/{boardGroupName}/categories";

    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        mockServerEnvironmentIsProd(Boolean.TRUE);

        User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private BoardCategoryDataArrayResponse getResponse(MvcResult response) throws Exception {
        return itUtil.getResult(response, BoardCategoryDataArrayResponse.class);
    }

    private static BoardCategoryResponse createBoardCategoryResponse(String name, String displayName, Boolean isExclusiveToHolders) {
        return new BoardCategoryResponse().name(name).displayName(displayName).isExclusiveToHolders(isExclusiveToHolders);
    }

    private MvcResult callApi(String boardGroupName, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, boardGroupName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenValidBoardGroupNameProvided {

        private static Stream<Arguments> boardGroupAndCategoriesProvider() {
            return Stream.of(
                Arguments.of(
                    "analysis",
                    List.of(
                        createBoardCategoryResponse("DAILY_ACT", "분석자료", Boolean.FALSE),
                        createBoardCategoryResponse("SOLIDARITY_LEADER_LETTERS", "주주연대 공지", Boolean.FALSE),
                        createBoardCategoryResponse("HOLDER_LIST_READ_AND_COPY", "주주명부 열람/등사", Boolean.FALSE)
                    )
                ),
                Arguments.of(
                    "debate",
                    List.of(
                        createBoardCategoryResponse("DEBATE", "토론방", Boolean.FALSE)
                    )
                ),
                Arguments.of(
                    "action",
                    List.of(
                        createBoardCategoryResponse("SURVEYS", "설문", Boolean.FALSE),
                        createBoardCategoryResponse("DIGITAL_DELEGATION", "의결권위임", Boolean.FALSE),
                        createBoardCategoryResponse("CO_HOLDING_ARRANGEMENTS", "공동보유", Boolean.FALSE),
                        createBoardCategoryResponse("ETC", "10초 서명", Boolean.FALSE)
                    )
                ),
                Arguments.of(
                    "globalboard",
                    List.of(
                        createBoardCategoryResponse("STOCKHOLDER_ACTION", "주주행동", Boolean.FALSE),
                        createBoardCategoryResponse("STOCK_ANALYSIS_DATA", "분석자료", Boolean.FALSE)
                    )
                ),
                Arguments.of(
                    "globalevent",
                    List.of(
                        new BoardCategoryResponse().name("EVENT,CAMPAIGN").displayName("이벤트/캠페인").isGroup(true).subCategories(
                            List.of(
                                new BoardCategoryResponse().name("EVENT").displayName("이벤트").badgeColor("FF910A"),
                                new BoardCategoryResponse().name("CAMPAIGN").displayName("캠페인").badgeColor("67C91C")
                            )
                        ),
                        new BoardCategoryResponse().name("NOTICE").displayName("공지사항").badgeColor("355CE9")
                    )
                ),
                Arguments.of(
                    "globalcommunity",
                    List.of(
                        createBoardCategoryResponse("FREE_DEBATE", "자유게시판", Boolean.FALSE)
                    )
                ),
                Arguments.of(
                    "act_best",
                    List.of(
                        createBoardCategoryResponse("ACT_BEST_STOCK", "종목", Boolean.TRUE),
                        createBoardCategoryResponse("ACT_BEST_SHAREHOLDER_ACTION_NEWS", "주주행동 News", Boolean.FALSE),
                        createBoardCategoryResponse("ACT_BEST_COMMUNITY", "자유게시판", Boolean.FALSE)
                    )
                )
            );
        }

        @ParameterizedTest(name = "{index} => boardGroupName=''{0}''")
        @MethodSource("boardGroupAndCategoriesProvider")
        void shouldReturnCorrectBoardCategories(String boardGroupName, List<BoardCategoryResponse> expectedBoardCategories) throws Exception {

            // When
            final BoardCategoryDataArrayResponse result = getResponse(callApi(boardGroupName, status().isOk()));

            // Then
            assertThat(result.getData(), is(equalTo(expectedBoardCategories)));
        }
    }

    @Nested
    class WhenInvalidBoardGroupName {
        @DisplayName("Should return 400 response code when invalid board group name " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {

            // Given
            final String boardGroupName = someAlphanumericString(10);

            // When
            final ResultMatcher badRequest = status().isBadRequest();

            final MvcResult response = callApi(boardGroupName, badRequest);

            // Then
            itUtil.assertErrorResponse(response, 400, "존재하지 않는 게시판 그룹 이름입니다.");
        }
    }
}
