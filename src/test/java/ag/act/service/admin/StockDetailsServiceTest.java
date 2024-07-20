package ag.act.service.admin;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.converter.stock.SolidarityStatisticsDailyResponseConverter;
import ag.act.converter.stock.StockDataArrayResponseConverter;
import ag.act.converter.stock.StockDetailsResponseConverter;
import ag.act.converter.stock.StockSearchResultItemResponseConverter;
import ag.act.dto.admin.StockDetailsResponseDto;
import ag.act.dto.user.SolidarityLeaderApplicantUserDto;
import ag.act.dto.user.SolidarityLeaderUserDto;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.service.admin.stock.StockDetailsService;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserService;
import ag.act.service.solidarity.SolidarityDashboardService;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.SolidarityStatisticsService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.service.stock.StockService;
import ag.act.service.user.UserService;
import ag.act.service.user.download.csv.UserDownloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@SuppressWarnings("unused")
@MockitoSettings(strictness = Strictness.LENIENT)
class StockDetailsServiceTest {

    @InjectMocks
    private StockDetailsService service;

    @Mock
    private StockService stockService;
    @Mock
    private UserDownloadService userDownloadService;
    @Mock
    private StockDataArrayResponseConverter stockConverter;
    @Mock
    private StockSearchResultItemResponseConverter stockSearchResultItemResponseConverter;
    @Mock
    private SimpleStockResponseConverter simpleStockResponseConverter;
    @Mock
    private SolidarityStatisticsService solidarityStatisticsService;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private SolidarityStatisticsDailyResponseConverter solidarityStatisticsDailyResponseConverter;
    @Mock
    private StockDetailsResponseConverter stockDetailsResponseConverter;
    @Mock
    private SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    @Mock
    private SolidarityLeaderElectionService solidarityLeaderElectionService;
    @Mock
    private UserService userService;
    @Mock
    private SolidarityDashboardService solidarityDashboardService;
    @Mock
    private StockAcceptorUserService stockAcceptUserService;

    @Nested
    class GetStockDetails {

        @Mock
        private Solidarity solidarity;
        @Mock
        private ag.act.model.DashboardResponse dashboardResponse;
        @Mock
        private SolidarityLeader solidarityLeader;
        @Mock
        private User solidarityLeaderUser;
        @Mock
        private SolidarityLeaderElection solidarityLeaderElection;
        @Mock
        private ag.act.model.StockDetailsResponse expectedResponse;
        private ag.act.model.StockDetailsResponse actualResponse;
        private String stockCode;
        private Long solidarityLeaderUserId;
        private Long solidarityId;
        private Long solidarityLeaderElectionId;
        @Captor
        private ArgumentCaptor<StockDetailsResponseDto> detailsResponseDtoArgumentCaptor;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            solidarityId = someLong();
            solidarityLeaderUserId = someLong();
            solidarityLeaderElectionId = someLong();
            final List<SolidarityLeaderApplicantUserDto> applicantUsers = List.of();

            given(solidarity.getId()).willReturn(solidarityId);
            given(solidarityService.getSolidarityByStockCode(stockCode)).willReturn(solidarity);
            given(solidarity.getSolidarityLeader()).willReturn(solidarityLeader);
            given(solidarityLeader.getUserId()).willReturn(solidarityLeaderUserId);
            given(userService.findUser(solidarityLeaderUserId)).willReturn(Optional.of(solidarityLeaderUser));
            given(solidarityDashboardService.getDashboard(stockCode)).willReturn(dashboardResponse);
            given(solidarityLeaderApplicantService.getSolidarityLeaderApplicantUsers(solidarityId)).willReturn(applicantUsers);
            given(solidarityLeaderElectionService.findOnGoingSolidarityLeaderElection(stockCode))
                .willReturn(Optional.of(solidarityLeaderElection));
        }

        @Nested
        class WhenSolidarityLeaderNotExists extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                given(solidarity.getSolidarityLeader()).willReturn(null);
                given(stockDetailsResponseConverter.convert(
                    any(StockDetailsResponseDto.class)
                )).willReturn(expectedResponse);

                actualResponse = service.getStockDetails(stockCode);
            }

            @Test
            void shouldNotGetSolidarityLeaderApplicantUsers() {
                then(solidarityLeaderApplicantService).shouldHaveNoInteractions();
            }

            @Test
            void shouldNotFindSolidarityUser() {
                then(userService).shouldHaveNoInteractions();
            }

            @Test
            void shouldSolidarityLeaderUserDtoNull() {
                then(stockDetailsResponseConverter).should().convert(detailsResponseDtoArgumentCaptor.capture());
                final StockDetailsResponseDto stockDetailsResponseDto = detailsResponseDtoArgumentCaptor.getValue();

                assertThat(stockDetailsResponseDto.leader(), nullValue());
            }
        }

        @Nested
        class WhenSolidarityLeaderExists extends DefaultTestCases {
            @Mock
            private User acceptUser;

            @BeforeEach
            void setUp() {
                given(solidarity.getSolidarityLeader()).willReturn(solidarityLeader);
                given(stockDetailsResponseConverter.convert(
                    any(StockDetailsResponseDto.class)
                )).willReturn(expectedResponse);

                actualResponse = service.getStockDetails(stockCode);
            }

            @Test
            void shouldNotGetSolidarityLeaderApplicantUsers() {
                then(solidarityLeaderApplicantService).shouldHaveNoInteractions();
            }

            @Test
            void shouldFindSolidarityUser() {
                then(userService).should().findUser(solidarityLeaderUserId);
            }

            @Test
            void shouldHaveSolidarityLeaderUserDto() {
                then(stockDetailsResponseConverter).should().convert(detailsResponseDtoArgumentCaptor.capture());
                final StockDetailsResponseDto stockDetailsResponseDto = detailsResponseDtoArgumentCaptor.getValue();
                final SolidarityLeaderUserDto leader = stockDetailsResponseDto.leader();

                assertThat(leader, notNullValue());
                assertThat(leader.solidarityLeader(), is(solidarityLeader));
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldReturnExpectedResponse() {
                assertThat(actualResponse, is(expectedResponse));
            }

            @Test
            void shouldGetDashboard() {
                then(solidarityDashboardService).should().getDashboard(stockCode);
            }
        }
    }
}
