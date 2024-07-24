package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
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

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckPollAnswerCurrentUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/polls/{pollId}/answers";
    private static final String DETAIL_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private PostPollAnswerRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Poll poll;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User postWriteUser = itUtil.createUserBeforePinRegistered();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
        post = itUtil.createPost(board, postWriteUser.getId());
        post = itUtil.createPoll(post);
        poll = post.getFirstPoll();

        LocalDateTime localDateTime = LocalDateTime.now();
        poll.setTargetStartDate(localDateTime);
        poll.setTargetEndDate(localDateTime.plusDays(5));
        poll.setSelectionOption(SelectionOption.SINGLE_ITEM);
        itUtil.updatePost(post);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private PostPollAnswerRequest genRequest() {

        CreatePollAnswerItemRequest answerItemRequest = new CreatePollAnswerItemRequest();
        answerItemRequest.setPollItemId(poll.getPollItemList().get(0).getId());

        List<CreatePollAnswerItemRequest> answerItemRequestList = new ArrayList<>();
        answerItemRequestList.add(answerItemRequest);

        PostPollAnswerRequest request = new PostPollAnswerRequest();
        request.pollAnswer(answerItemRequestList);
        return request;
    }

    private MvcResult callGetDetailApi() throws Exception {
        return mockMvc
            .perform(
                get(DETAIL_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult callPostAnswerApi() throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), poll.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    @Nested
    class WhenCreatePostAnswerAfterPostDetailCheckAnswers {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult postAnswerResponse = callPostAnswerApi();

            final ag.act.model.PollAnswerDataArrayResponse result = objectMapperUtil.toResponse(
                postAnswerResponse.getResponse().getContentAsString(),
                ag.act.model.PollAnswerDataArrayResponse.class
            );

            assertThat(result.getData().size(), is(request.getPollAnswer().size()));

            MvcResult getDetailResponse = callGetDetailApi();

            final ag.act.model.PostDataResponse resultDetail = objectMapperUtil.toResponse(
                getDetailResponse.getResponse().getContentAsString(),
                ag.act.model.PostDataResponse.class
            );

            for (ag.act.model.PollAnswerResponse pollAnswer : resultDetail.getData().getPoll().getAnswers()) {
                assertThat(pollAnswer.getUserId(), is(user.getId()));
            }
        }
    }
}
