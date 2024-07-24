package ag.act.api.election;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SolidarityLeaderElectionApplicantDataLabelResponse;
import ag.act.model.SolidarityLeaderElectionApplicantDataResponse;
import ag.act.model.SolidarityLeaderElectionDetailResponse;
import ag.act.model.SolidarityLeaderElectionVoteItemDataResponse;
import ag.act.module.solidarity.election.label.SolidarityLeaderElectionConditionalLabelDataGenerator;
import ag.act.util.NumberUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType.APPROVAL;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType.REJECTION;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetSolidarityLeaderElectionPostPollApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/vote-items";

    private String jwt;
    private Stock stock;
    private Board board;
    private User voter;
    private User candidateUser1;
    private User candidateUser2;
    private User candidateUser3;
    private User candidateUser4;
    private Post post;
    private Poll poll;
    private UserHoldingStock voterHoldingStock;
    private SolidarityLeaderElection solidarityLeaderElection;
    private List<SolidarityLeaderApplicant> solidarityLeaderApplicants;
    private List<User> applicantUsers;
    private Long solidarityLeaderElectionId;
    private Solidarity solidarity;
    private List<PollItem> pollItemList;
    private List<Optional<TestVoteItem>> testVoteItemDataList;
    private List<PollAnswer> pollAnswers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        itUtil.init();
        voter = itUtil.createUser();

        jwt = itUtil.createJwt(voter.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
        solidarity = itUtil.createSolidarity(stock.getCode());

        final LocalDateTime localDateTime = LocalDateTime.now();

        createApplicantUsers();
        createElectionAndPost(localDateTime);
    }

    private void createElectionAndPost(LocalDateTime localDateTime) {
        final LocalDateTime endDateTime = localDateTime.plusDays(5);

        solidarityLeaderElection = itUtil.createSolidarityElection(stock.getCode(), localDateTime);
        solidarityLeaderElectionId = solidarityLeaderElection.getId();

        solidarityLeaderApplicants = List.of(
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser1.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser2.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser3.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser4.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId)
        );

        final User superAdminUser = itUtil.createSuperAdminUser();
        post = itUtil.createPost(board, superAdminUser.getId());
        post = itUtil.createPoll(post, solidarityLeaderApplicants.size() * 2, SelectionOption.MULTIPLE_ITEMS);
        poll = post.getFirstPoll();
        pollItemList = poll.getPollItemList();

        poll.setTargetStartDate(localDateTime);
        poll.setTargetEndDate(endDateTime);
        itUtil.updatePost(post);

        for (int j = 0; j < pollItemList.size(); j++) {
            itUtil.createSolidarityLeaderElectionPollItemMapping(
                solidarityLeaderElectionId,
                solidarityLeaderApplicants.get(j / 2).getId(),
                pollItemList.get(j).getId(),
                (j % 2 == 0) ? APPROVAL : REJECTION
            );
        }
        solidarityLeaderElection.setVoteStartDateTime(localDateTime);
        solidarityLeaderElection.setVoteEndDateTime(endDateTime);
        solidarityLeaderElection.setPostId(post.getId());

        solidarityLeaderElection = itUtil.updateSolidarityElection(solidarityLeaderElection);
    }

    private void createApplicantUsers() {
        candidateUser1 = itUtil.createUser();
        candidateUser2 = itUtil.createUser();
        candidateUser3 = itUtil.createUser();
        candidateUser4 = itUtil.createUser();

        itUtil.createUserHoldingStock(stock.getCode(), candidateUser1);
        itUtil.createUserHoldingStock(stock.getCode(), candidateUser2);
        itUtil.createUserHoldingStock(stock.getCode(), candidateUser3);
        itUtil.createUserHoldingStock(stock.getCode(), candidateUser4);

        applicantUsers = List.of(candidateUser1, candidateUser2, candidateUser3, candidateUser4);
    }

    private long getStockQuantityPercentage(long totalVoteQuantity, Long quantity) {
        return NumberUtil.getPercentage(quantity, totalVoteQuantity);
    }

    @Nested
    class WhenSuccess {

        private UserHoldingStock userHoldingStock1;
        private UserHoldingStock userHoldingStock2;
        private UserHoldingStock userHoldingStock3;
        private UserHoldingStock userHoldingStock11;
        private UserHoldingStock userHoldingStock22;
        private UserHoldingStock userHoldingStock33;
        private int index0Count = 0;
        private int index2Count = 0;
        private int index4Count = 0;
        private int index7Count = 0;
        private long index0Quantity = 0L;
        private long index2Quantity = 0L;
        private long index4Quantity = 0L;
        private long index7Quantity = 0L;

        @BeforeEach
        void setUp() {
            voterHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), voter);
            createUserAndHoldingStock();

            final long totalVoteQuantity = pollAnswers.stream().mapToLong(PollAnswer::getStockQuantity).sum() / applicantUsers.size();

            final long index0StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index0Quantity);
            final long index2StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index2Quantity);
            final long index4StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index4Quantity);
            final long index7StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index7Quantity);
            testVoteItemDataList = List.of(
                Optional.of(
                    new TestVoteItem(APPROVAL, index0Quantity, false, index0Count, index0StockQuantityPercentage)
                ),
                Optional.empty(),
                Optional.of(
                    new TestVoteItem(APPROVAL, index2Quantity, false, index2Count, index2StockQuantityPercentage)
                ),
                Optional.empty(),
                Optional.of(
                    new TestVoteItem(APPROVAL, index4Quantity, false, index4Count, index4StockQuantityPercentage)
                ),
                Optional.empty(),
                Optional.of(
                    new TestVoteItem(APPROVAL, 0L, false, 0, 0L)
                ),
                Optional.of(
                    new TestVoteItem(REJECTION, index7Quantity, true, index7Count, index7StockQuantityPercentage)
                )
            );
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi();

            final SolidarityLeaderElectionDetailResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SolidarityLeaderElectionDetailResponse.class
            );

            assertResponse(result);
        }

        private void createUserAndHoldingStock() {
            final User user1 = itUtil.createUser();
            final User user2 = itUtil.createUser();
            final User user3 = itUtil.createUser();
            final User user11 = itUtil.createUser();
            final User user22 = itUtil.createUser();
            final User user33 = itUtil.createUser();

            userHoldingStock1 = itUtil.createUserHoldingStock(stock.getCode(), user1);
            userHoldingStock2 = itUtil.createUserHoldingStock(stock.getCode(), user2);
            userHoldingStock3 = itUtil.createUserHoldingStock(stock.getCode(), user3);
            userHoldingStock11 = itUtil.createUserHoldingStock(stock.getCode(), user11);
            userHoldingStock22 = itUtil.createUserHoldingStock(stock.getCode(), user22);
            userHoldingStock33 = itUtil.createUserHoldingStock(stock.getCode(), user33);

            index0Count = 2;
            index2Count = 2;
            index4Count = 2;
            index7Count = 2;
            index0Quantity = userHoldingStock1.getQuantity() + userHoldingStock11.getQuantity();
            index2Quantity = userHoldingStock2.getQuantity() + userHoldingStock22.getQuantity();
            index4Quantity = userHoldingStock3.getQuantity() + userHoldingStock33.getQuantity();
            index7Quantity = userHoldingStock3.getQuantity() + voterHoldingStock.getQuantity();

            pollAnswers = List.of(
                itUtil.createPollAnswer(user1.getId(), poll.getId(), pollItemList.get(0).getId(), userHoldingStock1.getQuantity()),
                itUtil.createPollAnswer(user2.getId(), poll.getId(), pollItemList.get(2).getId(), userHoldingStock2.getQuantity()),
                itUtil.createPollAnswer(user3.getId(), poll.getId(), pollItemList.get(4).getId(), userHoldingStock3.getQuantity()),
                itUtil.createPollAnswer(user3.getId(), poll.getId(), pollItemList.get(7).getId(), userHoldingStock3.getQuantity()),
                itUtil.createPollAnswer(user11.getId(), poll.getId(), pollItemList.get(0).getId(), userHoldingStock11.getQuantity()),
                itUtil.createPollAnswer(user22.getId(), poll.getId(), pollItemList.get(2).getId(), userHoldingStock22.getQuantity()),
                itUtil.createPollAnswer(user33.getId(), poll.getId(), pollItemList.get(4).getId(), userHoldingStock33.getQuantity()),
                itUtil.createPollAnswer(voter.getId(), poll.getId(), pollItemList.get(7).getId(), voterHoldingStock.getQuantity())
            );
        }
    }

    @Nested
    class WhenNumberOfVoters {
        private int index0Count = 0;
        private int index3Count = 0;
        private int index5Count = 0;
        private int index7Count = 0;

        private long index0Quantity = 0L;
        private long index3Quantity = 0L;
        private long index5Quantity = 0L;
        private long index7Quantity = 0L;

        @BeforeEach
        void setUp() {
            createUserHoldingStock(voter);
        }

        @Nested
        class IsOdd {

            @BeforeEach
            void setUp() {
                makeAndSetTestVoteItemDataList();
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi();

                final SolidarityLeaderElectionDetailResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SolidarityLeaderElectionDetailResponse.class
                );

                assertResponse(result);
            }

        }

        @Nested
        class IsEven {

            @BeforeEach
            void setUp() {
                createUserHoldingStock(itUtil.createUser());
                makeAndSetTestVoteItemDataList();
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi();

                final SolidarityLeaderElectionDetailResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SolidarityLeaderElectionDetailResponse.class
                );

                assertResponse(result);
            }

        }

        private void createUserHoldingStock(User user) {
            final UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);

            index0Count++;
            index3Count++;
            index5Count++;
            index7Count++;
            index0Quantity += userHoldingStock.getQuantity();
            index3Quantity += userHoldingStock.getQuantity();
            index5Quantity += userHoldingStock.getQuantity();
            index7Quantity += userHoldingStock.getQuantity();

            pollAnswers.addAll(
                List.of(
                    itUtil.createPollAnswer(user.getId(), poll.getId(), pollItemList.get(0).getId(), userHoldingStock.getQuantity()),
                    itUtil.createPollAnswer(user.getId(), poll.getId(), pollItemList.get(3).getId(), userHoldingStock.getQuantity()),
                    itUtil.createPollAnswer(user.getId(), poll.getId(), pollItemList.get(5).getId(), userHoldingStock.getQuantity()),
                    itUtil.createPollAnswer(user.getId(), poll.getId(), pollItemList.get(7).getId(), userHoldingStock.getQuantity())
                )
            );
        }

        private void makeAndSetTestVoteItemDataList() {
            final long totalVoteQuantity = pollAnswers.stream().mapToLong(PollAnswer::getStockQuantity).sum() / applicantUsers.size();

            final long index0StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index0Quantity);
            final long index3StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index3Quantity);
            final long index5StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index5Quantity);
            final long index7StockQuantityPercentage = getStockQuantityPercentage(totalVoteQuantity, index7Quantity);
            testVoteItemDataList = List.of(
                Optional.of(
                    new TestVoteItem(APPROVAL, index0Quantity, true, index0Count, index0StockQuantityPercentage)
                ),
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                    new TestVoteItem(REJECTION, index3Quantity, true, index3Count, index3StockQuantityPercentage)
                ),
                Optional.empty(),
                Optional.of(
                    new TestVoteItem(REJECTION, index5Quantity, true, index5Count, index5StockQuantityPercentage)
                ),
                Optional.empty(),
                Optional.of(
                    new TestVoteItem(REJECTION, index7Quantity, true, index7Count, index7StockQuantityPercentage)
                )
            );
        }
    }

    @NotNull
    private MvcResult callApi() throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode(), solidarityLeaderElectionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt))))
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertResponse(SolidarityLeaderElectionDetailResponse result) {
        final List<SolidarityLeaderElectionApplicantDataResponse> responseList = result.getPollApplicants();
        final long totalVoteQuantity = pollAnswers.stream().mapToLong(PollAnswer::getStockQuantity).sum() / responseList.size();
        final boolean currentUserHasVoted = hasCurrentUserVoted();
        final int candidateCount = solidarityLeaderApplicants.size();

        assertThat(result.getIsVoted(), is(currentUserHasVoted));
        assertThat(responseList.size(), is(candidateCount));
        assertThat(result.getSolidarityLeaderElectionId(), is(solidarityLeaderElectionId));
        assertThat(result.getStatus(), is(solidarityLeaderElection.getElectionStatus().name()));
        assertThat(result.getTotalVoterCount(), is(pollAnswers.size() / candidateCount));

        AtomicInteger pollItemIndex = new AtomicInteger(0);

        for (int responseIndex = 0; responseIndex < responseList.size(); responseIndex++) {
            SolidarityLeaderElectionApplicantDataResponse response = responseList.get(responseIndex);
            assertThat(response.getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicants.get(responseIndex).getId()));
            assertThat(response.getNickname(), is(applicantUsers.get(responseIndex).getNickname()));
            assertThat(response.getTotalVoteStockQuantity(), is(totalVoteQuantity));

            final SolidarityLeaderElectionApplicantDataLabelResponse resolutionCondition = response.getResolutionCondition();
            assertThat(
                resolutionCondition.getRequiredStockQuantityRatio(),
                is("1/4")
            );
            assertThat(
                resolutionCondition.getStockQuantity(),
                is(solidarityLeaderElection.getTotalStockQuantity() / 4)
            );
            assertThat(
                resolutionCondition.getColor(),
                is(SolidarityLeaderElectionConditionalLabelDataGenerator.COLOR_FOR_SATISFIED_CONDITION)
            );

            final SolidarityLeaderElectionApplicantDataLabelResponse finishedEarlyCondition = response.getFinishedEarlyCondition();
            assertThat(
                finishedEarlyCondition.getStockQuantity(),
                is(solidarityLeaderElection.getTotalStockQuantity() / 2)
            );
            assertThat(
                finishedEarlyCondition.getColor(),
                is(SolidarityLeaderElectionConditionalLabelDataGenerator.COLOR_FOR_SATISFIED_CONDITION)
            );

            assertVoteItemDataList(response, pollItemIndex);
        }
    }

    private boolean hasCurrentUserVoted() {
        return testVoteItemDataList.stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(TestVoteItem::isVoted);
    }

    private void assertVoteItemDataList(
        SolidarityLeaderElectionApplicantDataResponse response,
        AtomicInteger pollItemIndex
    ) {
        final List<SolidarityLeaderElectionVoteItemDataResponse> voteItemDataList = response.getPollItemGroups();
        for (int voteItemDataIndex = 0; voteItemDataIndex < voteItemDataList.size(); voteItemDataIndex++) {
            final SolidarityLeaderElectionVoteItemDataResponse voteItemData = voteItemDataList.get(voteItemDataIndex);
            final PollItem pollItem = pollItemList.get(pollItemIndex.get());

            testVoteItemDataList.get(pollItemIndex.get())
                .ifPresent(testVoteItemData -> assertVoteItemData(voteItemData, testVoteItemData));

            assertThat(voteItemData.getPollItemId(), is(pollItem.getId()));

            pollItemIndex.incrementAndGet();
        }
    }

    private void assertVoteItemData(
        SolidarityLeaderElectionVoteItemDataResponse voteItemData,
        TestVoteItem testVoteItemData
    ) {
        assertThat(voteItemData.getType(), is(testVoteItemData.answerType().name()));
        assertThat(voteItemData.getTitle(), is(testVoteItemData.answerType().getDisplayName()));
        assertThat(voteItemData.getStockQuantity(), is(testVoteItemData.stockQuantity()));
        assertThat(voteItemData.getIsVoted(), is(testVoteItemData.isVoted()));
        assertThat(voteItemData.getVoteCount(), is(testVoteItemData.voteCount()));
        assertThat(voteItemData.getStockQuantityPercentage(), is(testVoteItemData.stockQuantityPercentage()));
    }

    record TestVoteItem(
        SolidarityLeaderElectionAnswerType answerType,
        long stockQuantity,
        boolean isVoted,
        int voteCount,
        long stockQuantityPercentage
    ) {
    }
}
