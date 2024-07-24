package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.LatestPostTimestamp;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.RoleType;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.model.CreatePollItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.SimpleImageResponse;
import ag.act.model.Status;
import ag.act.util.ImageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someBoardCategoryExceptDebate;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;
import static shiver.me.timbers.data.random.RandomTimes.someTimeToday;

class CreatePostWithPollApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    private ag.act.model.CreatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private List<Long> imageIds;
    private List<SimpleImageResponse> simpleImageResponses;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        final FileContent image1 = itUtil.createImage();
        final FileContent image2 = itUtil.createImage();
        imageIds = Stream.of(image1, image2).map(FileContent::getId).toList();
        simpleImageResponses = Stream.of(image1, image2)
            .map(fileContent -> new SimpleImageResponse()
                .imageId(fileContent.getId())
                .imageUrl(ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename()))
            )
            .toList();
    }

    private ag.act.model.CreatePostRequest genRequest() {
        ag.act.model.CreatePostRequest request = new ag.act.model.CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(someBoolean());
        request.setIsActive(someThing(Boolean.TRUE, Boolean.FALSE, null, null));
        request.setImageIds(imageIds);

        final Instant endDate = Instant.now().plus(Period.ofDays(5));
        final Instant startDate = endDate.minus(Period.ofDays(1));

        final CreatePollRequest poll = new CreatePollRequest()
            .title(someString(10))
            .targetStartDate(startDate.truncatedTo(ChronoUnit.SECONDS))
            .targetEndDate(endDate.truncatedTo(ChronoUnit.SECONDS))
            .selectionOption(someEnum(SelectionOption.class).name())
            .voteType(someEnum(VoteType.class).name())
            .pollItems(List.of(
                new ag.act.model.CreatePollItemRequest().text("FIRST_" + someString(10)),
                new ag.act.model.CreatePollItemRequest().text("SECOND_" + someString(10))
            ));

        request.setPolls(List.of(poll));

        return request;
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenUserIsAdmin {

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
            user = itUtil.createUserRole(user, RoleType.ADMIN);
        }

        @Nested
        class AndCreatePostAndPoll {

            @BeforeEach
            void setUp() {
                itUtil.deleteAllLatestPostTimestamps();
                request = genRequest();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final ag.act.model.PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.PostDetailsDataResponse.class
                );

                assertResponse(result);
                assertLatestPostTimestamp();
            }

        }

        @Nested
        class AndTargetEndDateIsBeforeStartDate {

            @BeforeEach
            void setUp() {
                final Instant instant = someTimeToday().toInstant();

                request = genRequest();
                request.getPolls().get(0).setTargetStartDate(instant.plus(1, ChronoUnit.DAYS));
                request.getPolls().get(0).setTargetEndDate(instant);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "설문 시작일이 종료일보다 크거나 같습니다.");
            }
        }
    }

    @Nested
    class WhenUserIsNotAdmin {

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock);
        }

        @Nested
        class AndTryToCreatePostInNotDebateBoardCategory {

            @BeforeEach
            void setUp() {
                board.setCategory(someBoardCategoryExceptDebate());
                board = itUtil.updateBoard(board);
                request = genRequest();
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400,
                    "%s 게시판에 글쓰기 권한이 없습니다.".formatted(board.getCategory().getDisplayName()));
            }
        }

        @Nested
        class AndTryToCreatePostWithPollInDebateBoardCategory {

            @BeforeEach
            void setUp() {
                itUtil.deleteAllLatestPostTimestamps();

                board.setCategory(BoardCategory.DEBATE);
                board = itUtil.updateBoard(board);
                request = genRequest();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final ag.act.model.PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.PostDetailsDataResponse.class
                );

                assertResponse(result);
                assertLatestPostTimestamp();
            }
        }
    }

    @Nested
    class WhenUserIsSolidarityLeader {

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            itUtil.createUserHoldingStock(stock.getCode(), user);
            final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            itUtil.createSolidarityLeader(solidarity, user.getId());
        }

        @Nested
        class CreatePostInWrongCategory {
            @ParameterizedTest(name = "{index} => inputDate=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldReturnBadRequest(BoardCategory boardCategory) throws Exception {

                // Given
                board = itUtil.createBoard(stock, boardCategory.getBoardGroup(), boardCategory);
                board = itUtil.updateBoard(board);
                request = genRequest();

                // When
                MvcResult response = callApi(status().isBadRequest());

                // Then
                itUtil.assertErrorResponse(response, 400,
                    "%s 게시판에 글쓰기 권한이 없습니다.".formatted(board.getCategory().getDisplayName()));
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of(BoardCategory.DIGITAL_DELEGATION),
                    Arguments.of(BoardCategory.CO_HOLDING_ARRANGEMENTS),
                    Arguments.of(BoardCategory.ETC),
                    Arguments.of(BoardCategory.STOCKHOLDER_ACTION),
                    Arguments.of(BoardCategory.STOCK_ANALYSIS_DATA)
                );
            }
        }
    }

    private void assertResponse(ag.act.model.PostDetailsDataResponse result) {
        final PostDetailsResponse createUpdateResponse = result.getData();

        assertThat(createUpdateResponse.getPostImageList(), is(simpleImageResponses));
        assertThat(createUpdateResponse.getId(), is(notNullValue()));
        assertThat(createUpdateResponse.getBoardId(), is(board.getId()));
        assertThat(createUpdateResponse.getTitle(), is(request.getTitle()));
        assertThat(createUpdateResponse.getContent(), is(request.getContent()));
        if (user.isAdmin()) {
            assertThat(
                createUpdateResponse.getStatus(),
                is(
                    (request.getIsActive() == null || request.getIsActive())
                        ? Status.ACTIVE
                        : Status.INACTIVE_BY_ADMIN
                )
            );
        } else {
            assertThat(createUpdateResponse.getStatus(), is(Status.ACTIVE));
        }
        assertThat(createUpdateResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
        assertThat(createUpdateResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
        assertThat(createUpdateResponse.getViewCount(), anyOf(nullValue(), is(0L)));
        assertThat(createUpdateResponse.getLiked(), is(false));
        assertThat(createUpdateResponse.getDeletedAt(), is(nullValue()));

        assertUserProfile(createUpdateResponse);

        final CreatePollRequest requestPoll = request.getPolls().get(0);
        final PollResponse responsePoll = createUpdateResponse.getPoll();

        assertPoll(createUpdateResponse, requestPoll, responsePoll);
        assertPollItems(createUpdateResponse, requestPoll, responsePoll);
    }

    private void assertUserProfile(PostDetailsResponse createUpdateResponse) {
        ag.act.model.UserProfileResponse userProfile = createUpdateResponse.getUserProfile();

        if (request.getIsAnonymous()) {
            assertThat(userProfile.getNickname(), is("익명"));
        } else {
            assertThat(userProfile.getNickname(), is(user.getNickname()));
            assertThat(userProfile.getProfileImageUrl(), is(user.getProfileImageUrl()));
            assertThat(userProfile.getTotalAssetLabel(), nullValue());
            assertThat(
                userProfile.getIndividualStockCountLabel(),
                user.isAdmin() ? nullValue() : is("1주+")
            );
        }

        assertThat(userProfile.getUserIp(), is("127.0"));
    }

    private void assertPoll(
        PostDetailsResponse createUpdateResponse,
        CreatePollRequest requestPoll,
        PollResponse responsePoll
    ) {
        Long postId = createUpdateResponse.getId();


        assertThat(responsePoll.getId(), is(notNullValue()));
        assertThat(responsePoll.getTitle(), is(requestPoll.getTitle()));
        assertThat(responsePoll.getPostId(), is(postId));
        assertThat(responsePoll.getVoteType(), is(requestPoll.getVoteType()));
        assertThat(responsePoll.getSelectionOption(), is(requestPoll.getSelectionOption()));
        assertThat(responsePoll.getStatus(), is(Status.ACTIVE.name()));
        assertThat(responsePoll.getTargetStartDate(), is(requestPoll.getTargetStartDate()));
        assertThat(responsePoll.getTargetEndDate(), is(requestPoll.getTargetEndDate()));
    }

    private void assertPollItems(
        PostDetailsResponse createUpdateResponse,
        CreatePollRequest requestPoll,
        PollResponse responsePoll
    ) {
        final List<CreatePollItemRequest> requestPollPollItems = requestPoll.getPollItems();
        final List<PollItemResponse> responsePollItems = responsePoll.getPollItems();
        for (int index = 0; index < responsePollItems.size(); index++) {
            PollItemResponse responsePollItem = responsePollItems.get(index);
            assertThat(responsePollItem.getId(), is(notNullValue()));
            assertThat(responsePollItem.getPollId(), is(responsePoll.getId()));
            assertThat(responsePollItem.getText(), is(requestPollPollItems.get(index).getText()));
            assertThat(responsePollItem.getStatus(), is(Status.ACTIVE.name()));
            assertThat(responsePollItem.getCreatedAt(), is(notNullValue()));
            assertThat(responsePollItem.getUpdatedAt(), is(notNullValue()));
            assertThat(responsePollItem.getDeletedAt(), is(nullValue()));
        }

        final Post post = itUtil.findPostNoneNull(createUpdateResponse.getId());
        final Long expectedProxyId = post.getFirstPoll().getId();
        assertThat(responsePoll.getId(), is(expectedProxyId));
    }

    private void assertLatestPostTimestamp() {
        List<LatestPostTimestamp> latestPostTimestamps = itUtil.findAllLatestPostTimestamps();

        assertThat(latestPostTimestamps.size(), is(1));

        LatestPostTimestamp latestPostTimestamp = latestPostTimestamps.get(0);
        assertThat(latestPostTimestamp.getStock().getCode(), is(stock.getCode()));
        assertThat(latestPostTimestamp.getBoardGroup(), is(board.getGroup()));
        assertThat(latestPostTimestamp.getBoardCategory(), is(board.getCategory()));
        assertThat(latestPostTimestamp.getTimestamp(), notNullValue());
    }
}
