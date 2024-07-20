package ag.act.api.stockhome;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.LeaderElectionWinnerResponse;
import ag.act.model.StockHomeResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetStockHomeLeaderElectionWinnerApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    @Nested
    class WhenWinnerExist {
        private User winner;

        @Nested
        class WhenAdminDirectElection {

            @BeforeEach
            void setUp() {
                winner = itUtil.createUser();
                itUtil.createUserHoldingStock(stock.getCode(), winner);

                LocalDateTime electionStartDate = getTodayLocalDateTimeWithMocking(DateTimeUtil.getTodayLocalDateTime().minusDays(7));
                SolidarityLeaderElection election = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_BY_ADMIN_STATUS_GROUP,
                    electionStartDate
                );
                SolidarityLeaderApplicant applicant = itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    winner.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    election.getId()
                );

                election.setVoteClosingDateTime(election.getVoteEndDateTime());
                given(LeaderElectionCurrentDateTimeProvider.toKoreanTimeUntilMidnightNextDay(election.getVoteEndDateTime()))
                    .willReturn(LocalDateTime.now().plusDays(1));

                election.setWinnerApplicantId(applicant.getId());
                itUtil.updateSolidarityElection(election);

                itUtil.createSolidarityLeader(solidarity, winner.getId());
            }

            @Test
            @DisplayName("관리자에 의해 주주대표가 선출된 경우 당선자 정보를 내려준다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                StockHomeResponse response = getResponse(mvcResult);
                assertWinnerExist(response, Boolean.TRUE);
            }
        }

        @Nested
        class WhenElectionFinishedOnTime {

            @BeforeEach
            void setUp() {
                winner = itUtil.createUser();
                itUtil.createUserHoldingStock(stock.getCode(), winner);

                LocalDateTime electionStartDate = getTodayLocalDateTimeWithMocking(DateTimeUtil.getTodayLocalDateTime().minusDays(7));
                SolidarityLeaderElection election = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
                    electionStartDate
                );
                SolidarityLeaderApplicant applicant = itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    winner.getId(),
                    SolidarityLeaderElectionApplyStatus.COMPLETE,
                    election.getId()
                );

                election.setVoteClosingDateTime(election.getVoteEndDateTime());
                given(LeaderElectionCurrentDateTimeProvider.toKoreanTimeUntilMidnightNextDay(election.getVoteEndDateTime()))
                    .willReturn(LocalDateTime.now().plusDays(1));

                election.setWinnerApplicantId(applicant.getId());
                itUtil.updateSolidarityElection(election);

                itUtil.createSolidarityLeader(solidarity, winner.getId());
            }

            @Test
            @DisplayName("투표가 종료되어 주주대표가 선출되어 당선된 경우 당선자 정보를 내려준다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                StockHomeResponse response = getResponse(mvcResult);
                assertWinnerExist(response, Boolean.FALSE);
            }
        }

        private void assertWinnerExist(StockHomeResponse response, Boolean isElectedByAdmin) {
            LeaderElectionWinnerResponse winner = response.getLeaderElectionDetail().getWinner();
            assertThat(winner.getIsElected(), is(Boolean.TRUE));
            assertThat(winner.getIsElectedByAdmin(), is(isElectedByAdmin));
            assertThat(winner.getNickname(), is(winner.getNickname()));
            assertThat(winner.getProfileImageUrl(), is(winner.getProfileImageUrl()));
        }
    }

    @Nested
    class WhenWinnerNotExist {

        @Nested
        class WhenElectionInProgress {

            @BeforeEach
            void setUp() {
                final LocalDateTime electionStartDate = getTodayLocalDateTimeWithMocking(DateTimeUtil.getTodayLocalDateTime());
                itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                    electionStartDate
                );
            }

            @Test
            @DisplayName("주주대표 선출이 종료되지 않은 경우 winner 정보는 전달하지 않는다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                StockHomeResponse response = getResponse(mvcResult);
                assertThat(response.getLeaderElectionDetail().getWinner(), nullValue());
            }
        }

        @Nested
        class WhenElectionFinishedWithoutWinner {

            @BeforeEach
            void setUp() {
                final LocalDateTime electionStartDate = getTodayLocalDateTimeWithMocking(DateTimeUtil.getTodayLocalDateTime().minusDays(7));
                SolidarityLeaderElection election = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_WITH_NO_WINNER_STATUS_GROUP,
                    electionStartDate
                );
                election.setVoteClosingDateTime(election.getVoteEndDateTime());
                itUtil.updateSolidarityElection(election);
            }

            @Test
            @DisplayName("주주대표가 선출되지 않고 종료된 경우 선출되지 않았음을 내려준다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                StockHomeResponse response = getResponse(mvcResult);
                LeaderElectionWinnerResponse winner = response.getLeaderElectionDetail().getWinner();

                assertThat(winner.getIsElected(), is(Boolean.FALSE));
                assertThat(winner.getNickname(), nullValue());
                assertThat(winner.getProfileImageUrl(), nullValue());
            }
        }
    }

    @NotNull
    private LocalDateTime getTodayLocalDateTimeWithMocking(LocalDateTime todayLocalDateTime) {

        given(LeaderElectionCurrentDateTimeProvider.get()).willReturn(todayLocalDateTime);
        given(LeaderElectionCurrentDateTimeProvider.getKoreanDateTime())
            .willReturn(
                KoreanDateTimeUtil.getNowInKoreanTime()
                    .minusHours(KOREAN_TIME_OFFSET)
            );

        return todayLocalDateTime;
    }
}
