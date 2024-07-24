package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.SelectionOption;
import ag.act.model.PollResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.UpdatePollRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdatePostWithPollApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private ag.act.model.UpdatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private Poll poll;
    private Post post;
    private Long postId;
    private User loggedInUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        loggedInUser = itUtil.createUser();
        jwt = itUtil.createJwt(loggedInUser.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPoll(createDefaultPost());
        poll = post.getFirstPoll();
        postId = post.getId();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), loggedInUser);
    }

    private Post createDefaultPost() {
        return itUtil.createPost(board, loggedInUser.getId(), Boolean.FALSE);
    }

    private ag.act.model.UpdatePostRequest genRequest() {
        ag.act.model.UpdatePostRequest request = new ag.act.model.UpdatePostRequest();
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(post.getIsAnonymous());
        request.setIsActive(someBoolean());

        return request;
    }

    @Nested
    class WhenUpdatePostAndPoll {

        @BeforeEach
        void setUp() {
            request = genRequest();

            final Instant instant = DateTimeConverter.convert(poll.getTargetEndDate());

            request.setPolls(List.of(
                new ag.act.model.UpdatePollRequest()
                    .id(poll.getId())
                    .targetEndDate(instant.plus(1, ChronoUnit.DAYS))
            ));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());
            final ag.act.model.PostDetailsDataResponse result = itUtil.getResult(response, ag.act.model.PostDetailsDataResponse.class);

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.PostDetailsDataResponse result) {
            final ag.act.model.PostDetailsResponse createUpdateResponse = result.getData();

            final List<UpdatePollRequest> requestPolls = request.getPolls();
            final UpdatePollRequest requestPoll = requestPolls.get(0);
            final PollResponse responsePoll = createUpdateResponse.getPoll();

            assertThat(responsePoll.getTargetEndDate(), is(requestPoll.getTargetEndDate()));
        }

    }

    @Nested
    class FailToUpdatePost {

        @Nested
        class WhenTargetEndDateIsToday {

            @BeforeEach
            void setUp() {
                final Instant endDate = Instant.now().minus(5, ChronoUnit.MINUTES);

                request = genRequest();

                request.setPolls(List.of(
                    new ag.act.model.UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(endDate)
                ));
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "종료일은 현재일 이후로 수정 가능합니다.");
            }
        }

        @Nested
        class WhenTargetEndDateCheckStartDate {

            @BeforeEach
            void setUp() {
                request = genRequest();

                final Instant startDate = DateTimeConverter.convert(poll.getTargetStartDate());
                final Instant endDate = startDate.minus(5, ChronoUnit.MINUTES);

                request.setPolls(List.of(
                    new ag.act.model.UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(endDate)
                ));
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "종료일을 시작일 이전으로 수정 불가능합니다.");
            }
        }

        @Nested
        class WhenTargetEndDateIsAfterStartDate {

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                post = createDefaultPost();
                post = itUtil.createPoll(
                    post,
                    2,
                    SelectionOption.SINGLE_ITEM,
                    now,
                    now.plusDays(2)
                );
                poll = post.getFirstPoll();
                postId = post.getId();

                request = genRequest();

                final Instant currentDate = Instant.now();
                final Instant startDate = currentDate.plus(4, ChronoUnit.DAYS);
                final Instant endDate = currentDate.plus(3, ChronoUnit.DAYS);

                request.setPolls(List.of(
                    new ag.act.model.UpdatePollRequest()
                        .id(poll.getId())
                        .targetStartDate(startDate)
                        .targetEndDate(endDate)
                ));
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "시작일은 종료일 이후로 수정할 수 없습니다.");
            }
        }
    }

    @Nested
    class WhenChangingTargetStartDateAndEndDateOfPost {

        @BeforeEach
        void setUp() {
            final LocalDateTime now = LocalDateTime.now();
            post = createDefaultPost();
            post = itUtil.createPoll(
                post,
                2,
                SelectionOption.SINGLE_ITEM,
                now.plusDays(10),
                now.plusDays(20)
            );
            poll = post.getFirstPoll();
            postId = post.getId();

            request = genRequest();

            final Instant currentDate = Instant.now();
            final Instant startDate = currentDate.plus(1, ChronoUnit.DAYS);
            final Instant endDate = currentDate.plus(3, ChronoUnit.DAYS);

            request.setPolls(List.of(
                new ag.act.model.UpdatePollRequest()
                    .id(poll.getId())
                    .targetStartDate(startDate)
                    .targetEndDate(endDate)
            ));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());
            final ag.act.model.PostDetailsDataResponse result = itUtil.getResult(response, ag.act.model.PostDetailsDataResponse.class);
            final PostDetailsResponse postDetailsResponse = result.getData();
            final List<PollResponse> pollResponses = postDetailsResponse.getPolls();
            for (int index = 0; index < pollResponses.size(); index++) {
                final PollResponse pollResponse = pollResponses.get(index);
                final List<UpdatePollRequest> polls = request.getPolls();
                assertTime(pollResponse.getTargetStartDate(), polls.get(index).getTargetStartDate());
                assertTime(pollResponse.getTargetEndDate(), polls.get(index).getTargetEndDate());
            }
        }
    }

    @Nested
    class WhenChangingOnlyTargetEndDateOfPostWithPoll {

        @BeforeEach
        void setUp() {
            final LocalDateTime now = LocalDateTime.now();
            post = createDefaultPost();
            post = itUtil.createPoll(
                post,
                2,
                SelectionOption.SINGLE_ITEM,
                now.minusDays(1),
                now.plusDays(20)
            );
            poll = post.getFirstPoll();
            postId = post.getId();

            request = genRequest();

            final Instant currentDate = Instant.now();
            final Instant endDate = currentDate.plus(3, ChronoUnit.DAYS);

            request.setPolls(List.of(
                new ag.act.model.UpdatePollRequest()
                    .id(poll.getId())
                    .targetEndDate(endDate)
            ));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());
            final ag.act.model.PostDetailsDataResponse result = itUtil.getResult(response, ag.act.model.PostDetailsDataResponse.class);
            final PostDetailsResponse postDetailsResponse = result.getData();
            final List<PollResponse> pollResponses = postDetailsResponse.getPolls();
            final List<UpdatePollRequest> polls = request.getPolls();
            for (int index = 0; index < pollResponses.size(); index++) {
                final PollResponse pollResponse = pollResponses.get(index);
                assertTime(pollResponse.getTargetEndDate(), polls.get(index).getTargetEndDate());
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stock.getCode(), board.getGroup(), postId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
