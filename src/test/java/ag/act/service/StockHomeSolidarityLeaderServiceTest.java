package ag.act.service;

import ag.act.entity.LeaderStatus;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import ag.act.model.StockHomeLeaderResponse;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.stock.home.StockHomeSolidarityLeaderService;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockHomeSolidarityLeaderServiceTest {
    @InjectMocks
    private StockHomeSolidarityLeaderService service;
    @Mock
    private SolidarityLeaderService solidarityLeaderService;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    @Mock
    private UserService userService;
    private String stockCode;

    @Mock
    private Solidarity solidarity;
    private Long solidarityId;

    @BeforeEach
    void setUp() {
        stockCode = someStockCode();
        solidarityId = someLong();

        given(solidarity.getId()).willReturn(solidarityId);
    }

    @Nested
    class WhenGetStockHomeLeader {
        @Mock
        private SolidarityLeader solidarityLeader;
        @Mock
        private User leader;

        @Nested
        class AndSolidarityLeaderIsPresent {
            @BeforeEach
            void setUp() {
                given(solidarityLeaderService.findLeader(stockCode))
                    .willReturn(Optional.of(solidarityLeader));
                given(solidarityLeader.getSolidarity()).willReturn(solidarity);
            }

            @Test
            void shouldReturnElectedLeaderMessageResponseWithStringMessage() {

                // Given
                given(solidarityLeader.getMessage()).willReturn("message");

                // When
                StockHomeLeaderResponse actual = service.getStockHomeLeader(stockCode);

                // Then
                assertThat(actual.getStatus(), is(LeaderStatus.ELECTED.getName()));
                assertThat(actual.getMessage(), is("message"));
                assertThat(actual.getApplied(), nullValue());
                assertThat(actual.getSolidarityId(), is(solidarityId));
            }

            @Test
            void shouldReturnElectedLeaderMessageResponseWithDefaultMessage() {

                // Given
                Long leaderId = someLong();
                given(solidarityLeader.getUserId()).willReturn(leaderId);
                given(userService.getUser(leaderId))
                    .willReturn(leader);
                given(leader.getNickname()).willReturn("리더");

                // When
                StockHomeLeaderResponse actual = service.getStockHomeLeader(stockCode);

                // Then
                assertThat(actual.getStatus(), is(LeaderStatus.ELECTED.getName()));
                assertThat(actual.getMessage(), is("리더님이 주주대표로 선정되었습니다.\n주주대표 한마디가 곧 등록됩니다."));
                assertThat(actual.getApplied(), nullValue());
            }
        }

        @Nested
        class AndSolidarityLeaderIsEmpty {

            @BeforeEach
            void setUp() {
                given(solidarityService.getSolidarityByStockCode(stockCode)).willReturn(solidarity);
            }

            @Test
            void shouldReturnElectionInProgressWithTrueAppliedStatusResponse() {

                // Given
                given(solidarityLeaderApplicantService.isUserAppliedSolidarity(stockCode))
                    .willReturn(true);

                // When
                StockHomeLeaderResponse actual = service.getStockHomeLeader(stockCode);

                // Then
                assertThat(actual.getStatus(), is(LeaderStatus.ELECTION_IN_PROGRESS.getName()));
                assertThat(actual.getMessage(), nullValue());
                assertThat(actual.getApplied(), is(true));
                assertThat(actual.getSolidarityId(), is(solidarityId));
            }

            @Test
            void shouldReturnElectionInProgressWithFalseAppliedStatusResponse() {

                // Given
                given(solidarityLeaderApplicantService.isUserAppliedSolidarity(stockCode))
                    .willReturn(false);

                // When
                StockHomeLeaderResponse actual = service.getStockHomeLeader(stockCode);

                // Then
                assertThat(actual.getStatus(), is(LeaderStatus.ELECTION_IN_PROGRESS.getName()));
                assertThat(actual.getMessage(), nullValue());
                assertThat(actual.getApplied(), is(false));
                assertThat(actual.getSolidarityId(), is(solidarityId));
            }

        }
    }
}
