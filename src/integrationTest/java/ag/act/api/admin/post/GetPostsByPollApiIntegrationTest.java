package ag.act.api.admin.post;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.SelectionOption;
import ag.act.itutil.holder.PostsByBoardGroupTestHolder;
import ag.act.model.DigitalDocumentDownloadResponse;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.Paging;
import ag.act.model.PollResponse;
import ag.act.model.PostResponse;
import ag.act.model.UserDigitalDocumentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetPostsByPollApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/board-groups/{boardGroup}/posts";
    private static final PostsByBoardGroupTestHolder postsByBoardGroupTestHolder = new PostsByBoardGroupTestHolder();
    private static final int addPollItemSize = 4;
    private String jwt;
    private Map<String, Object> params;
    private Stock stock;
    private Board board;
    private Integer pageNumber;
    private Post post1;
    private Post post2;
    private UserHoldingStock userHoldingStock1;
    private UserHoldingStock userHoldingStock2;
    private UserHoldingStock userHoldingStock3;
    private UserHoldingStock userHoldingStock4;
    private PollAnswer pollAnswer1ByUser3;
    private PollAnswer pollAnswer2ByUser3;
    private PollAnswer pollAnswer1ByUser4;
    private PollAnswer pollAnswer2ByUser4;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private String zipFileKey;
    private String zipFilePath;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postsByBoardGroupTestHolder.initialize(itUtil.findAllPosts());
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        user1 = itUtil.createUser();
        user2 = itUtil.createUser();
        user3 = itUtil.createUser();
        user4 = itUtil.createUser();
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
        itUtil.createSolidarity(stock.getCode());
        userHoldingStock1 = itUtil.createUserHoldingStock(stock.getCode(), user1);
        userHoldingStock2 = itUtil.createUserHoldingStock(stock.getCode(), user2);
        userHoldingStock3 = itUtil.createUserHoldingStock(stock.getCode(), user3);
        userHoldingStock4 = itUtil.createUserHoldingStock(stock.getCode(), user4);

        zipFilePath = someAlphanumericString(10);
        zipFileKey = someAlphanumericString(10);
    }

    private GetBoardGroupPostResponse callApiAndGetResult(String boardGroup) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, boardGroup)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetBoardGroupPostResponse.class
        );
    }

    private void assertPostWithPollResponse(Post post, PostResponse postResponse) {
        assertPostResponse(post, postResponse);

        final List<PollResponse> pollResponseList = postResponse.getPolls();
        final PollResponse pollResponse = pollResponseList.get(0);
        assertThat(pollResponse.getVoteTotalCount(), is(2));
        assertThat(pollResponse.getVoteTotalStockSum(), is(
            pollAnswer1ByUser3.getStockQuantity() + pollAnswer2ByUser4.getStockQuantity()
        ));
    }

    private void assertPostWithDigitalDocumentResponse(Post post, PostResponse postResponse) {
        assertPostResponse(post, postResponse);

        assertThat(postResponse.getPoll(), is(nullValue()));

        UserDigitalDocumentResponse userDigitalDocument = postResponse.getDigitalDocument();
        assertThat(userDigitalDocument, is(notNullValue()));

        DigitalDocumentDownloadResponse digitalDocumentDownloadResponse = userDigitalDocument.getDigitalDocumentDownload();
        assertThat(digitalDocumentDownloadResponse, is(notNullValue()));
        assertThat(digitalDocumentDownloadResponse.getZipFileKey(), is(notNullValue()));
    }

    private void assertPostResponse(Post post, PostResponse postResponse) {
        assertThat(postResponse.getId(), is(post.getId()));
        assertThat(postResponse.getStock().getCode(), is(stock.getCode()));
        assertThat(postResponse.getBoardId(), is(board.getId()));
        assertThat(postResponse.getStatus(), is(post.getStatus()));
        assertThat(postResponse.getIsAuthorAdmin(), is(false));
        assertThat(postResponse.getReported(), is(false));
        itUtil.assertPostTitleAndContent(post, postResponse);
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    private Post createPostWithPolls(User user, String title, Board board, int addPollSize, SelectionOption selectionOption) {
        Post post = itUtil.createPost(board, user.getId());
        post.setTitle(someString(10) + title + someString(10));

        LocalDateTime now = LocalDateTime.now();
        for (int index = 0; index < addPollSize; index++) {
            itUtil.createPoll(post, addPollItemSize, selectionOption, now.minusDays(1), now.plusDays(5));
        }

        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    private Post createPostWithDigitalDocument(User user, String title, String zipFileKey, String zipFilePath, Board board, Stock stock) {
        Post post = itUtil.createPost(board, user.getId());
        post.setTitle(someString(10) + title + someString(10));

        User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT);
        itUtil.createDigitalDocumentUser(digitalDocument, user, stock, someAlphanumericString(10), DigitalDocumentAnswerStatus.COMPLETE);
        DigitalDocumentDownload digitalDocumentDownload = itUtil.createDigitalDocumentDownload(digitalDocument.getId(), user.getId(), true);
        digitalDocumentDownload.setZipFilePath(zipFilePath);
        digitalDocumentDownload.setZipFileKey(zipFileKey);
        itUtil.updateDigitalDocumentDownload(digitalDocumentDownload);

        itUtil.createMyDataSummary(user, stock.getCode(), digitalDocument.getStockReferenceDate());

        return postsByBoardGroupTestHolder.addOrSet(board.getGroup(), itUtil.updatePost(post));
    }

    @Nested
    class GetPosts {
        @BeforeEach
        void setUp() {
            pageNumber = PAGE_1;
            params = Map.of(
                "page", pageNumber.toString(),
                "size", SIZE.toString()
            );
        }

        private void assertResponse(GetBoardGroupPostResponse result, long totalElements) {
            final ag.act.model.Paging paging = result.getPaging();
            final List<ag.act.model.PostResponse> postResponses = result.getData();

            assertPaging(paging, totalElements);
            assertThat(postResponses.size(), is(SIZE));
            assertPostWithDigitalDocumentResponse(post2, postResponses.get(0));
            assertPostWithPollResponse(post1, postResponses.get(1));
        }

        private PollAnswer createAndGetPollAnswer(Long userId, Poll poll, PollItem pollItem, Long stockQuantity) {
            return itUtil.createPollAnswer(
                userId, poll.getId(), pollItem.getId(), stockQuantity
            );
        }

        @Nested
        class AndSurveySingleAnswer {
            @BeforeEach
            void setUp() {
                post1 = createPostWithPolls(user1, someString(10), board, 1, SelectionOption.SINGLE_ITEM);
                post2 = createPostWithDigitalDocument(user2, someString(10), zipFileKey, zipFilePath, board, stock);

                final Poll poll = post1.getFirstPoll();
                final List<PollItem> pollItemList = poll.getPollItemList();

                final PollItem pollItem1 = pollItemList.get(0);
                final PollItem pollItem2 = pollItemList.get(1);

                pollAnswer1ByUser3 = createAndGetPollAnswer(user3.getId(), poll, pollItem1, userHoldingStock3.getQuantity());
                pollAnswer2ByUser3 = new PollAnswer();
                pollAnswer2ByUser3.setPollId(poll.getId());
                pollAnswer2ByUser3.setPollItemId(pollItem2.getId());
                pollAnswer2ByUser3.setUserId(user3.getId());
                pollAnswer2ByUser3.setStockQuantity(0L);
                pollAnswer1ByUser4 = new PollAnswer();
                pollAnswer1ByUser4.setPollId(poll.getId());
                pollAnswer1ByUser4.setPollItemId(pollItem1.getId());
                pollAnswer1ByUser4.setUserId(user4.getId());
                pollAnswer1ByUser4.setStockQuantity(0L);
                pollAnswer2ByUser4 = createAndGetPollAnswer(user4.getId(), poll, pollItem2, userHoldingStock4.getQuantity());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(BoardGroup.ACTION).size();
                assertResponse(callApiAndGetResult(BoardGroup.ACTION.name()), totalElements);
            }
        }

        @Nested
        class AndSurveyMultiAnswer {
            @BeforeEach
            void setUp() {
                post1 = createPostWithPolls(user1, someString(10), board, 1, SelectionOption.MULTIPLE_ITEMS);
                post2 = createPostWithDigitalDocument(user2, someString(10), zipFileKey, zipFilePath, board, stock);

                final Poll poll = post1.getFirstPoll();
                final List<PollItem> pollItemList = poll.getPollItemList();

                final PollItem pollItem1 = pollItemList.get(0);
                final PollItem pollItem2 = pollItemList.get(1);

                pollAnswer1ByUser3 = createAndGetPollAnswer(user3.getId(), poll, pollItem1, userHoldingStock3.getQuantity());
                pollAnswer2ByUser3 = createAndGetPollAnswer(user3.getId(), poll, pollItem2, userHoldingStock3.getQuantity());
                pollAnswer1ByUser4 = createAndGetPollAnswer(user4.getId(), poll, pollItem1, userHoldingStock4.getQuantity());
                pollAnswer2ByUser4 = createAndGetPollAnswer(user4.getId(), poll, pollItem2, userHoldingStock4.getQuantity());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnPosts() throws Exception {
                final long totalElements = postsByBoardGroupTestHolder.getPosts(BoardGroup.ACTION).size();
                assertResponse(callApiAndGetResult(BoardGroup.ACTION.name()), totalElements);
            }
        }

        @Nested
        class AndMultiSurveyCheckVoteCountSum {
            private final int addPollSize = 2;
            private List<PollAnswer> answerList;

            @BeforeEach
            void setUp() {
                userHoldingStock1.setQuantity(100L);
                userHoldingStock2.setQuantity(200L);
                userHoldingStock3.setQuantity(300L);
                userHoldingStock4.setQuantity(400L);
                itUtil.updateUserHoldingStock(userHoldingStock1);
                itUtil.updateUserHoldingStock(userHoldingStock2);
                itUtil.updateUserHoldingStock(userHoldingStock3);
                itUtil.updateUserHoldingStock(userHoldingStock4);
            }

            private void assertResponseMultiPoll(GetBoardGroupPostResponse response, long totalElements) {
                final Paging paging = response.getPaging();
                final List<PostResponse> postResponseList = response.getData();

                assertPaging(paging, totalElements);
                final PostResponse postResponse = postResponseList.get(0);
                final List<PollResponse> pollResponseList = postResponse.getPolls();
                final PollResponse pollResponse = pollResponseList.get(0);

                final Map<Long, List<PollAnswer>> answerUserMap = answerList
                    .stream()
                    .collect(Collectors.groupingBy(PollAnswer::getUserId));

                long stockQuantity = answerUserMap.values().stream()
                    .mapToLong(value -> value.get(0).getStockQuantity())
                    .sum();

                assertThat(pollResponse.getVoteTotalStockSum(), is(stockQuantity));
                assertThat(pollResponse.getVoteTotalCount(), is(answerUserMap.size()));
            }

            @Nested
            class AndMultiSurveySingleAnswer {
                /*
                 * A설문: a(100주) / b(200주) / c(300주)
                 * B설문: b(200주) / c(300주) / d(400주)
                 * <p>
                 * 참여인원: 4명(a,b,c,d) / 참여주식수: 1000주(100+200+300+400)
                 */

                @BeforeEach
                void setUp() {
                    post1 = createPostWithPolls(user1, someString(10), board, addPollSize, SelectionOption.SINGLE_ITEM);

                    final List<Poll> polls = post1.getPolls();

                    answerList = new ArrayList<>();

                    final Poll poll0 = polls.get(0);
                    final PollItem poll0Item0 = poll0.getPollItemList().get(0);
                    final PollItem poll0Item1 = poll0.getPollItemList().get(1);
                    final PollItem poll0Item2 = poll0.getPollItemList().get(2);
                    answerList.add(createAndGetPollAnswer(user1.getId(), poll0, poll0Item0, userHoldingStock1.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user2.getId(), poll0, poll0Item1, userHoldingStock2.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user3.getId(), poll0, poll0Item2, userHoldingStock3.getQuantity()));

                    final Poll poll1 = polls.get(1);
                    final PollItem poll1Item1 = poll1.getPollItemList().get(1);
                    final PollItem poll1Item2 = poll1.getPollItemList().get(2);
                    final PollItem poll1Item3 = poll1.getPollItemList().get(3);
                    answerList.add(createAndGetPollAnswer(user2.getId(), poll1, poll1Item1, userHoldingStock2.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user3.getId(), poll1, poll1Item2, userHoldingStock3.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user4.getId(), poll1, poll1Item3, userHoldingStock4.getQuantity()));
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnPosts() throws Exception {
                    final long totalElements = postsByBoardGroupTestHolder.getPosts(BoardGroup.ACTION).size();
                    assertResponseMultiPoll(callApiAndGetResult(BoardGroup.ACTION.name()), totalElements);
                }
            }

            @Nested
            class AndMultiSurveyMultiAnswer {
                /*
                 * A설문: a(100주) / a(100주) / b(200주) / c(300주)
                 * B설문: b(200주) / c(300주) / d(400주) / d(400주)
                 * <p>
                 * 참여인원: 4명(a,b,c,d) / 참여주식수: 1000주(100+200+300+400)
                 */

                @BeforeEach
                void setUp() {
                    post1 = createPostWithPolls(user1, someString(10), board, addPollSize, SelectionOption.MULTIPLE_ITEMS);

                    final List<Poll> polls = post1.getPolls();

                    answerList = new ArrayList<>();

                    final Poll poll0 = polls.get(0);
                    final PollItem poll0Item0 = poll0.getPollItemList().get(0);
                    final PollItem poll0Item1 = poll0.getPollItemList().get(1);
                    final PollItem poll0Item2 = poll0.getPollItemList().get(2);
                    final PollItem poll0Item3 = poll0.getPollItemList().get(3);
                    answerList.add(createAndGetPollAnswer(user1.getId(), poll0, poll0Item0, userHoldingStock1.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user1.getId(), poll0, poll0Item1, userHoldingStock1.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user2.getId(), poll0, poll0Item2, userHoldingStock2.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user3.getId(), poll0, poll0Item3, userHoldingStock3.getQuantity()));

                    final Poll poll1 = polls.get(1);
                    final PollItem poll1Item0 = poll1.getPollItemList().get(0);
                    final PollItem poll1Item1 = poll1.getPollItemList().get(1);
                    final PollItem poll1Item2 = poll1.getPollItemList().get(2);
                    final PollItem poll1Item3 = poll1.getPollItemList().get(3);
                    answerList.add(createAndGetPollAnswer(user2.getId(), poll1, poll1Item1, userHoldingStock2.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user3.getId(), poll1, poll1Item2, userHoldingStock3.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user4.getId(), poll1, poll1Item3, userHoldingStock4.getQuantity()));
                    answerList.add(createAndGetPollAnswer(user4.getId(), poll1, poll1Item0, userHoldingStock4.getQuantity()));
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnPosts() throws Exception {
                    final long totalElements = postsByBoardGroupTestHolder.getPosts(BoardGroup.ACTION).size();
                    assertResponseMultiPoll(callApiAndGetResult(BoardGroup.ACTION.name()), totalElements);
                }
            }
        }
    }
}
