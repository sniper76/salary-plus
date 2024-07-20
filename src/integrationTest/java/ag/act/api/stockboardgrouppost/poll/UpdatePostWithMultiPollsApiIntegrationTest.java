package ag.act.api.stockboardgrouppost.poll;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.SelectionOption;
import ag.act.model.PollResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.UpdatePollRequest;
import ag.act.model.UpdatePostRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdatePostWithMultiPollsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";
    private final int addPollCount = 2;
    private final int addPollItemCount = 2;
    private ag.act.model.UpdatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
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
        post = createDefaultPost();

        final LocalDateTime startDateTime = someLocalDateTimeInTheFuture();
        final LocalDateTime endDateTime = startDateTime.plusDays(someIntegerBetween(1, 10));
        itUtil.createPolls(post, addPollCount, addPollItemCount, SelectionOption.SINGLE_ITEM, startDateTime, endDateTime);
        postId = post.getId();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), loggedInUser);
    }

    private Post createDefaultPost() {
        return itUtil.createPost(board, loggedInUser.getId(), Boolean.FALSE);
    }

    private UpdatePostRequest genRequest(List<UpdatePollRequest> polls) {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setPolls(polls);

        return request;
    }

    private MvcResult callApi(ResultMatcher resultMatcher, UpdatePostRequest updatePostRequest) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, stock.getCode(), board.getGroup(), postId)
                    .content(objectMapperUtil.toRequestBody(updatePostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(PostDetailsDataResponse result) {
        final PostDetailsResponse createUpdateResponse = result.getData();

        assertThat(createUpdateResponse.getTitle(), is(request.getTitle()));
        assertThat(createUpdateResponse.getContent(), is(request.getContent()));

        final List<PollResponse> pollResponseList = createUpdateResponse.getPolls();
        for (PollResponse pollResponse : pollResponseList) {
            final Optional<UpdatePollRequest> optionalUpdatePollRequest = request.getPolls()
                .stream()
                .filter(it -> Objects.equals(it.getId(), pollResponse.getId()))
                .findFirst();
            if (optionalUpdatePollRequest.isPresent()) {
                final UpdatePollRequest pollRequest = optionalUpdatePollRequest.get();

                assertTime(pollResponse.getTargetEndDate(), pollRequest.getTargetEndDate());
                if (pollRequest.getTargetStartDate() != null) {
                    assertTime(pollResponse.getTargetStartDate(), pollRequest.getTargetStartDate());
                }
            }
        }
    }

    @Nested
    class WhenUpdatePostAndPolls {

        @Nested
        class WhenChangeOnePoll {

            @BeforeEach
            void setUp() {
                final List<Poll> polls = post.getPolls();
                final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size() - 1);
                for (Poll poll : polls) {
                    final Instant instant = DateTimeConverter.convert(poll.getTargetEndDate());
                    updatePollRequestList.add(new UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(instant.plus(1, ChronoUnit.DAYS))
                    );
                }
                request = genRequest(updatePollRequestList);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk(), request);
                final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

                assertResponse(result);
            }
        }


        @Nested
        class WhenChangeAllPolls {

            @BeforeEach
            void setUp() {
                final List<Poll> polls = post.getPolls();
                final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size());
                for (Poll poll : polls) {
                    final Instant instant = DateTimeConverter.convert(poll.getTargetEndDate());
                    updatePollRequestList.add(new UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(instant.plus(1, ChronoUnit.DAYS))
                    );
                }
                request = genRequest(updatePollRequestList);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk(), request);
                final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

                assertResponse(result);
            }
        }
    }

    @Nested
    class FailToUpdatePost {

        @Nested
        class WhenTargetEndDateIsToday {

            @BeforeEach
            void setUp() {
                final Instant endDate = Instant.now().minus(5, ChronoUnit.MINUTES);
                final List<Poll> polls = post.getPolls();
                final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size());
                for (Poll poll : polls) {
                    final Instant instant = DateTimeConverter.convert(poll.getTargetEndDate());
                    updatePollRequestList.add(new UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(endDate)
                    );
                }
                request = genRequest(updatePollRequestList);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(response, 400, "종료일은 현재일 이후로 수정 가능합니다.");
            }
        }

        @Nested
        class WhenTargetEndDateCheckStartDate {

            @BeforeEach
            void setUp() {
                final List<Poll> polls = post.getPolls();
                final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size());
                for (Poll poll : polls) {
                    final Instant startDate = DateTimeConverter.convert(poll.getTargetStartDate());
                    final Instant endDate = startDate.minus(5, ChronoUnit.MINUTES);
                    updatePollRequestList.add(new UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(endDate)
                    );
                }
                request = genRequest(updatePollRequestList);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(response, 400, "종료일을 시작일 이전으로 수정 불가능합니다.");
            }
        }

        @Nested
        class WhenTargetEndDateIsAfterStartDate {

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                post = createDefaultPost();
                postId = post.getId();
                itUtil.createPolls(
                    post,
                    addPollCount,
                    addPollItemCount,
                    SelectionOption.SINGLE_ITEM,
                    now,
                    now.plusDays(2)
                );

                final List<Poll> polls = post.getPolls();
                final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size());
                final Instant currentDate = Instant.now();
                for (Poll poll : polls) {
                    final Instant startDate = currentDate.plus(4, ChronoUnit.DAYS);
                    final Instant endDate = currentDate.plus(3, ChronoUnit.DAYS);
                    updatePollRequestList.add(new UpdatePollRequest()
                        .id(poll.getId())
                        .targetEndDate(endDate)
                        .targetStartDate(startDate)
                    );
                }
                request = genRequest(updatePollRequestList);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest(), request);

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
            postId = post.getId();
            itUtil.createPolls(
                post,
                addPollCount,
                addPollItemCount,
                SelectionOption.SINGLE_ITEM,
                now.plusDays(10),
                now.plusDays(20)
            );

            final List<Poll> polls = post.getPolls();
            final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size());
            final Instant currentDate = Instant.now();
            for (Poll poll : polls) {
                final Instant startDate = currentDate.plus(1, ChronoUnit.DAYS);
                final Instant endDate = currentDate.plus(3, ChronoUnit.DAYS);
                updatePollRequestList.add(new UpdatePollRequest()
                    .id(poll.getId())
                    .targetEndDate(endDate)
                    .targetStartDate(startDate)
                );
            }
            request = genRequest(updatePollRequestList);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

            assertResponse(result);
        }
    }

    @Nested
    class WhenChangingOnlyTargetEndDateOfPostWithPolls {

        @BeforeEach
        void setUp() {
            final LocalDateTime now = LocalDateTime.now();
            post = createDefaultPost();
            postId = post.getId();
            itUtil.createPolls(
                post,
                addPollCount,
                addPollItemCount,
                SelectionOption.SINGLE_ITEM,
                now.minusDays(1),
                now.plusDays(20)
            );

            final List<Poll> polls = post.getPolls();
            final List<UpdatePollRequest> updatePollRequestList = new ArrayList<>(polls.size());
            final Instant currentDate = Instant.now();
            for (Poll poll : polls) {
                final Instant endDate = currentDate.plus(3, ChronoUnit.DAYS);
                updatePollRequestList.add(new UpdatePollRequest()
                    .id(poll.getId())
                    .targetEndDate(endDate)
                );
            }
            request = genRequest(updatePollRequestList);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

            assertResponse(result);
        }
    }
}
