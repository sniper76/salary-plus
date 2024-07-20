package ag.act.api.batch.solidarity.election;


import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@SuppressWarnings({"AbbreviationAsWordInName", "checkstyle:MemberName", "checkstyle:LineLength"})
class WhenCandidateRegistrationPeriodToVotePeriodIntegrationTest extends AbstractCommonMaintainElectionBatchIntegrationTest {

    @DisplayName("[Batch] 주주대표 선출프로세스에서 후보자 등록 기간에서 투표기간으로 넘어 갈때")
    @Nested
    class WhenCandidateRegistrationPeriodToVotePeriod {

        @DisplayName("임시저장된 지원자들이 있을때")
        @Nested
        class WhenHaveSomeTemporarySavedApplicants {
            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stock.getCode(), SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP, LocalDateTime.now());
                solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection, SolidarityLeaderElectionApplyStatus.SAVE);
                solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection, SolidarityLeaderElectionApplyStatus.SAVE);

                solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(someIntegerBetween(1, 20)));
                solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now());
                solidarityElection.setVoteStartDateTime(LocalDateTime.now());
                solidarityElection.setVoteEndDateTime(LocalDateTime.now().plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays()));
                itUtil.updateSolidarityElection(solidarityElection);

                final Post post = itUtil.createSolidarityLeaderElectionPost(solidarityElection);
                solidarityElection.setPostId(post.getId());
                itUtil.updateSolidarityElection(solidarityElection);
            }

            @DisplayName("임시저장된 지원자들을 모두 EXPIRED 처리한다.")
            @Test
            void shouldReturnSuccess() throws Exception {
                final SimpleStringResponse result = callApiAndGetResult();

                assertResponse(result);
                assertExpiredTemporarySavedApplicants();
            }

            private void assertExpiredTemporarySavedApplicants() {
                final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();
                final List<SolidarityLeaderApplicant> allApplicants = itUtil.findAllSolidarityLeaderApplicantsByLeaderElectionId(solidarityLeaderElection.getId());

                allApplicants.forEach(applicant -> assertThat(applicant.getStatus(), not(SolidarityLeaderElectionApplyStatus.SAVE)));
            }
        }

        @DisplayName("투표기간이 되고")
        @Nested
        class WhenCheckVotePeriodElection {
            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stock.getCode(), SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP, LocalDateTime.now());
                solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
                solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
                solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);

                solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(someIntegerBetween(1, 20)));
                solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now());
                solidarityElection.setVoteStartDateTime(LocalDateTime.now());
                solidarityElection.setVoteEndDateTime(LocalDateTime.now().plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays()));

                final Post post = itUtil.createSolidarityLeaderElectionPost(solidarityElection);
                solidarityElection.setPostId(post.getId());

                itUtil.updateSolidarityElection(solidarityElection);
            }

            @DisplayName("투표시간에 처리해야할 프로세스를 모두 처리한다.")
            @Test
            void shouldReturnSuccess() throws Exception {
                final SimpleStringResponse result = callApiAndGetResult();

                assertResponse(result);
                assertCandidateCount();
                assertTotalStockQuantity();
                assertElectionPost();
                assertElectionVoteStartSlackMessage();
                assertElectionVoteStartPush();
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
        }

        @DisplayName("지원서를 완료한 후보자가 없을때")
        @Nested
        class WhenNoCandidates {
            @BeforeEach
            void setUp() {
                SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
                    LocalDateTime.now()
                );
                solidarityElection.setCandidateRegistrationStartDateTime(LocalDateTime.now().minusDays(someIntegerBetween(1, 20)));
                solidarityElection.setCandidateRegistrationEndDateTime(LocalDateTime.now());
                solidarityElection.setVoteStartDateTime(LocalDateTime.now());
                solidarityElection.setVoteEndDateTime(LocalDateTime.now().plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays()));

                final Post post = itUtil.createSolidarityLeaderElectionPost(solidarityElection);

                solidarityElection.setPostId(post.getId());
                itUtil.updateSolidarityElection(solidarityElection);

            }

            @DisplayName("처리해야할 프로세스를 모두 처리한다.")
            @Test
            void shouldReturnSuccess() throws Exception {
                //noinspection unused
                final SimpleStringResponse result = callApiAndGetResult();

                assertTotalStockQuantity();
                assertDeletedElectionPost();
                assertNoSlackMessage();
                assertNoPushSent();
                assertFinishedWithNoCandidateElection();
                assertResponse(result);
            }

            private void assertFinishedWithNoCandidateElection() {
                final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

                assertThat(solidarityLeaderElection.getElectionStatus(), is(SolidarityLeaderElectionStatus.FINISHED));
                assertThat(solidarityLeaderElection.getElectionStatusDetails(), is(SolidarityLeaderElectionStatusDetails.FINISHED_BY_NO_CANDIDATE));
            }

            private void assertDeletedElectionPost() {
                final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

                assertThat(solidarityLeaderElection.getPostId(), notNullValue());

                final Post post = itUtil.findPost(solidarityLeaderElection.getPostId()).orElseThrow();
                final Poll poll = post.getFirstPoll();

                assertThat(post.getBoardId(), is(board.getId()));
                assertThat(post.getTitle(), is("%s 주주대표 선출 투표".formatted(stock.getName())));
                assertThat(post.getContent(), notNullValue());
                assertThat(poll.getTitle(), is("주주대표 선출 투표"));
                assertThat(post.getStatus(), is(Status.DELETED));
            }
        }
    }
}
