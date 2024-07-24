package ag.act.api.stockboardgrouppost.poll;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.model.CreatePollItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreatePostWithMultiPollsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    private CreatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;

    @BeforeEach
    void setUp() {
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private CreatePostRequest genRequests() {
        CreatePostRequest request = new CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(Boolean.FALSE);
        request.setIsActive(Boolean.TRUE);

        final Instant now = Instant.now();
        final Instant endDate = now.plus(Period.ofDays(5));
        final Instant startDate = endDate.minus(Period.ofDays(1));

        request.setPolls(List.of(getCreatePollRequest(endDate, startDate), getCreatePollRequest(endDate, startDate)));

        return request;
    }

    private CreatePollRequest getCreatePollRequest(Instant endDate, Instant startDate) {
        return new CreatePollRequest()
            .title(someString(10))
            .content(someAlphanumericString(100))
            .targetStartDate(startDate.truncatedTo(ChronoUnit.SECONDS))
            .targetEndDate(endDate.truncatedTo(ChronoUnit.SECONDS))
            .selectionOption(someEnum(SelectionOption.class).name())
            .voteType(someEnum(VoteType.class).name())
            .pollItems(List.of(
                new CreatePollItemRequest().text("FIRST_" + someString(10)),
                new CreatePollItemRequest().text("SECOND_" + someString(10))
            ));
    }

    private MvcResult callApi(ResultMatcher resultMatcher, CreatePostRequest createPostRequest) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup())
                    .content(objectMapperUtil.toRequestBody(createPostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(PostDetailsDataResponse result) {
        final PostDetailsResponse postDetailsResponse = result.getData();

        assertThat(postDetailsResponse.getId(), notNullValue());
        assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
        assertThat(postDetailsResponse.getTitle(), is(request.getTitle()));
        assertThat(postDetailsResponse.getContent(), is(request.getContent()));
        assertThat(postDetailsResponse.getStatus(), is(Status.ACTIVE));
        assertThat(postDetailsResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
        assertThat(postDetailsResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
        assertThat(postDetailsResponse.getViewCount(), anyOf(nullValue(), is(0L)));
        assertThat(postDetailsResponse.getLiked(), is(Boolean.FALSE));
        assertThat(postDetailsResponse.getDeletedAt(), nullValue());

        assertUserProfile(postDetailsResponse);
    }

    private void assertUserProfile(PostDetailsResponse postDetailsResponse) {
        UserProfileResponse userProfile = postDetailsResponse.getUserProfile();

        assertThat(userProfile.getNickname(), is(user.getNickname()));
        assertThat(userProfile.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userProfile.getTotalAssetLabel(), nullValue());
        assertThat(userProfile.getIndividualStockCountLabel(), user.isAdmin() ? nullValue() : is("1주+"));

        assertThat(userProfile.getUserIp(), is("127.0"));
    }

    private void assertPoll(
        PostDetailsResponse postDetailsResponse,
        CreatePollRequest requestPoll,
        PollResponse responsePoll
    ) {
        Long postId = postDetailsResponse.getId();

        assertThat(responsePoll.getId(), notNullValue());
        assertThat(responsePoll.getTitle(), is(requestPoll.getTitle()));
        assertThat(responsePoll.getContent(), is(requestPoll.getContent()));
        assertThat(responsePoll.getPostId(), is(postId));
        assertThat(responsePoll.getVoteType(), is(requestPoll.getVoteType()));
        assertThat(responsePoll.getSelectionOption(), is(requestPoll.getSelectionOption()));
        assertThat(responsePoll.getStatus(), is(Status.ACTIVE.name()));
        assertThat(responsePoll.getTargetStartDate(), is(requestPoll.getTargetStartDate()));
        assertThat(responsePoll.getTargetEndDate(), is(requestPoll.getTargetEndDate()));
    }

    private void assertPollItems(
        CreatePollRequest requestPoll,
        PollResponse responsePoll
    ) {
        final List<CreatePollItemRequest> requestPollPollItems = requestPoll.getPollItems();
        final List<PollItemResponse> responsePollItems = responsePoll.getPollItems();
        for (int index = 0; index < responsePollItems.size(); index++) {
            PollItemResponse responsePollItem = responsePollItems.get(index);
            assertThat(responsePollItem.getId(), notNullValue());
            assertThat(responsePollItem.getPollId(), is(responsePoll.getId()));
            assertThat(responsePollItem.getText(), is(requestPollPollItems.get(index).getText()));
            assertThat(responsePollItem.getStatus(), is(Status.ACTIVE.name()));
            assertThat(responsePollItem.getCreatedAt(), notNullValue());
            assertThat(responsePollItem.getUpdatedAt(), notNullValue());
            assertThat(responsePollItem.getDeletedAt(), nullValue());
        }
    }

    @Nested
    class WhenMultiPolls {

        @BeforeEach
        void setUp() {
            request = genRequests();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isOk(), request);

            final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                PostDetailsDataResponse.class
            );

            assertResponse(result);

            final PostDetailsResponse postDetailsResponse = result.getData();
            final List<CreatePollRequest> requestPolls = request.getPolls();
            final List<PollResponse> responsePolls = postDetailsResponse.getPolls();
            final PollResponse responsePoll = postDetailsResponse.getPoll();
            final CreatePollRequest requestPoll = requestPolls.get(0);
            assertPoll(postDetailsResponse, requestPoll, responsePoll);
            assertPollItems(requestPoll, responsePoll);
            for (int index = 0; index < responsePolls.size(); index++) {
                final CreatePollRequest pollRequest = requestPolls.get(index);
                final PollResponse pollResponse = responsePolls.get(index);
                assertPoll(postDetailsResponse, pollRequest, pollResponse);
                assertPollItems(pollRequest, pollResponse);
            }
        }
    }

    @Nested
    class WhenError {

        @Nested
        class WhenEmptySingleAndMultiPolls {

            @BeforeEach
            void setUp() {
                request = genRequests();
                request.setPolls(null);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isBadRequest(), request);

                itUtil.assertErrorResponse(response, 400, "설문 정보가 없습니다.");
            }
        }
    }
}
