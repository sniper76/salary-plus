package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.UpdatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdatePostApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private UpdatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private Long postId;
    private User currentUser;
    private Solidarity solidarity;

    @BeforeEach
    void setUp() {
        itUtil.init();
        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, currentUser.getId(), Boolean.FALSE);
        postId = post.getId();

        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);
    }

    @Nested
    class WhenSolidarityLeaderUpdatePost {

        private Push push;

        @BeforeEach
        void setUp() {
            itUtil.createSolidarityLeader(solidarity, currentUser.getId());
            request = genRequest();
        }

        @Nested
        class AndPushSendStatusIsReady {
            @BeforeEach
            void setUp() {
                createPush(PushSendStatus.READY);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());
                final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

                assertPostDetailResponse(result);
                assertPushFromDatabase(push.getTargetDatetime().plusMinutes(10L));
            }
        }

        @Nested
        class AndPushSendStatusIsNotReady {

            @BeforeEach
            void setUp() {
                createPush(PushSendStatus.COMPLETE);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());
                final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

                assertPostDetailResponse(result);
                assertPushFromDatabase(push.getTargetDatetime());
            }

        }

        private UpdatePostRequest genRequest() {
            UpdatePostRequest request = new UpdatePostRequest();
            request.setTitle(someString(10));
            request.setContent(someAlphanumericString(300));
            return request;
        }

        private void createPush(PushSendStatus pushSendStatus) {
            push = itUtil.createPush(someString(10), stock, PushTargetType.STOCK);
            push.setPostId(postId);
            push.setSendType(PushSendType.IMMEDIATELY);
            push.setSendStatus(pushSendStatus);
            push.setTargetDatetime(post.getActiveStartDate());
            itUtil.updatePush(push);

            post.setPushId(push.getId());
            itUtil.updatePost(post);
        }

        private void assertPushFromDatabase(LocalDateTime expectedTargetDatetime) {
            final Push actualPush = itUtil.findPush(push.getId()).orElseThrow();
            assertThat(actualPush.getTitle(), is(push.getTitle()));
            assertThat(actualPush.getContent(), is(push.getContent()));
            assertThat(actualPush.getSendType(), is(push.getSendType()));
            assertThat(actualPush.getSendStatus(), is(push.getSendStatus()));
            assertTime(actualPush.getTargetDatetime(), expectedTargetDatetime);
        }

        private void assertPostDetailResponse(PostDetailsDataResponse result) {
            final PostDetailsResponse postDetailsResponse = result.getData();
            assertThat(postDetailsResponse.getTitle(), is(request.getTitle()));
            assertThat(postDetailsResponse.getContent(), is(request.getContent()));
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stock.getCode(), board.getGroup(), postId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
