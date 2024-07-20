package ag.act.api.stockboardgrouppost.poll;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
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
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.model.PostDataResponse;
import ag.act.model.PostResponse;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetPostDetailsMultiPollsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";
    private final int pollCount = 2;
    private final int addItemSize = 4;
    private final int voteSingleCount = 1;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Long postId;
    private List<PollAnswer> pollAnswerList;

    @BeforeEach
    void setUp() {
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(PostDataResponse result) {
        final PostResponse postDetailsResponse = result.getData();

        assertThat(postDetailsResponse.getId(), notNullValue());
        assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
        assertThat(postDetailsResponse.getTitle(), is(post.getTitle()));
        assertThat(postDetailsResponse.getContent(), is(post.getContent()));
        assertThat(postDetailsResponse.getStatus(), is(Status.ACTIVE));
        assertThat(postDetailsResponse.getLikeCount(), notNullValue());
        assertThat(postDetailsResponse.getCommentCount(), notNullValue());
        assertThat(postDetailsResponse.getViewCount(), notNullValue());
        assertThat(postDetailsResponse.getLiked(), is(Boolean.FALSE));
        assertThat(postDetailsResponse.getDeletedAt(), nullValue());

        assertUserProfile(postDetailsResponse);
    }

    private void assertUserProfile(PostResponse postDetailsResponse) {
        UserProfileResponse userProfile = postDetailsResponse.getUserProfile();

        assertThat(userProfile.getNickname(), is(user.getNickname()));
        assertThat(userProfile.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userProfile.getTotalAssetLabel(), nullValue());
        assertThat(userProfile.getIndividualStockCountLabel(), user.isAdmin() ? nullValue() : is("1주+"));
    }

    private void assertPoll(
        PostResponse postDetailsResponse,
        Poll poll,
        PollResponse responsePoll,
        PollAnswerResult pollAnswerResult
    ) {
        Long postId = postDetailsResponse.getId();

        assertThat(responsePoll.getId(), notNullValue());
        assertThat(responsePoll.getTitle(), is(poll.getTitle()));
        assertThat(responsePoll.getContent(), is(poll.getContent()));
        assertThat(responsePoll.getPostId(), is(postId));
        assertThat(responsePoll.getVoteType(), is(poll.getVoteType().name()));
        assertThat(responsePoll.getSelectionOption(), is(poll.getSelectionOption().name()));
        assertThat(responsePoll.getStatus(), is(Status.ACTIVE.name()));
        assertThat(responsePoll.getTargetStartDate(), is(DateTimeConverter.convert(poll.getTargetStartDate())));
        assertThat(responsePoll.getTargetEndDate(), is(DateTimeConverter.convert(poll.getTargetEndDate())));

        assertThat(responsePoll.getVoteTotalStockSum(), is(pollAnswerResult.stockQuantity()));
        assertThat(responsePoll.getVoteTotalCount(), is(pollAnswerResult.voteTotalCount()));
    }

    private void assertPollItems(
        Poll poll,
        PollResponse responsePoll,
        List<PollAnswer> answerList
    ) {
        final List<PollItemResponse> responsePollItems = responsePoll.getPollItems();
        for (PollItemResponse responsePollItem : responsePollItems) {
            final PollItem pollItem = poll.getPollItemList()
                .stream()
                .filter(it -> Objects.equals(it.getId(), responsePollItem.getId()))
                .findFirst().orElse(null);

            assertThat(responsePollItem.getId(), notNullValue());
            assertThat(responsePollItem.getPollId(), is(responsePoll.getId()));
            assertThat(responsePollItem.getText(), is(pollItem.getText()));
            assertThat(responsePollItem.getStatus(), is(Status.ACTIVE.name()));
            assertThat(responsePollItem.getCreatedAt(), notNullValue());
            assertThat(responsePollItem.getUpdatedAt(), notNullValue());
            assertThat(responsePollItem.getDeletedAt(), nullValue());

            assertThat(responsePollItems.size(), is(addItemSize));

            for (final PollAnswer pollAnswer : answerList) {
                assertThat(responsePollItems.stream()
                    .filter(it -> Objects.equals(it.getId(), pollAnswer.getPollItemId()))
                    .findFirst()
                    .orElseThrow()
                    .getVoteItemCount(), is(voteSingleCount));
            }
        }
    }

    private void assertFirstPollResponse(PostResponse postDetailsResponse) {
        final PollResponse firstPollResponse = postDetailsResponse.getPoll();
        final Poll firstPoll = post.getFirstPoll();

        PollAnswerResult result = getResult(firstPoll);

        assertPoll(postDetailsResponse, firstPoll, firstPollResponse, result);
        assertPollItems(firstPoll, firstPollResponse, result.answerList());
    }

    private void assertMultiPollsResponse(PostResponse postDetailsResponse) {
        final List<PollResponse> responsePolls = postDetailsResponse.getPolls();
        for (final PollResponse pollResponse : responsePolls) {
            final Poll poll = post.getPolls().stream()
                .filter(it -> Objects.equals(it.getId(), pollResponse.getId()))
                .findFirst().orElse(null);

            PollAnswerResult result = getResult(poll);

            assertPoll(postDetailsResponse, poll, pollResponse, result);
            assertPollItems(poll, pollResponse, result.answerList());
        }
    }

    @NotNull
    private PollAnswerResult getResult(Poll firstPoll) {
        final Map<Long, List<PollAnswer>> pollAnswerMap = pollAnswerList.stream()
            .filter(it -> Objects.equals(it.getPollId(), firstPoll.getId()))
            .collect(Collectors.groupingBy(PollAnswer::getPollId));
        final List<PollAnswer> answerList = pollAnswerMap.get(firstPoll.getId());
        final long stockQuantity = answerList.stream()
            .mapToLong(PollAnswer::getStockQuantity)
            .sum();
        return new PollAnswerResult(answerList, stockQuantity, answerList.size());
    }

    private void createPollAnswers(User answerUser1, User answerUser2, Poll poll) {
        List<PollItem> pollItemList = poll.getPollItemList();

        pollAnswerList.add(itUtil.createPollAnswer(answerUser1.getId(), poll.getId(), pollItemList.get(0).getId()));
        pollAnswerList.add(itUtil.createPollAnswer(answerUser1.getId(), poll.getId(), pollItemList.get(2).getId()));
        pollAnswerList.add(itUtil.createPollAnswer(answerUser2.getId(), poll.getId(), pollItemList.get(1).getId()));
        pollAnswerList.add(itUtil.createPollAnswer(answerUser2.getId(), poll.getId(), pollItemList.get(3).getId()));
    }

    private record PollAnswerResult(List<PollAnswer> answerList, long stockQuantity, int voteTotalCount) {
    }

    @Nested
    class WhenSinglePoll {

        @Nested
        class AndSingleAnswer {

            private Post createPostWithPoll(User user, Board board) {
                Post post = itUtil.createPost(board, user.getId(), Boolean.FALSE);
                LocalDateTime startDateTime = KoreanDateTimeUtil.getYesterdayStartDateTime();
                LocalDateTime endDateTime = startDateTime.plusDays(5);

                Poll poll = itUtil.createPoll(post, addItemSize, SelectionOption.SINGLE_ITEM, startDateTime, endDateTime).getFirstPoll();
                List<PollItem> pollItemList = poll.getPollItemList();

                User answerUser1 = itUtil.createUser();
                User answerUser2 = itUtil.createUser();
                pollAnswerList.add(itUtil.createPollAnswer(answerUser1.getId(), poll.getId(), pollItemList.get(0).getId()));
                pollAnswerList.add(itUtil.createPollAnswer(answerUser2.getId(), poll.getId(), pollItemList.get(1).getId()));

                return post;
            }

            @BeforeEach
            void setUp() {
                pollAnswerList = new ArrayList<>();
                post = createPostWithPoll(user, board);
                postId = post.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final PostDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDataResponse.class
                );

                assertResponse(result);
                assertFirstPollResponse(result.getData());
                assertMultiPollsResponse(result.getData());
            }
        }

        @Nested
        class AndMultiAnswers {

            private Post createPostWithPoll(User user, Board board) {
                Post post = itUtil.createPost(board, user.getId(), Boolean.FALSE);
                LocalDateTime startDateTime = KoreanDateTimeUtil.getYesterdayStartDateTime();
                LocalDateTime endDateTime = startDateTime.plusDays(5);

                User answerUser1 = itUtil.createUser();
                User answerUser2 = itUtil.createUser();

                Poll poll = itUtil.createPoll(post, addItemSize, SelectionOption.MULTIPLE_ITEMS, startDateTime, endDateTime).getFirstPoll();
                createPollAnswers(answerUser1, answerUser2, poll);

                return post;
            }

            @BeforeEach
            void setUp() {
                pollAnswerList = new ArrayList<>();
                post = createPostWithPoll(user, board);
                postId = post.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final PostDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDataResponse.class
                );

                assertResponse(result);
                assertFirstPollResponse(result.getData());
                assertMultiPollsResponse(result.getData());
            }
        }
    }

    @Nested
    class WhenMultiPolls {

        @Nested
        class AndSingleAnswer {

            private Post createPostWithPolls(User user, Board board) {
                Post post = itUtil.createPost(board, user.getId(), Boolean.FALSE);
                LocalDateTime startDateTime = KoreanDateTimeUtil.getYesterdayStartDateTime();
                LocalDateTime endDateTime = startDateTime.plusDays(5);

                User answerUser1 = itUtil.createUser();
                User answerUser2 = itUtil.createUser();

                itUtil.createPolls(post, pollCount, addItemSize, SelectionOption.SINGLE_ITEM, startDateTime, endDateTime);
                final List<Poll> polls = post.getPolls();
                for (final Poll poll : polls) {
                    List<PollItem> pollItemList = poll.getPollItemList();

                    pollAnswerList.add(itUtil.createPollAnswer(answerUser1.getId(), poll.getId(), pollItemList.get(0).getId()));
                    pollAnswerList.add(itUtil.createPollAnswer(answerUser2.getId(), poll.getId(), pollItemList.get(1).getId()));
                }

                return post;
            }

            @BeforeEach
            void setUp() {
                pollAnswerList = new ArrayList<>();
                post = createPostWithPolls(user, board);
                postId = post.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final PostDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDataResponse.class
                );

                assertResponse(result);
                assertFirstPollResponse(result.getData());
                assertMultiPollsResponse(result.getData());
            }
        }

        @Nested
        class AndMultiAnswers {
            @BeforeEach
            void setUp() {
                pollAnswerList = new ArrayList<>();
                post = createPostWithPolls(user, board);
                postId = post.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final PostDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDataResponse.class
                );

                assertResponse(result);
                assertFirstPollResponse(result.getData());
                assertMultiPollsResponse(result.getData());
            }
        }
    }

    @Nested
    class WhenGuestGetPostDetail {
        @BeforeEach
        void setUp() {
            pollAnswerList = new ArrayList<>();
            board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
            post = createPostWithPolls(user, board);
            postId = post.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk());

            final PostDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                PostDataResponse.class
            );

            assertResponse(result);
            assertFirstPollResponse(result.getData());
            assertMultiPollsResponse(result.getData());
        }

        private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
            return mockMvc
                .perform(
                    get(TARGET_API, stock.getCode(), board.getGroup(), postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_APP_VERSION, X_APP_VERSION_WEB)
                )
                .andExpect(resultMatcher)
                .andReturn();
        }
    }

    private Post createPostWithPolls(User user, Board board) {
        Post post = itUtil.createPost(board, user.getId(), Boolean.FALSE);
        LocalDateTime startDateTime = KoreanDateTimeUtil.getYesterdayStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusDays(5);

        User answerUser1 = itUtil.createUser();
        User answerUser2 = itUtil.createUser();

        itUtil.createPolls(post, pollCount, addItemSize, SelectionOption.MULTIPLE_ITEMS, startDateTime, endDateTime);
        final List<Poll> polls = post.getPolls();
        for (final Poll poll : polls) {
            createPollAnswers(answerUser1, answerUser2, poll);
        }

        return post;
    }

}
