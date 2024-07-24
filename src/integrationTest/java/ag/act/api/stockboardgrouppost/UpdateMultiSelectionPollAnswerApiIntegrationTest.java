package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Board;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.model.CreatePollAnswerItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateMultiSelectionPollAnswerApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/polls/{pollId}/answers";

    @Autowired
    private GlobalBoardManager globalBoardManager;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Long postId;
    private Long pollId;
    private UserHoldingStock userHoldingStock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
    }

    private void setUpForNormalStock() {
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());

        LocalDateTime currentDate = LocalDateTime.now();
        post = itUtil.createPoll(post, 3, SelectionOption.MULTIPLE_ITEMS, currentDate.minusDays(1), currentDate.plusDays(10));

        postId = post.getId();
        pollId = post.getFirstPoll().getId();
    }

    private void setUpForGlobalStock() {
        stock = itUtil.findStock(globalBoardManager.getStockCode());
        board = itUtil.findBoardNonNull(stock.getCode(), someBoardCategory(BoardGroup.GLOBALBOARD));
        post = itUtil.createPost(board, user.getId());

        LocalDateTime currentDate = LocalDateTime.now();
        post = itUtil.createPoll(post, 3, SelectionOption.MULTIPLE_ITEMS, currentDate.minusDays(1), currentDate.plusDays(10));

        postId = post.getId();
        pollId = post.getFirstPoll().getId();
    }

    @Nested
    class WhenCreateMultiPollAnswer {
        private ag.act.model.PostPollAnswerRequest request;

        private ag.act.model.PostPollAnswerRequest genRequest(List<PollItem> pollItems) {
            request = new ag.act.model.PostPollAnswerRequest();

            request.setPollAnswer(pollItems.stream().map(element -> {
                CreatePollAnswerItemRequest itemRequest = new CreatePollAnswerItemRequest();
                itemRequest.setPollItemId(element.getId());
                return itemRequest;
            }).collect(Collectors.toList()));
            return request;
        }

        @Nested
        class WhenGlobalStock {
            @BeforeEach
            void setUp() {
                setUpForGlobalStock();

                List<PollItem> requestPollItemList = new ArrayList<>();
                for (int index = 0; index < post.getFirstPoll().getPollItemList().size() - 1; index++) {
                    requestPollItemList.add(post.getFirstPoll().getPollItemList().get(index));
                }

                request = genRequest(requestPollItemList);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(request.getPollAnswer(), callApiAndGetResponse(), 0L);
            }
        }

        @Nested
        class WhenNormalStock {
            @BeforeEach
            void setUp() {
                setUpForNormalStock();

                List<PollItem> requestPollItemList = new ArrayList<>();
                for (int index = 0; index < post.getFirstPoll().getPollItemList().size() - 1; index++) {
                    requestPollItemList.add(post.getFirstPoll().getPollItemList().get(index));
                }

                request = genRequest(requestPollItemList);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(request.getPollAnswer(), callApiAndGetResponse(), userHoldingStock.getQuantity());
            }
        }

        private ag.act.model.PollAnswerDataArrayResponse callApiAndGetResponse() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stock.getCode(), board.getGroup(), postId, pollId)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            return itUtil.getResult(response, ag.act.model.PollAnswerDataArrayResponse.class);
        }
    }

    @Nested
    class WhenUpdateMultiPollAnswer {
        private ag.act.model.PostPollAnswerRequest requestUpdate;

        @Nested
        class WhenGlobalStock {
            @BeforeEach
            void setUp() {
                setUpForGlobalStock();

                List<PollItem> insertItems = post.getFirstPoll().getPollItemList().stream().limit(2).toList();
                PollItem deleteItem = post.getFirstPoll().getPollItemList().get(post.getFirstPoll().getPollItemList().size() - 1);

                requestUpdate = genRequest(insertItems);

                itUtil.createPollAnswer(
                    user.getId(), pollId, deleteItem.getId()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(requestUpdate.getPollAnswer(), callApiAndGetResponse(), 0L);
            }
        }

        @Nested
        class WhenNormalStock {
            @BeforeEach
            void setUp() {
                setUpForNormalStock();

                List<PollItem> insertItems = post.getFirstPoll().getPollItemList().stream().limit(2).toList();
                PollItem deleteItem = post.getFirstPoll().getPollItemList().get(post.getFirstPoll().getPollItemList().size() - 1);

                requestUpdate = genRequest(insertItems);

                itUtil.createPollAnswer(
                    user.getId(), pollId, deleteItem.getId()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(requestUpdate.getPollAnswer(), callApiAndGetResponse(), userHoldingStock.getQuantity());
            }
        }

        private ag.act.model.PostPollAnswerRequest genRequest(List<PollItem> pollItems) {
            requestUpdate = new ag.act.model.PostPollAnswerRequest();

            for (PollItem pollItem : pollItems) {
                CreatePollAnswerItemRequest itemRequest = new CreatePollAnswerItemRequest();
                itemRequest.setPollItemId(pollItem.getId());

                requestUpdate.addPollAnswerItem(itemRequest);
            }
            return requestUpdate;
        }

        private ag.act.model.PollAnswerDataArrayResponse callApiAndGetResponse() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    put(TARGET_API, stock.getCode(), board.getGroup(), postId, pollId)
                        .content(objectMapperUtil.toRequestBody(requestUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            return itUtil.getResult(response, ag.act.model.PollAnswerDataArrayResponse.class);
        }


    }

    private void assertResponse(
        List<CreatePollAnswerItemRequest> requestPollItems,
        ag.act.model.PollAnswerDataArrayResponse result,
        Long stockQuantity
    ) {
        final List<ag.act.model.PollAnswerResponse> answerResponse = result.getData();

        assertThat(answerResponse.size(), is(requestPollItems.size()));

        for (int index = 0; index < answerResponse.size(); index++) {
            ag.act.model.PollAnswerResponse responsePollAnswer = answerResponse.get(index);
            CreatePollAnswerItemRequest requestPollItem = requestPollItems.get(index);

            assertThat(responsePollAnswer.getPollItemId(), is(requestPollItem.getPollItemId()));
            assertThat(responsePollAnswer.getStockQuantity(), is(stockQuantity));
        }
    }
}
