package ag.act.api.stocksolidarity.leaderelection;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP;
import static ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class WithdrawSolidarityLeaderApplicantApiIntegrationTest extends AbstractSolidarityLeaderElectionApiIntegrationTest {

    @SuppressWarnings("LineLength")
    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId}";

    private SolidarityLeaderElection election;
    private SolidarityLeaderApplicant applicant;
    private SolidarityLeaderElectionStatusGroup expectedElectionStatusGroup;
    private int candidateCount;
    private Long postId;

    @Nested
    class WhenSuccess {

        @Nested
        class WhenSavedApplicant {

            @Nested
            class WhenSingleCandidate {

                @Nested
                class WhenElectionIsPending {

                    @BeforeEach
                    void setUp() {
                        candidateCount = 0;
                        expectedElectionStatusGroup = SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP;
                        election = createElection(CANDIDATE_REGISTER_PENDING_STATUS_GROUP);
                        applicant = createApplicant(SAVE);
                    }

                    @Test
                    @DisplayName("단일 사용자가 임시저장한 지원을 취소하면 선출이 FINISHED 상태로 변경되어야 한다.")
                    void shouldEndElection() throws Exception {
                        MvcResult mvcResult = callApi(status().isOk());
                        itUtil.assertSimpleOkay(mvcResult);

                        assertElection();
                        assertApplicantDeleted();
                    }
                }
            }

            @Nested
            class WhenMultipleCandidate {

                @Nested
                class WhenElectionIsPending {

                    @BeforeEach
                    void setUp() {
                        candidateCount = 0;
                        expectedElectionStatusGroup = CANDIDATE_REGISTER_PENDING_STATUS_GROUP;
                        election = createElection(CANDIDATE_REGISTER_PENDING_STATUS_GROUP);
                        applicant = createApplicant(SAVE);

                        // 다른 사용자도 임시 저장
                        User anotherUser = itUtil.createUser();
                        itUtil.createSolidarityLeaderApplicant(solidarity.getId(), anotherUser.getId(), SAVE, election.getId());
                    }

                    @Test
                    void shouldNotFinishedElection() throws Exception {
                        MvcResult mvcResult = callApi(status().isOk());
                        itUtil.assertSimpleOkay(mvcResult);

                        assertElection();
                        assertApplicantDeleted();
                    }
                }

                @Nested
                class WhenElectionIsInCandidateRegisterPeriod {

                    @BeforeEach
                    void setUp() {
                        candidateCount = 1;

                        expectedElectionStatusGroup = CANDIDATE_REGISTER_STATUS_GROUP;
                        election = createElection(CANDIDATE_REGISTER_STATUS_GROUP, DateTimeUtil.getTodayLocalDateTime());
                        applicant = createApplicant(SAVE);

                        // 다른 사용자는 지원완료
                        User anotherUser = itUtil.createUser();
                        itUtil.createSolidarityLeaderApplicant(solidarity.getId(), anotherUser.getId(), COMPLETE, election.getId());
                        election = itUtil.updateCandidateCount(election, 1);
                    }

                    @Test
                    void shouldNotFinishedElection() throws Exception {
                        MvcResult mvcResult = callApi(status().isOk());
                        itUtil.assertSimpleOkay(mvcResult);

                        assertElection();
                        assertApplicantDeleted();
                    }
                }
            }
        }

        @Nested
        class WhenCompletedApplicant {

            @Nested
            class WhenSingleCandidate {

                @BeforeEach
                void setUp() {
                    candidateCount = 0;
                    expectedElectionStatusGroup = SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP;
                    election = createElection(CANDIDATE_REGISTER_STATUS_GROUP, DateTimeUtil.getTodayLocalDateTime());
                    applicant = createApplicant(COMPLETE);

                    createElectionStartPush();
                }

                @Test
                void shouldFinishElection() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());
                    itUtil.assertSimpleOkay(mvcResult);

                    assertElectionFinishedWithNoCandidate();
                    assertApplicantDeleted();
                    assertUnsentPushNotExists();
                }

                private void assertUnsentPushNotExists() {
                    final List<Push> pushes = itUtil.findAllPushesByPostIdAndSendStatus(postId, PushSendStatus.READY);
                    assertThat(pushes.size(), is(0));
                }
            }

            @Nested
            class WhenMultipleCandidates {

                @BeforeEach
                void setUp() {
                    candidateCount = 1;
                    expectedElectionStatusGroup = CANDIDATE_REGISTER_STATUS_GROUP;
                    election = createElection(CANDIDATE_REGISTER_STATUS_GROUP, DateTimeUtil.getTodayLocalDateTime());
                    applicant = createApplicant(COMPLETE);
                    createElectionStartPush();

                    // 다른 사용자도 지원완료
                    User anotherUser = itUtil.createUser();
                    itUtil.createSolidarityLeaderApplicant(solidarity.getId(), anotherUser.getId(), COMPLETE, election.getId());
                    election = itUtil.updateCandidateCount(election, 2);
                }

                @Test
                void shouldNotFinishElection() throws Exception {
                    MvcResult mvcResult = callApi(status().isOk());
                    itUtil.assertSimpleOkay(mvcResult);

                    assertElection();
                    assertApplicantDeleted();
                    assertUnsentPushNotDeleted();
                }

                private void assertUnsentPushNotDeleted() {
                    final List<Push> pushes = itUtil.findAllPushesByPostIdAndSendStatus(postId, PushSendStatus.READY);
                    assertThat(pushes.size(), is(1));
                }
            }

            private void createElectionStartPush() {
                final Push push = itUtil.createPush(
                    "%s 주주대표 선출 절차".formatted(stock.getName()),
                    "%s 주주대표 선출 절차가 시작되었습니다. 후보자들의 공약을 살펴보고, 주주대표에 관심 있다면 직접 지원해보세요.".formatted(stock.getName()),
                    PushTargetType.STOCK,
                    postId
                );
                push.setSendStatus(PushSendStatus.READY);
                itUtil.updatePush(push);
            }
        }
    }

    @Nested
    class WhenFailed {

        @Nested
        class WhenNotAuthor {

            @BeforeEach
            void setUp() {
                election = createElection(CANDIDATE_REGISTER_STATUS_GROUP, DateTimeUtil.getTodayLocalDateTime());
                applicant = createApplicant(COMPLETE);

                User anotherUser = itUtil.createUser();
                jwt = itUtil.createJwt(anotherUser.getId());
                itUtil.createUserHoldingStock(stock.getCode(), anotherUser);
            }

            @Test
            void shouldReturnBad() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "작성자만이 지원을 취소할 수 있습니다.");
            }

        }
    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc.perform(
                delete(TARGET_API, stock.getCode(), election.getId(), applicant.getId())
                    .headers(headers(jwt(jwt)))
            ).andExpect(matcher)
            .andReturn();
    }

    private void assertElection() {
        SolidarityLeaderElection actual = getSolidarityLeaderElectionByStockCode();

        assertThat(actual.getId(), is(election.getId()));
        assertThat(actual.getElectionStatus(), is(expectedElectionStatusGroup.electionStatus()));
        assertThat(actual.getElectionStatusDetails(), is(expectedElectionStatusGroup.electionStatusDetails()));
        assertThat(actual.getCandidateCount(), is(candidateCount));
        assertThat(actual.getPostId(), is(postId));
    }

    private void assertElectionFinishedWithNoCandidate() {
        SolidarityLeaderElection actual = getSolidarityLeaderElectionByStockCode();

        assertThat(actual.getId(), is(election.getId()));
        assertThat(actual.getElectionStatus(), is(SolidarityLeaderElectionStatus.FINISHED));
        assertThat(actual.getElectionStatusDetails(), is(SolidarityLeaderElectionStatusDetails.FINISHED_BY_NO_CANDIDATE));
        assertThat(actual.getCandidateCount(), is(0));
        assertThat(actual.getPostId(), is(postId));
    }

    private void assertApplicantDeleted() {
        SolidarityLeaderApplicant actual = getSolidarityLeaderApplicantBySolidarityIdAndUserId();

        assertThat(actual.getId(), is(applicant.getId()));
        assertThat(actual.getApplyStatus(), is(SolidarityLeaderElectionApplyStatus.DELETED_BY_USER));
    }

    private SolidarityLeaderApplicant createApplicant(SolidarityLeaderElectionApplyStatus applyStatus) {
        return itUtil.createSolidarityLeaderApplicant(
            solidarity.getId(),
            user.getId(),
            applyStatus,
            election.getId()
        );
    }

    private SolidarityLeaderElection createElection(SolidarityLeaderElectionStatusGroup group) {
        return createElection(group, null);
    }

    private SolidarityLeaderElection createElection(SolidarityLeaderElectionStatusGroup group, LocalDateTime startDateTime) {
        final SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stock.getCode(), group, startDateTime);

        if (group == CANDIDATE_REGISTER_PENDING_STATUS_GROUP) {
            return solidarityElection;
        }

        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        postId = post.getId();
        solidarityElection.setPostId(postId);
        return itUtil.updateSolidarityElection(solidarityElection);
    }

}
