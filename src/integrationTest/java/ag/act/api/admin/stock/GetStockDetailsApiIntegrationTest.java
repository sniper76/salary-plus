package ag.act.api.admin.stock;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.CorporateUser;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.DashboardItemResponse;
import ag.act.model.DashboardResponse;
import ag.act.model.DigitalDocumentAcceptUserResponse;
import ag.act.model.GetStockDetailsDataResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderResponse;
import ag.act.model.SolidarityResponse;
import ag.act.model.StockDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class GetStockDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{code}";

    private String jwt;
    private Stock stock;
    private String stockCode;
    private SolidarityDailySummary mostRecentDailySummary;
    private SolidarityDailySummary secondMostRecentDailySummary;
    private Solidarity solidarity;
    private User leaderUser;
    private SolidarityLeader solidarityLeader;

    private User applicantUser;
    private SolidarityLeaderApplicant solidarityLeaderApplicant;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        stock = mockStock();
        stockCode = stock.getCode();
        solidarity = itUtil.createSolidarity(stock.getCode());
    }

    private Stock mockStock() {
        Stock stock = itUtil.createStock();
        stock.setTotalIssuedQuantity(10000L);
        stock.setClosingPrice(100);
        return itUtil.updateStock(stock);
    }

    private void mockSolidarityDailySummary(
        Stock stock,
        Solidarity solidarity,
        SolidarityDailySummary mostRecentDailySummary,
        SolidarityDailySummary secondMostRecentDailySummary
    ) {
        mostRecentDailySummary.setStake(15.6);
        mostRecentDailySummary.setStockQuantity(10_000_000L);
        mostRecentDailySummary.setMemberCount(100_000_000);
        mostRecentDailySummary.setMarketValue(15_000_000_000L);
        solidarity.setMostRecentDailySummary(mostRecentDailySummary);

        secondMostRecentDailySummary.setStake(mostRecentDailySummary.getStake() / 2);
        secondMostRecentDailySummary.setStockQuantity(mostRecentDailySummary.getStockQuantity() / 2);
        secondMostRecentDailySummary.setMemberCount(mostRecentDailySummary.getMemberCount() / 2);
        secondMostRecentDailySummary.setMarketValue(mostRecentDailySummary.getMarketValue() / 2);
        solidarity.setSecondMostRecentDailySummary(secondMostRecentDailySummary);

        stock.setSolidarity(solidarity);
        stock.setSolidarityId(solidarity.getId());
        itUtil.updateSolidarity(solidarity);
    }

    @Nested
    class WhenGetStockDetails {

        @Nested
        class AndSolidarityLeaderExists {

            @BeforeEach
            void setUp() {
                leaderUser = itUtil.createUser();
                solidarityLeader = itUtil.createSolidarityLeader(solidarity, leaderUser.getId());
                solidarityLeader.setMessage(someAlphanumericString(10));
                solidarityLeader = itUtil.updateSolidarityLeader(solidarityLeader);
            }

            @Nested
            class AndDailySummaryNotExists {

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnStockDetails() throws Exception {
                    GetStockDetailsDataResponse result = callApiAndGetResult();
                    final StockDetailsResponse detailsResponse = result.getData();

                    assertSolidarity(detailsResponse.getSolidarity());
                    assertSolidarityLeader(detailsResponse.getSolidarityLeader());
                    assertTodayDeltaWithoutDailySummary(detailsResponse.getTodayDelta());

                    assertThat(detailsResponse.getSolidarityLeaderApplicants(), is(List.of()));
                    assertThat(detailsResponse.getAcceptUser(), nullValue());
                }
            }

            @Nested
            class AndDailySummaryExists {

                @BeforeEach
                void setUp() {
                    mostRecentDailySummary = itUtil.createSolidarityDailySummary();
                    secondMostRecentDailySummary = itUtil.createSolidarityDailySummary();
                    mockSolidarityDailySummary(stock, solidarity, mostRecentDailySummary, secondMostRecentDailySummary);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnStockDetails() throws Exception {
                    GetStockDetailsDataResponse result = callApiAndGetResult();
                    final StockDetailsResponse detailsResponse = result.getData();

                    assertSolidarity(detailsResponse.getSolidarity());
                    assertSolidarityLeader(detailsResponse.getSolidarityLeader());
                    assertTodayDeltaWithDailySummary(detailsResponse.getTodayDelta());

                    assertThat(detailsResponse.getSolidarityLeaderApplicants(), is(List.of()));
                    assertThat(detailsResponse.getAcceptUser(), nullValue());
                }
            }

            @Nested
            class AndIsCorporateUser {
                private String corporateNo;

                @BeforeEach
                void setUp() {
                    mostRecentDailySummary = itUtil.createSolidarityDailySummary();
                    secondMostRecentDailySummary = itUtil.createSolidarityDailySummary();
                    mockSolidarityDailySummary(stock, solidarity, mostRecentDailySummary, secondMostRecentDailySummary);

                    corporateNo = someString(10);
                    final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, someString(10));
                    corporateUser.setUserId(leaderUser.getId());
                    itUtil.updateCorporateUser(corporateUser);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnStockDetails() throws Exception {
                    GetStockDetailsDataResponse result = callApiAndGetResult();
                    final StockDetailsResponse detailsResponse = result.getData();

                    assertSolidarity(detailsResponse.getSolidarity());
                    assertSolidarityLeader(detailsResponse.getSolidarityLeader());
                    assertTodayDeltaWithDailySummary(detailsResponse.getTodayDelta());

                    assertThat(detailsResponse.getSolidarityLeader().getCorporateNo(), is(corporateNo));
                    assertThat(detailsResponse.getAcceptUser(), nullValue());
                }
            }
        }

        @Nested
        class AndSolidarityLeaderNotExists {

            @Nested
            class AndDailySummaryExists {

                @BeforeEach
                void setUp() {
                    mostRecentDailySummary = itUtil.createSolidarityDailySummary();
                    secondMostRecentDailySummary = itUtil.createSolidarityDailySummary();
                    mockSolidarityDailySummary(stock, solidarity, mostRecentDailySummary, secondMostRecentDailySummary);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnStockDetails() throws Exception {
                    GetStockDetailsDataResponse result = callApiAndGetResult();
                    final StockDetailsResponse detailsResponse = result.getData();

                    assertSolidarity(detailsResponse.getSolidarity());
                    assertTodayDeltaWithDailySummary(detailsResponse.getTodayDelta());

                    assertThat(detailsResponse.getSolidarityLeader(), nullValue());
                    assertThat(detailsResponse.getSolidarityLeaderApplicants(), is(List.of()));
                    assertThat(detailsResponse.getAcceptUser(), nullValue());
                }
            }

            @Nested
            @DisplayName("현재 시작되어 진행중인 선출이 있는 경우")
            class AndActiveSolidarityLeaderElectionExists {

                @BeforeEach
                void setUp() {
                    applicantUser = itUtil.createUser();
                    SolidarityLeaderElectionStatusGroup onGoingStatusGroup = someThing(
                        SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                        SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP
                    );
                    SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stockCode, onGoingStatusGroup);
                    solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        applicantUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityElection.getId()
                    );

                    // not included data
                    itUtil.createSolidarityLeaderApplicant(solidarity.getId(), someLong());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnStockDetails() throws Exception {
                    GetStockDetailsDataResponse result = callApiAndGetResult();
                    final StockDetailsResponse detailsResponse = result.getData();

                    assertSolidarity(detailsResponse.getSolidarity());
                    assertTodayDeltaWithoutDailySummary(detailsResponse.getTodayDelta());
                    assertSolidarityLeaderApplicants(detailsResponse.getSolidarityLeaderApplicants());

                    assertThat(detailsResponse.getSolidarityLeader(), nullValue());
                    assertThat(detailsResponse.getAcceptUser(), nullValue());
                }
            }

            @Nested
            @DisplayName("진행중인 선출이 없는 경우")
            class WhenActiveSolidarityLeaderElectionNotExists {

                @BeforeEach
                void setUp() {
                    applicantUser = itUtil.createUser();
                    SolidarityLeaderElectionStatusGroup finishedStatusGroup = someThing(
                        SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP,
                        SolidarityLeaderElectionStatusGroup.FINISHED_EARLY_STATUS_GROUP,
                        SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_WITH_NO_WINNER_STATUS_GROUP,
                        SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP
                    );
                    SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stockCode, finishedStatusGroup);
                    solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(
                        solidarity.getId(),
                        applicantUser.getId(),
                        SolidarityLeaderElectionApplyStatus.COMPLETE,
                        solidarityElection.getId()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnStockDetails() throws Exception {
                    GetStockDetailsDataResponse result = callApiAndGetResult();
                    final StockDetailsResponse detailsResponse = result.getData();

                    assertSolidarity(detailsResponse.getSolidarity());
                    assertTodayDeltaWithoutDailySummary(detailsResponse.getTodayDelta());
                    assertThat(detailsResponse.getSolidarityLeaderApplicants().size(), is(0));

                    assertThat(detailsResponse.getSolidarityLeader(), nullValue());
                    assertThat(detailsResponse.getAcceptUser(), nullValue());
                }
            }
        }

        @Nested
        class AndAcceptorUserExists {
            private User acceptorUser;

            @BeforeEach
            void setUp() {
                acceptorUser = itUtil.createUser();
                leaderUser = itUtil.createUser();
                solidarityLeader = itUtil.createSolidarityLeader(solidarity, leaderUser.getId());
                solidarityLeader.setMessage(someAlphanumericString(10));
                solidarityLeader = itUtil.updateSolidarityLeader(solidarityLeader);

                mostRecentDailySummary = itUtil.createSolidarityDailySummary();
                secondMostRecentDailySummary = itUtil.createSolidarityDailySummary();
                mockSolidarityDailySummary(stock, solidarity, mostRecentDailySummary, secondMostRecentDailySummary);

                itUtil.createStockAcceptorUser(solidarity.getStockCode(), acceptorUser);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStockDetails() throws Exception {
                GetStockDetailsDataResponse result = callApiAndGetResult();
                final StockDetailsResponse detailsResponse = result.getData();

                assertSolidarity(detailsResponse.getSolidarity());
                assertSolidarityLeader(detailsResponse.getSolidarityLeader());
                assertTodayDeltaWithDailySummary(detailsResponse.getTodayDelta());

                final DigitalDocumentAcceptUserResponse responseAcceptUser = detailsResponse.getAcceptUser();
                assertThat(responseAcceptUser, notNullValue());
                assertThat(responseAcceptUser.getId(), is(acceptorUser.getId()));
                assertThat(responseAcceptUser.getName(), is(acceptorUser.getName()));
                assertThat(responseAcceptUser.getCorporateNo(), nullValue());
            }
        }
    }

    private void assertSolidarityLeaderApplicants(List<SolidarityLeaderApplicantResponse> solidarityLeaderApplicants) {
        assertThat(solidarityLeaderApplicants.size(), is(1));
        assertSolidarityLeaderApplicant(solidarityLeaderApplicants.get(0), solidarityLeaderApplicant, applicantUser);
    }

    private void assertSolidarityLeaderApplicant(
        SolidarityLeaderApplicantResponse solidarityLeaderApplicantResponse,
        SolidarityLeaderApplicant solidarityLeaderApplicant,
        User applicantUser
    ) {
        assertThat(solidarityLeaderApplicantResponse.getSolidarityApplicantId(), is(solidarityLeaderApplicant.getId()));
        assertThat(solidarityLeaderApplicantResponse.getName(), is(applicantUser.getName()));
        assertThat(solidarityLeaderApplicantResponse.getNickname(), is(applicantUser.getNickname()));
        assertThat(solidarityLeaderApplicantResponse.getPhoneNumber(), is(itUtil.decrypt(applicantUser.getHashedPhoneNumber())));
    }

    private void assertTodayDeltaWithoutDailySummary(DashboardResponse todayDelta) {
        final List<DashboardItemResponse> items = todayDelta.getItems();
        assertThat(items.size(), is(4));
        assertDashboardItem(items.get(0), "주식수", "0주", "-", "#000000");
        assertDashboardItem(items.get(1), "지분율", "0.00%", "-", "#000000");
        assertDashboardItem(items.get(2), "시가액", "0.0억원", "-", "#000000");
        assertDashboardItem(items.get(3), "주주수", "0명", "-", "#000000");
    }

    private void assertTodayDeltaWithDailySummary(DashboardResponse todayDelta) {
        final List<DashboardItemResponse> items = todayDelta.getItems();
        assertThat(items.size(), is(4));
        assertDashboardItem(items.get(0), "주식수", "10,000,000주", "▲ 5,000,000주", "#FF0000");
        assertDashboardItem(items.get(1), "지분율", "15.60%", "▲ 7.80%", "#FF0000");
        assertDashboardItem(items.get(2), "시가액", "150.0억원", "▲ 75.0억원", "#FF0000");
        assertDashboardItem(items.get(3), "주주수", "100,000,000명", "▲ 50,000,000명", "#FF0000");
    }

    private void assertDashboardItem(
        DashboardItemResponse dashboardItem,
        String title,
        String value,
        String variationText,
        String variationColor
    ) {
        assertThat(dashboardItem.getTitle(), is(title));
        assertThat(dashboardItem.getValue(), is(value));
        assertThat(dashboardItem.getVariation().getText(), is(variationText));
        assertThat(dashboardItem.getVariation().getColor(), is(variationColor));
    }

    private void assertSolidarityLeader(SolidarityLeaderResponse actualSolidarityLeader) {
        assertThat(actualSolidarityLeader.getSolidarityId(), is(solidarity.getId()));
        assertThat(actualSolidarityLeader.getSolidarityLeaderId(), is(solidarityLeader.getId()));
        assertThat(actualSolidarityLeader.getMessage(), is(solidarityLeader.getMessage()));
        assertThat(actualSolidarityLeader.getUserId(), is(leaderUser.getId()));
        assertThat(actualSolidarityLeader.getName(), is(leaderUser.getName()));
        assertThat(DateTimeConverter.convert(actualSolidarityLeader.getBirthDate()), is(leaderUser.getBirthDate()));
        assertThat(actualSolidarityLeader.getEmail(), is(leaderUser.getEmail()));
        assertThat(actualSolidarityLeader.getStatus(), is(leaderUser.getStatus()));
        assertThat(actualSolidarityLeader.getNickname(), is(leaderUser.getNickname()));
        assertThat(actualSolidarityLeader.getPhoneNumber(), is(itUtil.decrypt(leaderUser.getHashedPhoneNumber())));
    }

    private void assertSolidarity(SolidarityResponse solidarityResponse) {
        assertThat(solidarityResponse.getCode(), is(stock.getCode()));
        assertThat(solidarityResponse.getName(), is(stock.getName()));
        assertThat(solidarityResponse.getId(), is(solidarity.getId()));

        if (mostRecentDailySummary != null) {
            assertThat(solidarityResponse.getStake(), is(mostRecentDailySummary.getStake().floatValue()));
            assertThat(solidarityResponse.getMemberCount(), is(mostRecentDailySummary.getMemberCount()));
        }
    }

    private GetStockDetailsDataResponse callApiAndGetResult() throws Exception {
        final ResultMatcher resultMatcher = status().isOk();
        final MvcResult response = callApi(resultMatcher);

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetStockDetailsDataResponse.class
        );
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
