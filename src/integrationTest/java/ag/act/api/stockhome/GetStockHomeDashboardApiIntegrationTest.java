package ag.act.api.stockhome;

import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.model.DashboardItemResponse;
import ag.act.model.DashboardResponse;
import ag.act.model.SimpleStockResponse;
import ag.act.model.StockHomeResponse;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"AbbreviationAsWordInName"})
class GetStockHomeDashboardApiIntegrationTest extends GetStockHomeApiIntegrationTest {

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

    private void mockMostRecentSolidarityDailySummary(
        Solidarity toBeUpdatedSolidarity, Long stockQuantity, Integer memberCount, Double stake, Long marketValue
    ) {
        final SolidarityDailySummary summary = mockSolidarityDailySummary(stockQuantity, memberCount, stake, marketValue);
        itUtil.updateSolidarityDailySummary(summary);
        toBeUpdatedSolidarity.setMostRecentDailySummary(summary);
        itUtil.updateSolidarity(toBeUpdatedSolidarity);
    }

    private void mockSecondMostRecentSolidarityDailySummary(
        Solidarity toBeUpdatedSolidarity, Long stockQuantity, Integer memberCount, Double stake, Long marketValue
    ) {
        final SolidarityDailySummary summary = mockSolidarityDailySummary(stockQuantity, memberCount, stake, marketValue);
        itUtil.updateSolidarityDailySummary(summary);
        toBeUpdatedSolidarity.setSecondMostRecentDailySummary(summary);
        itUtil.updateSolidarity(toBeUpdatedSolidarity);
    }


    private SolidarityDailySummary mockSolidarityDailySummary(Long stockQuantity, Integer memberCount, Double stake, Long marketValue) {
        final SolidarityDailySummary summary = itUtil.createSolidarityDailySummary();
        summary.setStockQuantity(stockQuantity);
        summary.setMemberCount(memberCount);
        summary.setStake(stake);
        summary.setMarketValue(marketValue);

        return summary;
    }

    @Nested
    class WhenDailySummaryNotExists {
        private List<MockedStatic<?>> statics;

