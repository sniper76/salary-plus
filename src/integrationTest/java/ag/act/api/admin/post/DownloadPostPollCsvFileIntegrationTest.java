package ag.act.api.admin.post;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someBoardCategoryExcluding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;

public class DownloadPostPollCsvFileIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/posts/{postId}/csv-download";

    private User admin;
    private User user1;
    private User user2;
    private User user3;
    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private Poll poll;
    private PollItem pollItem1;
    private PollItem pollItem2;
    private List<PollAnswer> userAnswerList1;
    private List<PollAnswer> userAnswerList2;
    private List<PollAnswer> userAnswerList3;

    @BeforeEach
    void setUp() {
        itUtil.init();

        admin = itUtil.createAdminUser();
        jwt = itUtil.createJwt(admin.getId());

        user1 = itUtil.createUser();
        user2 = itUtil.createUser();
        user3 = itUtil.createUser();

        stock = itUtil.createStock();

    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();

    }

    private String getResultContentAsString(MvcResult mvcResult) throws UnsupportedEncodingException {
        return mvcResult.getResponse().getContentAsString();
    }

    @Nested
    class WhenSuccess {
        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
            post = itUtil.createPost(board, admin.getId());

            itUtil.createPoll(post, 2, SelectionOption.MULTIPLE_ITEMS);
            poll = post.getFirstPoll();
            pollItem1 = poll.getPollItemList().get(0);
            pollItem2 = poll.getPollItemList().get(1);

            userAnswerList1 = new ArrayList<>();
            userAnswerList2 = new ArrayList<>();
            userAnswerList3 = new ArrayList<>();

            user1Vote();
            user2Vote();
            user3Vote();
        }

        private void user1Vote() {
            userAnswerList1.add(itUtil.createPollAnswer(user1.getId(), poll.getId(), pollItem1.getId()));
        }

        private void user2Vote() {
            userAnswerList2.add(itUtil.createPollAnswer(user2.getId(), poll.getId(), pollItem2.getId()));
        }

        private void user3Vote() {
            userAnswerList3.add(itUtil.createPollAnswer(user3.getId(), poll.getId(), pollItem1.getId()));
            userAnswerList3.add(itUtil.createPollAnswer(user3.getId(), poll.getId(), pollItem2.getId()));
        }

        @Test
        void shouldDownloadCsv() throws Exception {
            String actual = getResultContentAsString(callApi(status().isOk()));
            List<PollItem> pollItems = List.of(pollItem1, pollItem2);

            final String expectedResult1 = getExpectedCsvRecord(user1, pollItems, userAnswerList1);
            final String expectedResult2 = getExpectedCsvRecord(user2, pollItems, userAnswerList2);
            final String expectedResult3 = getExpectedCsvRecord(user3, pollItems, userAnswerList3);

            final String expectedResult = """
                유저아이디,유저이름,종목코드,종목명,게시물번호,보유주식수,%s-주주수,%s-주식수,%s-주주수,%s-주식수
                %s
                %s
                %s
                """.formatted(pollItem1.getText(), pollItem1.getText(), pollItem2.getText(), pollItem2.getText(),
                expectedResult1, expectedResult2, expectedResult3);

            assertThat(actual, is(expectedResult));
        }

        private String getExpectedCsvRecord(User user, List<PollItem> pollItems, List<PollAnswer> userAnswerList) {
            Long stockQuantity = userAnswerList.get(0).getStockQuantity();

            Stream<String> defaultData = Stream.of(
                user.getId().toString(),
                user.getName(),
                stock.getCode(),
                stock.getName(),
                post.getId().toString(),
                userAnswerList.get(0).getStockQuantity().toString()
            );

            Stream<String> userAnswerData = pollItems.stream()
                .flatMap(pollItem -> {
                    boolean inAnswerList = userAnswerList.stream()
                        .anyMatch(pollAnswer -> pollAnswer.getPollItemId().equals(pollItem.getId()));

                    return inAnswerList
                        ? Stream.of("1", stockQuantity.toString())
                        : Stream.of("0", "0");
                });

            return String.join(
                ",",
                Stream.concat(defaultData, userAnswerData).toList()
            );
        }
    }

    @Nested
    class WhenPostNotSurvey {
        @BeforeEach
        void setUp() {
            BoardGroup boardGroup = someEnum(BoardGroup.class);
            BoardCategory boardCategory = someBoardCategoryExcluding(boardGroup, BoardCategory.SURVEYS);
            board = itUtil.createBoard(stock, boardGroup, boardCategory);
            post = itUtil.createPost(board, admin.getId());
        }

        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult result = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(
                result,
                400,
                "해당 글은 설문이 아닙니다."
            );
        }
    }

    @Nested
    class WhenPostSurveyWithoutPoll {
        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
            post = itUtil.createPost(board, admin.getId());
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            MvcResult result = callApi(status().isNotFound());

            itUtil.assertErrorResponse(
                result,
                404,
                "해당 게시글의 설문을 찾을 수 없습니다."
            );
        }
    }
}
