package ag.act.api.stockboardgrouppost;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Push;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.model.CreatePollItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class CreatePostBySolidarityLeaderApiIntegrationTest extends AbstractCommonIntegrationTest {

    public static final String TARGET_URI = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    private BoardCategory boardCategory;
    private BoardGroup boardGroup;
    private User currentUser;
    private Solidarity solidarity;
    private Stock stock;
    private CreatePostRequest createPostRequest;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());

        currentUser = itUtil.createUser();
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);
        jwt = itUtil.createJwt(currentUser.getId());
    }

    @Nested
    class BoardCategoryIsDebate {

        @BeforeEach
        void setUp() {
            boardCategory = BoardCategory.DEBATE;
            boardGroup = boardCategory.getBoardGroup();
            itUtil.createBoard(stock, boardCategory.getBoardGroup(), boardCategory);

            createPostRequest = new CreatePostRequest()
                .title(someString(10))
                .content(someAlphanumericString(100))
                .isActive(Boolean.TRUE)
                .boardCategory(boardCategory.name())
                .polls(
                    List.of(
                        new CreatePollRequest()
                            .title(someString(10))
                            .content(someAlphanumericString(100))
                            .voteType(VoteType.SHAREHOLDER_BASED.name())
                            .selectionOption(SelectionOption.SINGLE_ITEM.name())
                            .pollItems(
                                List.of(
                                    new CreatePollItemRequest()
                                        .text(someString(10))
                                )
                            ).targetStartDate(Instant.now())
                            .targetEndDate(Instant.now().plusSeconds(300))
                    )
                );
        }

        @Nested
        class WhenUserIsLeader {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());
            }

            @Test
            @DisplayName("주주대표는 DEBATE 게시판에 글을 작성할 수 있고, 작성하면 푸시가 나간다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                PostDetailsDataResponse response = getResponse(mvcResult);

                assertPostResponse(response);
                assertPush(response.getData().getId());
            }
        }

        @Nested
        class WhenUserIsNotLeader {
            @BeforeEach
            void setUp() {
                createUserAsSolidarityLeaderOfAnotherStock();
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                PostDetailsDataResponse response = getResponse(mvcResult);

                assertPostResponse(response);
                assertPushNotExist(response.getData().getId());
            }
        }

        @Nested
        class WhenUserIsAdmin {

            @BeforeEach
            void setUp() {
                currentUser = itUtil.createAdminUser();
                itUtil.createUserHoldingStock(stock.getCode(), currentUser);
                jwt = itUtil.createJwt(currentUser.getId());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                PostDetailsDataResponse response = getResponse(mvcResult);

                assertPostResponse(response);
                assertPushNotExist(response.getData().getId());
            }
        }
    }

    @Nested
    class WhenOtherBoardCategory {

        @BeforeEach
        void setUp() {
            boardCategory = someThing(BoardCategory.DAILY_ACT, BoardCategory.SOLIDARITY_LEADER_LETTERS);
            boardGroup = boardCategory.getBoardGroup();
            itUtil.createBoard(stock, boardGroup, boardCategory);

            createPostRequest = new CreatePostRequest()
                .title(someString(10))
                .content(someAlphanumericString(100))
                .isActive(Boolean.TRUE)
                .boardCategory(boardCategory.name());
        }

        @Nested
        class WhenUserIsLeader {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());
            }

            @Nested
            class WhenNormal {

                @Test
                @DisplayName("주주대표는 DAILY_ACT, SOLIDARITY_LEADER_LETTERS 게시판에 글을 작성할 수 있고, 작성하면 푸시가 나간다")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    PostDetailsDataResponse response = getResponse(mvcResult);

                    assertPostResponse(response);
                    assertPush(response.getData().getId());
                }
            }


            @Nested
            class WhenPostRequestIsAnonymously {

                @BeforeEach
                void setUp() {
                    createPostRequest.setIsAnonymous(Boolean.TRUE);
                }

                @Test
                @DisplayName("익명으로 작성할 경우 푸시가 나가지 않는다.")
                void shouldReturnSuccess() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());

                    PostDetailsDataResponse response = getResponse(mvcResult);

                    assertPostResponse(response);
                    assertPushNotExist(response.getData().getId());
                }
            }
        }

        @Nested
        class WhenUserIsNotLeader {

            @BeforeEach
            void setUp() {
                createUserAsSolidarityLeaderOfAnotherStock();
            }

            @Test
            @DisplayName("주주대표가 아닌 경우 해당 카테고리들에 글을 쓸 수 없다.")
            void shouldReturnBadRequest() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(
                    mvcResult,
                    BAD_REQUEST_STATUS,
                    "%s 게시판에 글쓰기 권한이 없습니다.".formatted(boardCategory.getDisplayName()));
            }
        }

        @Nested
        class WhenUserIsAdmin {

            @BeforeEach
            void setUp() {
                currentUser = itUtil.createAdminUser();
                itUtil.createUserHoldingStock(stock.getCode(), currentUser);
                jwt = itUtil.createJwt(currentUser.getId());
            }

            @Test
            @DisplayName("어드민이 글을 작성했을 경우 푸시가 나가지 않는다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                PostDetailsDataResponse response = getResponse(mvcResult);

                assertPostResponse(response);
                assertPushNotExist(response.getData().getId());
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                post(TARGET_URI, stock.getCode(), boardGroup.name())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapperUtil.toRequestBody(createPostRequest))
                    .headers(headers(jwt(jwt))))
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertPostResponse(PostDetailsDataResponse response) {
        assertThat(response.getData().getTitle(), is(createPostRequest.getTitle()));
        assertThat(response.getData().getContent(), is(createPostRequest.getContent()));
        assertThat(response.getData().getIsActive(), is(createPostRequest.getIsActive()));
        assertThat(response.getData().getBoardCategory().getName(), is(createPostRequest.getBoardCategory()));
    }

    private void assertPushNotExist(Long postId) {
        List<Push> pushes = itUtil.findAllPushesByPostId(postId);
        assertThat(pushes.size(), is(0));
    }

    private void assertPush(Long postId) {
        List<Push> pushes = itUtil.findAllPushesByPostId(postId);

        assertThat(pushes.size(), is(1));
        final Push push = pushes.get(0);

        assertThat(push.getTitle(), is("주주대표 새글 알림"));
        assertThat(push.getContent(), is("주주대표가 새로운 글을 작성하였습니다."));
    }

    private void createUserAsSolidarityLeaderOfAnotherStock() {
        Stock anotherStock = itUtil.createStock();
        Solidarity anotherSolidarity = itUtil.createSolidarity(anotherStock.getCode());
        itUtil.createSolidarityLeader(anotherSolidarity, currentUser.getId()); // leader of another stock solidarity
    }

    private PostDetailsDataResponse getResponse(MvcResult mvcResult) throws Exception {
        return itUtil.getResult(mvcResult, PostDetailsDataResponse.class);
    }
}
