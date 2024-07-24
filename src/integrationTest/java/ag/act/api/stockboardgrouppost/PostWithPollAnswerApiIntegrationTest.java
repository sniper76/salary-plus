package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.itutil.dto.PostUserProfileDto;
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.util.badge.StockCountLabelGenerator;
import ag.act.util.badge.TotalAssetLabelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostWithPollAnswerApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    @Autowired
    private StockCountLabelGenerator stockCountLabelGenerator;
    @Autowired
    private TotalAssetLabelGenerator totalAssetLabelGenerator;

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private User userTwo;
    private Poll poll;
    private Long postId;
    private Long pollId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        userTwo = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);


        itUtil.createSolidarity(stock.getCode());
        final List<UserHoldingStock> userHoldingStocks = List.of(itUtil.createUserHoldingStock(stock.getCode(), user));

        Post post = itUtil.createPost(
            board,
            user.getId(),
            new PostUserProfileDto(
                null,
                user.getProfileImageUrl(),
                user.getNickname(),
                totalAssetLabelGenerator.generate(userHoldingStocks),
                stockCountLabelGenerator.generate(userHoldingStocks.stream()
                    .filter(userHoldingStock -> userHoldingStock.getStockCode().equals(stock.getCode()))
                    .findFirst()
                    .orElse(null))
            )
        );

        post = itUtil.createPoll(itUtil.updatePost(post));

        poll = post.getFirstPoll();
        postId = post.getId();
        pollId = poll.getId();
    }

    @Nested
    class WhenMatchAnswerCount {

        private final List<PollAnswer> pollAnswerList = new ArrayList<>();
        private Long userIdOne;

        @BeforeEach
        void setUp() {
            userIdOne = user.getId();
            final Long userIdTwo = userTwo.getId();

            for (int index = 0; index < poll.getPollItemList().size(); index++) {
                PollItem pollItem = poll.getPollItemList().get(index);
                pollAnswerList.add(itUtil.createPollAnswer(index % 2 == 0 ? userIdTwo : userIdOne, pollId, pollItem.getId()));
            }
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, stock.getCode(), board.getGroup(), postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.PostDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.PostDataResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.PostDataResponse result) {
            final ag.act.model.PostResponse postResponse = result.getData();

            final PollResponse responsePoll = postResponse.getPoll();

            final List<PollItemResponse> responsePollItems = responsePoll.getPollItems();

            assertThat(responsePoll.getVoteTotalCount(), is(2));
            assertThat(responsePollItems.size(), is(2));
            assertThat(responsePoll.getAnswers().size(), is(1));

            for (int index = 0; index < responsePollItems.size(); index++) {
                PollItemResponse responsePollItem = responsePollItems.get(index);

                assertThat(Long.valueOf(responsePollItem.getVoteItemCount()), is(
                    pollAnswerList.stream().filter(pollAnswer -> Objects.equals(pollAnswer.getUserId(), userIdOne)).count()
                ));
            }
        }
    }
}
