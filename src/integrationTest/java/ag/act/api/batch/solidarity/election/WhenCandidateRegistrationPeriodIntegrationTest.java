package ag.act.api.batch.solidarity.election;


import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings({"AbbreviationAsWordInName", "checkstyle:MemberName", "checkstyle:LineLength"})
class WhenCandidateRegistrationPeriodIntegrationTest extends AbstractCommonMaintainElectionBatchIntegrationTest {

    @DisplayName("[Batch] 주주대표 선출프로세스에서 후보자 등록 기간일때")
    @Nested
    class WhenCandidateRegistrationPeriod {
        @BeforeEach
        void setUp() {
            final SolidarityLeaderElection solidarityElection = createLeaderElection();
            solidarityLeaderApplicant1 = createSolidarityLeaderApplicant(solidarityElection);
            solidarityLeaderApplicant2 = createSolidarityLeaderApplicant(solidarityElection);
            solidarityLeaderApplicant3 = createSolidarityLeaderApplicant(solidarityElection);
        }

        @DisplayName("후보자 수를 동기화하고 poll 없이 선출 게시글을 생성한다.")
        @Test
        void shouldReturnSuccess() throws Exception {
            final SimpleStringResponse result = callApiAndGetResult();

            assertResponse(result);
            assertCandidateCount();
            assertElectionPost();
            assertElectionCandidateRegistrationStartPush();
        }

        private SolidarityLeaderElection createLeaderElection() {
            return itUtil.createSolidarityElection(
                stock.getCode(),
                SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                LocalDateTime.now()
            );
        }

        private void assertElectionPost() {
            final SolidarityLeaderElection solidarityLeaderElection = getSolidarityLeaderElection();

            assertThat(solidarityLeaderElection.getPostId(), notNullValue());

            final Post post = itUtil.findPost(solidarityLeaderElection.getPostId()).orElseThrow();
            final Poll poll = post.getFirstPoll();

            assertThat(post.getBoardId(), is(board.getId()));
            assertThat(post.getTitle(), is("%s 주주대표 선출 투표".formatted(stock.getName())));
            assertThat(post.getContent(), notNullValue());
            assertThat(post.getStatus(), is(ag.act.model.Status.INACTIVE_BY_ADMIN));
            assertThat(poll, is(nullValue()));
        }
    }
}
