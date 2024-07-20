package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.SelectionOption;
import ag.act.model.CreatePollAnswerItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateSingleSelectionPollAnswerApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/polls/{pollId}/answers";

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Long postId;
    private Long pollId;
    private ag.act.model.PostPollAnswerRequest request;
    private ag.act.model.PostPollAnswerRequest requestUpdate;
    private UserHoldingStock userHoldingStock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());

        LocalDateTime currentDate = LocalDateTime.now();
        post = itUtil.createPoll(post, 2, SelectionOption.SINGLE_ITEM, currentDate.minusDays(1), currentDate.plusDays(10));

        postId = post.getId();
        pollId = post.getFirstPoll().getId();
    }

    @Nested
    class WhenCreatePollAnswer {
        @BeforeEach
        void setUp() {
            final List<PollItem> requestPollItemList = getRequestPollItemListFromPoll();

            request = genRequest(requestPollItemList);
        }

        private List<PollItem> getRequestPollItemListFromPoll() {
            final List<PollItem> pollItemList = post.getFirstPoll().getPollItemList();

            return isSingleItemSelectionOption()
                ? List.of(pollItemList.get(0))
                : new ArrayList<>(pollItemList);
        }

        private boolean isSingleItemSelectionOption() {
            return post.getFirstPoll().getSelectionOption() == SelectionOption.SINGLE_ITEM;
        }

        private ag.act.model.PostPollAnswerRequest genRequest(List<PollItem> pollItems) {
            request = new ag.act.model.PostPollAnswerRequest();

            request.setPollAnswer(pollItems.stream().map(element -> {
                ag.act.model.CreatePollAnswerItemRequest itemRequest = new CreatePollAnswerItemRequest();
                itemRequest.setPollItemId(element.getId());
                return itemRequest;
            }).collect(Collectors.toList()));
            return request;
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stock.getCode(), board.getGroup(), postId, pollId)
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
            final List<ag.act.model.PollAnswerResponse> answerResponse = result.getData();

            final List<CreatePollAnswerItemRequest> responsePollItems = request.getPollAnswer();

            assertThat(answerResponse.size(), is(responsePollItems.size()));

            for (int index = 0; index < answerResponse.size(); index++) {
                ag.act.model.PollAnswerResponse responsePollAnswer = answerResponse.get(index);
                ag.act.model.CreatePollAnswerItemRequest requestPollItem = responsePollItems.get(index);

                assertThat(responsePollAnswer.getPollItemId(), is(
                    requestPollItem.getPollItemId()
                ));
                assertThat(responsePollAnswer.getStockQuantity(), is(
                    userHoldingStock.getQuantity()
                ));
            }
        }
    }

    @Nested
    class WhenUpdatePollAnswer {
        private PollItem insertItem;

        @BeforeEach
        void setUp() {
            insertItem = post.getFirstPoll().getPollItemList().get(0);
            final PollItem deleteItem = post.getFirstPoll().getPollItemList().get(post.getFirstPoll().getPollItemList().size() - 1);

            requestUpdate = genRequest(insertItem);

            itUtil.createPollAnswer(
                user.getId(), pollId, deleteItem.getId()
            );
        }

        private ag.act.model.PostPollAnswerRequest genRequest(PollItem pollItem) {
            requestUpdate = new ag.act.model.PostPollAnswerRequest();

            ag.act.model.CreatePollAnswerItemRequest itemRequest = new CreatePollAnswerItemRequest();
            itemRequest.setPollItemId(pollItem.getId());

            requestUpdate.addPollAnswerItem(itemRequest);
            return requestUpdate;
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    put(TARGET_API, stock.getCode(), board.getGroup(), postId, pollId)
                        .content(objectMapperUtil.toRequestBody(requestUpdate))
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
            final List<ag.act.model.PollAnswerResponse> answerResponse = result.getData();

            for (ag.act.model.PollAnswerResponse responseAnswer : answerResponse) {
                assertThat(responseAnswer.getPollItemId(), is(insertItem.getId()));
                assertThat(responseAnswer.getStockQuantity(), is(userHoldingStock.getQuantity()));
            }
        }
    }
}
