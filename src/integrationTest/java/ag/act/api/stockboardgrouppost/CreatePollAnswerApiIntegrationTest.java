package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.SelectionOption;
import ag.act.model.CreatePollAnswerItemRequest;
import ag.act.model.PostPollAnswerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreatePollAnswerApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/polls/{pollId}/answers";

    private ag.act.model.PostPollAnswerRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Poll poll;
    private UserHoldingStock userHoldingStock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());
        post = itUtil.createPoll(post);
        poll = post.getFirstPoll();

        LocalDateTime localDateTime = LocalDateTime.now();
        poll.setTargetStartDate(localDateTime);
        poll.setTargetEndDate(localDateTime.plusDays(5));
        poll.setSelectionOption(SelectionOption.MULTIPLE_ITEMS);
        itUtil.updatePost(post);
        itUtil.createSolidarity(stock.getCode());
        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private ag.act.model.PostPollAnswerRequest genRequest() {

        ag.act.model.CreatePollAnswerItemRequest answerItemRequestOne = new CreatePollAnswerItemRequest();
        ag.act.model.CreatePollAnswerItemRequest answerItemRequestTwo = new CreatePollAnswerItemRequest();
        answerItemRequestOne.setPollItemId(poll.getPollItemList().get(0).getId());
        answerItemRequestTwo.setPollItemId(poll.getPollItemList().get(1).getId());

        List<ag.act.model.CreatePollAnswerItemRequest> answerItemRequestList = new ArrayList<>();
        answerItemRequestList.add(answerItemRequestOne);
        answerItemRequestList.add(answerItemRequestTwo);

        ag.act.model.PostPollAnswerRequest request = new PostPollAnswerRequest();
        request.pollAnswer(answerItemRequestList);
        return request;
    }

    @Nested
    class WhenCreatePostAnswer {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), poll.getId())
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.PollAnswerDataArrayResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.PollAnswerDataArrayResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.PollAnswerDataArrayResponse result) {
            final List<ag.act.model.PollAnswerResponse> responseList = result.getData();

            for (int k = 0; k < responseList.size(); k++) {
                ag.act.model.PollAnswerResponse response = responseList.get(k);

                assertThat(response.getId(), is(notNullValue()));
                assertThat(response.getPollId(), is(poll.getId()));
                assertThat(response.getUserId(), is(user.getId()));
                assertThat(response.getPollItemId(), is(request.getPollAnswer().get(k).getPollItemId()));
                assertThat(response.getStockQuantity(), is(userHoldingStock.getQuantity()));
            }
        }

    }

    @Nested
    class WhenUpdatePostAnswer {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    put(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), poll.getId())
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.PollAnswerDataArrayResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.PollAnswerDataArrayResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.PollAnswerDataArrayResponse result) {
            final List<ag.act.model.PollAnswerResponse> responseList = result.getData();

            for (int k = 0; k < responseList.size(); k++) {
                ag.act.model.PollAnswerResponse response = responseList.get(k);

                assertThat(response.getId(), is(notNullValue()));
                assertThat(response.getPollId(), is(poll.getId()));
                assertThat(response.getUserId(), is(user.getId()));
                assertThat(response.getPollItemId(), is(request.getPollAnswer().get(k).getPollItemId()));
                assertThat(response.getStockQuantity(), is(userHoldingStock.getQuantity()));
            }
        }

    }

}
