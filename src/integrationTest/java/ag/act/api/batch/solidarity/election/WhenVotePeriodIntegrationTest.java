package ag.act.api.batch.solidarity.election;


import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someReadyOrCompletePushSendStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@SuppressWarnings({"AbbreviationAsWordInName", "checkstyle:MemberName", "checkstyle:LineLength"})
class WhenVotePeriodIntegrationTest extends AbstractCommonMaintainElectionBatchIntegrationTest {

    @DisplayName("[Batch] 주주대표 선출프로세스에서 투표 기간일때")
    @Nested
    class WhenVotePeriod {

        @DisplayName("투표 기간이 끝나기 전에 투표가 종료되었을 때")
        @Nested
        class FinishedEarly {

            @DisplayName("Winner 가 한 명일때")
            @Nested
            class WinnerIsOnlyOne {

                private Post post;

                @BeforeEach
                void setUp() {
                    SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                        stock.getCode(),
                        SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                        LocalDateTime.now()
                    );
                    solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                    solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
                    solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);

                    solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(someIntegerBetween(1, 20)));
                    solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now());
                    solidarityElection.setVoteStartDateTime(LocalDateTime.now());
                    solidarityElection.setVoteEndDateTime(LocalDateTime.now().plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays()));

                    post = itUtil.createSolidarityLeaderElectionPostAndPoll(
                        solidarityElection,
                        List.of(solidarityLeaderApplicant1, solidarityLeaderApplicant2, solidarityLeaderApplicant3)
                    );
                    final Push push = itUtil.createPush(
                        "%s 주주대표 선출 절차".formatted(stock.getName()),
                        "%s 주주대표 선출 투표가 시작되었습니다. 내가 원하는 주주대표에 투표를 해 주세요!".formatted(stock.getName()),
                        PushTargetType.STOCK,
                        post.getId());
                    push.setSendStatus(someReadyOrCompletePushSendStatus());
                    itUtil.updatePush(push);

                    solidarityElection.setPostId(post.getId());
                    itUtil.updateSolidarityElection(solidarityElection);

                    final Poll poll = post.getFirstPoll();
                    final List<PollItem> pollItemList = poll.getPollItemList();
                    final PollItem firstCandidateApprovalPollItem = pollItemList.get(0);
                    final PollItem firstCandidateRejectionPollItem = pollItemList.get(1);
                    final PollItem secondCandidateApprovalPollItem = pollItemList.get(2);

                    // 첫번째 후보자 50% 찬성
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                    // 첫번째 후보자 20% 반대
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                }

                @DisplayName("한 명의 Winner 가 선정된다.")
                @Test
                void shouldReturnSuccess() throws Exception {
                    callApiAndAssertResponse();
                }
            }

            @DisplayName("최고 득표를 기록한 두명의 동점자가 있고")
            @Nested
            class TwoWinnersHavingSameVoteStockQuantity {

                @BeforeEach
                void setUp() {
                    SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                        stock.getCode(),
                        SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                        LocalDateTime.now()
                    );
                    solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                    solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
                    solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);

                    solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(someIntegerBetween(1, 20)));
                    solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now());
                    solidarityElection.setVoteStartDateTime(LocalDateTime.now());
                    solidarityElection.setVoteEndDateTime(LocalDateTime.now().plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays()));

                    Post post = itUtil.createSolidarityLeaderElectionPostAndPoll(
                        solidarityElection,
                        List.of(solidarityLeaderApplicant1, solidarityLeaderApplicant2, solidarityLeaderApplicant3)
                    );
                    final Push push = itUtil.createPush(
                        "%s 주주대표 선출 절차".formatted(stock.getName()),
                        "%s 주주대표 선출 투표가 시작되었습니다. 내가 원하는 주주대표에 투표를 해 주세요!".formatted(stock.getName()),
                        PushTargetType.STOCK,
                        post.getId());
                    push.setSendStatus(someReadyOrCompletePushSendStatus());
                    itUtil.updatePush(push);

                    solidarityElection.setPostId(post.getId());
                    itUtil.updateSolidarityElection(solidarityElection);

                    final Poll poll = post.getFirstPoll();
                    final List<PollItem> pollItemList = poll.getPollItemList();
                    final PollItem firstCandidateApprovalPollItem = pollItemList.get(0);
                    final PollItem firstCandidateRejectionPollItem = pollItemList.get(1);
                    final PollItem secondCandidateApprovalPollItem = pollItemList.get(2);

                    // 첫번째 후보자 50% 찬성
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                    // 첫번째 후보자 20% 반대
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                    // 두번째 후보자 50% 찬성
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                }

                @DisplayName("그 중 한명이 더 많은 주식을 보유한 경우")
                @Nested
                class OneHasMoreStockQuantity {

                    @BeforeEach
                    void setUp() {
                        firstCandidateHasMoreUserHoldingStocks();
                    }

                    @DisplayName("한명의 Winner 가 선정된다.")
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        callApiAndAssertResponse();
                    }
                }

                @DisplayName("두명이 모두 같은 주식수를 보유했을때")
                @Nested
                class HaveSameStockQuantity {

                    @DisplayName("첫 번째 후보자가 더 먼저 가입했을때")
                    @Nested
                    class OneHasOlderRegistrationDate {
                        @BeforeEach
                        void setUp() {
                            firstCandidateHasOlderRegistrationDate();
                        }

                        @DisplayName("첫 번째 후보자가 Winner 로 선정된다.")
                        @Test
                        void shouldReturnSuccess() throws Exception {
                            callApiAndAssertResponse();
                        }
                    }

                    @DisplayName("두 후보자가 동일한 가입일을 가지고 있을때")
                    @Nested
                    class TwoHaveSameRegistrationDate {

                        @BeforeEach
                        void setUp() {
                            final LocalDateTime createdAtToSet = LocalDateTime.now().minusDays(someIntegerBetween(1, 20));
                            firstCandidateHasOlderRegistrationDate(createdAtToSet);
                            secondCandidateHasOlderRegistrationDate(createdAtToSet);
                        }

                        @DisplayName("첫 번째 후보자가 더 많은 게시글을 작성했을때")
                        @Nested
                        class OneHasWrittenMorePosts {
                            @BeforeEach
                            void setUp() {
                                firstCandidateHasWrittenMorePosts();
                            }

                            @DisplayName("첫 번째 후보자가 Winner 로 선정된다.")
                            @Test
                            void shouldReturnSuccess() throws Exception {
                                callApiAndAssertResponse();
                            }
                        }

                        @DisplayName("작성한 게시글 수 까지 같은 경우")
                        @Nested
                        class TwoHaveWrittenSameNumberOfPosts {

                            @DisplayName("500 에러를 발생시킨다")
                            @Test
                            void shouldReturnException() throws Exception {
                                callApiAndAssert500Error();
                            }
                        }
                    }
                }
            }

            private void callApiAndAssertResponse() throws Exception {
                final SimpleStringResponse result = callApiAndGetResult();

                assertResponse(result);
                assertCandidateCount();
                assertTotalStockQuantity();
                assertElectionPost();
                assertEarlyFinishedElection();
                assertElectionFinishedSlackMessage();
                assertElectionFinishedPush();
                assertElectedSolidarityLeader();
            }

            private void assertElectedSolidarityLeader() {
                final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
                final Long winnerApplicantId = solidarityLeaderElection.getWinnerApplicantId();

                assertThat(winnerApplicantId, notNullValue());

                final SolidarityLeader solidarityLeader = itUtil.findSolidarityLeader(stock.getCode()).orElseThrow();
                assertThat(solidarityLeader.getUserId(), is(solidarityLeaderApplicant1.getUserId()));
            }

            private void assertEarlyFinishedElection() {
                final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

                assertTime(solidarityLeaderElection.getVoteClosingDateTime(), todayLocalDateTime);
                assertTime(solidarityLeaderElection.getDisplayEndDateTime(), midnightNextDayLocalDateTime);
                assertThat(solidarityLeaderElection.getElectionStatus(), is(SolidarityLeaderElectionStatus.FINISHED));
                assertThat(solidarityLeaderElection.getElectionStatusDetails(), is(SolidarityLeaderElectionStatusDetails.FINISHED_EARLY));
            }
        }

        @DisplayName("투표 기간이 끝나고 투표가 기간이 완료되어 종료되었을 때")
        @Nested
        class FinishedOnTime {
            @DisplayName("Winner 가 나왔을때")
            @Nested
            class HaveWinner {
                @DisplayName("Winner 가 나왔을때")
                @Nested
                class WinnerIsOnlyOne {

                    @BeforeEach
                    void setUp() {
                        SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                            stock.getCode(),
                            SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                            LocalDateTime.now()
                        );
                        solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                        solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
                        solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);

                        solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(10));
                        solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now().minusDays(5));
                        solidarityElection.setVoteStartDateTime(LocalDateTime.now().minusDays(5));
                        solidarityElection.setVoteEndDateTime(todayLocalDateTime.minusSeconds(1));// already finished period
                        solidarityElection.setTotalStockQuantity(SOLIDARITY_STOCK_TOTAL_ISSUED_QUANTITY);

                        Post post = itUtil.createSolidarityLeaderElectionPostAndPoll(
                            solidarityElection,
                            List.of(solidarityLeaderApplicant1, solidarityLeaderApplicant2, solidarityLeaderApplicant3)
                        );

                        solidarityElection.setPostId(post.getId());
                        itUtil.updateSolidarityElection(solidarityElection);

                        final Poll poll = post.getFirstPoll();
                        final List<PollItem> pollItemList = poll.getPollItemList();
                        final PollItem firstCandidateApprovalPollItem = pollItemList.get(0);
                        final PollItem firstCandidateRejectionPollItem = pollItemList.get(1);
                        final PollItem secondCandidateApprovalPollItem = pollItemList.get(2);
                        final PollItem secondCandidateRejectionPollItem = pollItemList.get(3);

                        // 첫번째 후보자 30% 찬성
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                        // 첫번째 후보자 20% 반대
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                        // 두번째 후보자 40% 찬성
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                        // 두번째 후보자 50% 반대
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    }

                    @DisplayName("선출 완료 및 Winner 선정")
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        callApiAndAssertResponse();
                    }
                }

                @DisplayName("최고 득표를 기록한 두명의 동점자가 있고")
                @Nested
                class TwoWinnersHavingSameVoteStockQuantity {

                    @BeforeEach
                    void setUp() {
                        SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                            stock.getCode(),
                            SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                            LocalDateTime.now()
                        );
                        solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                        solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
                        solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);

                        solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(10));
                        solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now().minusDays(5));
                        solidarityElection.setVoteStartDateTime(LocalDateTime.now().minusDays(5));
                        solidarityElection.setVoteEndDateTime(todayLocalDateTime.minusSeconds(1));// already finished period
                        solidarityElection.setTotalStockQuantity(SOLIDARITY_STOCK_TOTAL_ISSUED_QUANTITY);

                        Post post = itUtil.createSolidarityLeaderElectionPostAndPoll(
                            solidarityElection,
                            List.of(solidarityLeaderApplicant1, solidarityLeaderApplicant2, solidarityLeaderApplicant3)
                        );

                        solidarityElection.setPostId(post.getId());
                        itUtil.updateSolidarityElection(solidarityElection);

                        final Poll poll = post.getFirstPoll();
                        final List<PollItem> pollItemList = poll.getPollItemList();
                        final PollItem firstCandidateApprovalPollItem = pollItemList.get(0);
                        final PollItem firstCandidateRejectionPollItem = pollItemList.get(1);
                        final PollItem secondCandidateApprovalPollItem = pollItemList.get(2);

                        // 첫번째 후보자 50% 찬성
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                        // 첫번째 후보자 20% 반대
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                        // 두번째 후보자 50% 찬성
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                        itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    }

                    @DisplayName("그 중 한명이 더 많은 주식을 보유한 경우")
                    @Nested
                    class OneHasMoreStockQuantity {

                        @BeforeEach
                        void setUp() {
                            firstCandidateHasMoreUserHoldingStocks();
                        }

                        @DisplayName("한명의 Winner 가 선정된다.")
                        @Test
                        void shouldReturnSuccess() throws Exception {
                            callApiAndAssertResponse();
                        }
                    }

                    @DisplayName("두명이 모두 같은 주식수를 보유했을때")
                    @Nested
                    class HaveSameStockQuantity {

                        @DisplayName("첫 번째 후보자가 더 먼저 가입했을때")
                        @Nested
                        class OneHasOlderRegistrationDate {
                            @BeforeEach
                            void setUp() {
                                firstCandidateHasOlderRegistrationDate();
                            }

                            @DisplayName("첫 번째 후보자가 Winner 로 선정된다.")
                            @Test
                            void shouldReturnSuccess() throws Exception {
                                callApiAndAssertResponse();
                            }
                        }

                        @DisplayName("두 후보자가 동일한 가입일을 가지고 있을때")
                        @Nested
                        class TwoHaveSameRegistrationDate {

                            @BeforeEach
                            void setUp() {
                                final LocalDateTime createdAtToSet = LocalDateTime.now().minusDays(someIntegerBetween(1, 20));
                                firstCandidateHasOlderRegistrationDate(createdAtToSet);
                                secondCandidateHasOlderRegistrationDate(createdAtToSet);
                            }

                            @DisplayName("첫 번째 후보자가 더 많은 게시글을 작성했을때")
                            @Nested
                            class OneHasWrittenMorePosts {
                                @BeforeEach
                                void setUp() {
                                    firstCandidateHasWrittenMorePosts();
                                }

                                @DisplayName("첫 번째 후보자가 Winner 로 선정된다.")
                                @Test
                                void shouldReturnSuccess() throws Exception {
                                    callApiAndAssertResponse();
                                }
                            }

                            @DisplayName("작성한 게시글 수 까지 같은 경우")
                            @Nested
                            class TwoHaveWrittenSameNumberOfPosts {

                                @DisplayName("500 에러를 발생시킨다")
                                @Test
                                void shouldReturnException() throws Exception {
                                    callApiAndAssert500Error();
                                }
                            }
                        }
                    }
                }

                private void callApiAndAssertResponse() throws Exception {
                    final SimpleStringResponse result = callApiAndGetResult();

                    assertResponse(result);
                    assertCandidateCount();
                    assertTotalStockQuantity();
                    assertElectionPost();
                    assertFinishedOnTimeElection();
                    assertElectionFinishedSlackMessage();
                    assertElectionFinishedPush();
                    assertElectedSolidarityLeader();
                }

                private void assertFinishedOnTimeElection() {
                    final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

                    assertTime(solidarityLeaderElection.getVoteClosingDateTime(), solidarityLeaderElection.getVoteEndDateTime());
                    assertTime(solidarityLeaderElection.getDisplayEndDateTime(), midnightNextDayLocalDateTime);
                    assertThat(solidarityLeaderElection.getElectionStatus(), is(SolidarityLeaderElectionStatus.FINISHED));
                    assertThat(solidarityLeaderElection.getElectionStatusDetails(), is(SolidarityLeaderElectionStatusDetails.FINISHED_ON_TIME));
                }

                private void assertElectedSolidarityLeader() {
                    final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
                    final Long winnerApplicantId = solidarityLeaderElection.getWinnerApplicantId();

                    assertThat(winnerApplicantId, notNullValue());

                    final SolidarityLeader solidarityLeader = itUtil.findSolidarityLeader(stock.getCode()).orElseThrow();
                    assertThat(solidarityLeader.getUserId(), is(solidarityLeaderApplicant1.getUserId()));
                }
            }

            @DisplayName("Winner 가 없을때")
            @Nested
            class AndHaveNoWinner {

                @BeforeEach
                void setUp() {
                    SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                        stock.getCode(),
                        SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                        LocalDateTime.now()
                    );
                    solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                    solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
                    solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);

                    solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(10));
                    solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now().minusDays(5));
                    solidarityElection.setVoteStartDateTime(LocalDateTime.now().minusDays(5));
                    solidarityElection.setVoteEndDateTime(todayLocalDateTime.minusSeconds(1));// already finished period
                    solidarityElection.setTotalStockQuantity(SOLIDARITY_STOCK_TOTAL_ISSUED_QUANTITY);

                    Post post = itUtil.createSolidarityLeaderElectionPostAndPoll(
                        solidarityElection,
                        List.of(solidarityLeaderApplicant1, solidarityLeaderApplicant2, solidarityLeaderApplicant3)
                    );

                    solidarityElection.setPostId(post.getId());
                    itUtil.updateSolidarityElection(solidarityElection);

                    final Poll poll = post.getFirstPoll();
                    final List<PollItem> pollItemList = poll.getPollItemList();
                    final PollItem firstCandidateApprovalPollItem = pollItemList.get(0);
                    final PollItem firstCandidateRejectionPollItem = pollItemList.get(1);
                    final PollItem secondCandidateApprovalPollItem = pollItemList.get(2);
                    final PollItem secondCandidateRejectionPollItem = pollItemList.get(3);

                    // 첫번째 후보자 20% 찬성
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                    // 첫번째 후보자 20% 반대
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), firstCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                    // 두번째 후보자 40% 찬성
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateApprovalPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);

                    // 두번째 후보자 50% 반대
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                    itUtil.createPollAnswer(createUserAndHoldingStock().getId(), poll.getId(), secondCandidateRejectionPollItem.getId(), USER_HOLDING_STOCK_QUANTITY);
                }

                @DisplayName("선출 완료 종료처리")
                @Test
                void shouldReturnSuccess() throws Exception {
                    final SimpleStringResponse result = callApiAndGetResult();

                    assertResponse(result);
                    assertCandidateCount();
                    assertTotalStockQuantity();
                    assertElectionPost();
                    assertFinishedOnTimeElectionWithNoWinner();
                    assertElectionFinishedSlackMessage();
                    assertElectionFinishedPush();
                    assertNoSolidarityLeader();
                }

                private void assertNoSolidarityLeader() {
                    final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
                    final Long winnerApplicantId = solidarityLeaderElection.getWinnerApplicantId();

                    assertThat(winnerApplicantId, nullValue());
                    assertThat(itUtil.findSolidarityLeader(stock.getCode()).isEmpty(), is(true));
                }

                private void assertFinishedOnTimeElectionWithNoWinner() {
                    final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

                    assertTime(solidarityLeaderElection.getVoteClosingDateTime(), solidarityLeaderElection.getVoteEndDateTime());
                    assertTime(solidarityLeaderElection.getDisplayEndDateTime(), midnightNextDayLocalDateTime);
                    assertThat(solidarityLeaderElection.getElectionStatus(), is(SolidarityLeaderElectionStatus.FINISHED));
                    assertThat(solidarityLeaderElection.getElectionStatusDetails(), is(SolidarityLeaderElectionStatusDetails.FINISHED_ON_TIME_WITH_NO_WINNER));
                }


            }
        }

        private void callApiAndAssert500Error() throws Exception {
            final MvcResult mvcResult = callApi(status().is5xxServerError());

            itUtil.assertErrorResponse(mvcResult, INTERNAL_SERVER_ERROR_STATUS, "알 수 없는 오류가 발생하였습니다. 잠시 후에 다시 이용해 주세요.");
        }

        private void assertElectionPost() {
            final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

            assertThat(solidarityLeaderElection.getPostId(), notNullValue());

            final Post post = itUtil.findPost(solidarityLeaderElection.getPostId()).orElseThrow();
            final Poll poll = post.getFirstPoll();
            final List<PollItem> pollItemList = poll.getPollItemList();
            final List<SolidarityLeaderElectionPollItemMapping> electionPollItemMappings = getPollItemMappings(solidarityLeaderElection);

            assertThat(post.getBoardId(), is(board.getId()));
            assertThat(post.getTitle(), is("%s 주주대표 선출 투표".formatted(stock.getName())));
            assertThat(post.getContent(), notNullValue());
            assertThat(poll.getTitle(), is("주주대표 선출 투표"));
            assertThat(post.getStatus(), is(Status.INACTIVE_BY_ADMIN));
            assertThat(pollItemList.size(), is(electionPollItemMappings.size()));

            assertThat(electionPollItemMappings.get(0).getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicant1.getId()));
            assertThat(electionPollItemMappings.get(1).getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicant1.getId()));
            assertThat(electionPollItemMappings.get(2).getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicant2.getId()));
            assertThat(electionPollItemMappings.get(3).getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicant2.getId()));
            assertThat(electionPollItemMappings.get(4).getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicant3.getId()));
            assertThat(electionPollItemMappings.get(5).getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicant3.getId()));
        }

        private void firstCandidateHasMoreUserHoldingStocks() {
            final User user = itUtil.findUser(solidarityLeaderApplicant1.getUserId());
            user.getUserHoldingStocks().stream()
                .filter(userHoldingStock -> userHoldingStock.getStockCode().equals(stock.getCode()))
                .findFirst()
                .ifPresent(userHoldingStock -> {
                    userHoldingStock.setQuantity(userHoldingStock.getQuantity() + someIntegerBetween(1, 100));
                });
            itUtil.updateUser(user);
        }

        private void firstCandidateHasWrittenMorePosts() {
            final User user = itUtil.findUser(solidarityLeaderApplicant1.getUserId());

            IntStream.range(0, someIntegerBetween(1, 10))
                .forEach(i -> itUtil.createPost(itUtil.createBoard(stock), user.getId()));
        }

        private void firstCandidateHasOlderRegistrationDate() {
            firstCandidateHasOlderRegistrationDate(LocalDateTime.now().minusDays(someIntegerBetween(1, 20)));
        }

        private void firstCandidateHasOlderRegistrationDate(LocalDateTime createdAt) {
            final User user = itUtil.findUser(solidarityLeaderApplicant1.getUserId());
            user.setCreatedAt(createdAt);
            itUtil.updateUser(user);
        }

        private void secondCandidateHasOlderRegistrationDate(LocalDateTime createdAt) {
            final User user = itUtil.findUser(solidarityLeaderApplicant2.getUserId());
            user.setCreatedAt(createdAt);
            itUtil.updateUser(user);
        }
    }
}