        @AfterEach
        void tearDown() {
            statics.forEach(MockedStatic::close);
        }

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(DateTimeUtil.class));
        }

        private void assertStockResponse(SimpleStockResponse stockResponse) {
            assertThat(stockResponse.getCode(), is(stock.getCode()));
            assertThat(stockResponse.getName(), is(stock.getName()));
            assertThat(stockResponse.getStandardCode(), is(stock.getStandardCode()));
        }

        private void assertDashboardResponse(StockHomeResponse result) {
            DashboardResponse dashboard = result.getDashboard();
            List<DashboardItemResponse> dashboardItems = dashboard.getItems();
            String descriptionLabel = dashboard.getDescriptionLabel();

            assertThat(descriptionLabel, is("최종 업데이트(전일대비): 2021-01-01 00:00"));

            assertThat(dashboardItems.size(), is(4));
            assertDashboardItem(dashboardItems.get(0), "주식수", "0주", "-", "#000000");
            assertDashboardItem(dashboardItems.get(1), "지분율", "0.00%", "-", "#000000");
            assertDashboardItem(dashboardItems.get(2), "시가액", "0.0억원", "-", "#000000");
            assertDashboardItem(dashboardItems.get(3), "주주수", "0명", "-", "#000000");
        }

        @Nested
        class WhenUserHasStock extends DefaultTestCase {
        }

        @Nested
        class WhenUserHasNoStock extends DefaultTestCase {
            @BeforeEach
            void setUp() {
                currentUser = itUtil.createUser();
                jwt = itUtil.createJwt(currentUser.getId());
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCase {
            @Test
            void shouldReturnSuccessWithZeroFilledDashBoard() throws Exception {
                // Given
                given(DateTimeUtil.getFormattedKoreanTime(eq("yyyy-MM-dd HH:mm"), any(Instant.class)))
                    .willReturn("2021-01-01 00:00");

                // When
                StockHomeResponse result = getResponse(callApi(status().isOk()));

                // Then
                assertDashboardResponse(result);
                assertStockResponse(result.getStock());
            }
        }
    }

    @Nested
    class WhenOnlyMostRecentDailySummaryExists {
        @Test
        void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

            // Given
            mockMostRecentSolidarityDailySummary(
                solidarity, 10987654321L, 5432, 0.235, 123456L
            );

            // When
            StockHomeResponse result = getResponse(callApi(status().isOk()));

            // Then
            DashboardResponse dashboard = result.getDashboard();
            String descriptionLabel = dashboard.getDescriptionLabel();
            List<DashboardItemResponse> dashboardItems = dashboard.getItems();

            assertThat(descriptionLabel, is(startsWith("최종 업데이트(전일대비): ")));

            assertThat(dashboardItems.size(), is(4));
            assertDashboardItem(dashboardItems.get(0), "주식수", "10,987,654,321주", "-", "#000000");
            assertDashboardItem(dashboardItems.get(1), "지분율", "0.24%", "-", "#000000");
            assertDashboardItem(dashboardItems.get(2), "시가액", "0.0억원", "-", "#000000");
            assertDashboardItem(dashboardItems.get(3), "주주수", "5,432명", "-", "#000000");
        }
    }

    @Nested
    class WhenTopTwoMostRecentDailySummaryExists {

        @Nested
        class AndNoVariation {
            @Test
            void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                // Given
                mockMostRecentSolidarityDailySummary(solidarity, 10987654321L, 5432, 0.235, 123456L);
                mockSecondMostRecentSolidarityDailySummary(solidarity, 10987654321L, 5432, 0.235, 123456L);

                // When
                StockHomeResponse result = getResponse(callApi(status().isOk()));

                // Then
                DashboardResponse dashboard = result.getDashboard();
                String descriptionLabel = dashboard.getDescriptionLabel();
                List<DashboardItemResponse> dashboardItems = dashboard.getItems();

                assertThat(descriptionLabel, is(startsWith("최종 업데이트(전일대비): ")));

                assertThat(dashboardItems.size(), is(4));
                assertDashboardItem(dashboardItems.get(0), "주식수", "10,987,654,321주", "-", "#000000");
                assertDashboardItem(dashboardItems.get(1), "지분율", "0.24%", "-", "#000000");
                assertDashboardItem(dashboardItems.get(2), "시가액", "0.0억원", "-", "#000000");
                assertDashboardItem(dashboardItems.get(3), "주주수", "5,432명", "-", "#000000");
            }
        }

        @Nested
        class AndPositiveVariation {
            private void mockSummariesForPositiveStakeVariation(double currentStake, double previousStake) {
                mockMostRecentSolidarityDailySummary(solidarity, 10987654321L, 5432, currentStake, 121110987654321L);
                mockSecondMostRecentSolidarityDailySummary(solidarity, 987654321L, 432, previousStake, 120000000000000L);
            }

            @Nested
            class Success {
                @Test
                void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                    // Given
                    mockMostRecentSolidarityDailySummary(solidarity, 10987654321L, 5432, 3.224, 121110987654321L);
                    mockSecondMostRecentSolidarityDailySummary(solidarity, 987654321L, 432, 1.219, 120000000000000L);


                    // When
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    // Then
                    DashboardResponse dashboard = result.getDashboard();
                    String descriptionLabel = dashboard.getDescriptionLabel();
                    List<DashboardItemResponse> dashboardItems = dashboard.getItems();

                    assertThat(descriptionLabel, is(startsWith("최종 업데이트(전일대비): ")));

                    assertThat(dashboardItems.size(), is(4));
                    assertDashboardItem(dashboardItems.get(0), "주식수", "10,987,654,321주", "▲ 10,000,000,000주", "#FF0000");
                    assertDashboardItem(dashboardItems.get(1), "지분율", "3.22%", "▲ 2.01%", "#FF0000");
                    assertDashboardItem(dashboardItems.get(2), "시가액", "1,211,109.9억원", "▲ 11,109.9억원", "#FF0000");
                    assertDashboardItem(dashboardItems.get(3), "주주수", "5,432명", "▲ 5,000명", "#FF0000");
                }
            }

            @Nested
            class AndStakeDifferenceBiggerThanTwoDecimalPlaces {
                @Test
                void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                    // Given
                    mockSummariesForPositiveStakeVariation(0.224f, 0.219f);

                    // When
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    // Then
                    List<DashboardItemResponse> dashboardItems = result.getDashboard().getItems();
                    assertDashboardItem(dashboardItems.get(1), "지분율", "0.22%", "▲ 0.01%", "#FF0000");
                }
            }

            @Nested
            class AndStakeDifferenceSmallerThanTwoDecimalPlaces {
                @Test
                void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                    // Given
                    mockSummariesForPositiveStakeVariation(0.222_224f, 0.222_219f);

                    // When
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    // Then
                    List<DashboardItemResponse> dashboardItems = result.getDashboard().getItems();
                    assertDashboardItem(dashboardItems.get(1), "지분율", "0.22%", "▲ 0.00%", "#FF0000");
                }
            }
        }

        @Nested
        class AndNegativeVariation {
            private void mockSummariesForNegativeStakeVariation(double currentStake, double previousStake) {
                mockMostRecentSolidarityDailySummary(solidarity, 987654321L, 432, currentStake, 120000000000000L);
                mockSecondMostRecentSolidarityDailySummary(solidarity, 10987654321L, 5432, previousStake, 121110987654321L);
            }

            @Nested
            class Success {

                @Test
                void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                    // Given
                    mockMostRecentSolidarityDailySummary(solidarity, 987654321L, 432, 1.219, 120_000_000_000_000L);
                    mockSecondMostRecentSolidarityDailySummary(solidarity, 10987654321L, 5432, 3.224, 121_110_987_654_321L);

                    // When
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    // Then
                    DashboardResponse dashboard = result.getDashboard();
                    String descriptionLabel = dashboard.getDescriptionLabel();
                    List<DashboardItemResponse> dashboardItems = dashboard.getItems();

                    assertThat(descriptionLabel, is(startsWith("최종 업데이트(전일대비): ")));

                    assertThat(dashboardItems.size(), is(4));
                    assertDashboardItem(dashboardItems.get(0), "주식수", "987,654,321주", "▼ 10,000,000,000주", "#355CE9");
                    assertDashboardItem(dashboardItems.get(1), "지분율", "1.22%", "▼ 2.01%", "#355CE9");
                    assertDashboardItem(dashboardItems.get(2), "시가액", "1,200,000.0억원", "▼ 11,109.9억원", "#355CE9");
                    assertDashboardItem(dashboardItems.get(3), "주주수", "432명", "▼ 5,000명", "#355CE9");
                }
            }

            @Nested
            class AndStakeDifferenceBiggerThanTwoDecimalPlaces {
                @Test
                void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                    // Given
                    mockSummariesForNegativeStakeVariation(0.219f, 0.224f);

                    // When
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    // Then
                    List<DashboardItemResponse> dashboardItems = result.getDashboard().getItems();
                    assertDashboardItem(dashboardItems.get(1), "지분율", "0.22%", "▼ 0.01%", "#355CE9");
                }
            }

            @Nested
            class AndStakeDifferenceSmallerThanTwoDecimalPlaces {
                @Test
                void shouldReturnSuccessWithMostRecentSummaryFilledDashBoard() throws Exception {

                    // Given
                    mockSummariesForNegativeStakeVariation(0.222_219f, 0.222_224f);

                    // When
                    StockHomeResponse result = getResponse(callApi(status().isOk()));

                    // Then
                    List<DashboardItemResponse> dashboardItems = result.getDashboard().getItems();
                    assertDashboardItem(dashboardItems.get(1), "지분율", "0.22%", "▼ 0.00%", "#355CE9");
                }
            }
        }
    }
}
