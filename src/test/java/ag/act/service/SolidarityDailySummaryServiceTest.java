package ag.act.service;

import ag.act.converter.dashboard.DashboardItemResponseConverter;
import ag.act.dto.TopTwoMostRecentDailySummaryDto;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.dashboard.item.DashboardItem;
import ag.act.repository.SolidarityDailySummaryRepository;
import ag.act.service.solidarity.SolidarityDailySummaryService;
import ag.act.service.solidarity.SolidarityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityDailySummaryServiceTest {
    @InjectMocks
    private SolidarityDailySummaryService service;
    @Mock
    private SolidarityDailySummaryRepository solidarityDailySummaryRepository;
    @Mock
    private DashboardItemResponseConverter dashboardItemResponseConverter;
    @Mock
    private SolidarityService solidarityService;

    @Nested
    class WhenGetTopTwoMostRecentDailySummaries {
        @Mock
        SolidarityDailySummary mostRecentDailySummary;
        @Mock
        SolidarityDailySummary secondMostRecentDailySummary;
        private String stockCode;
        @Mock
        private Solidarity solidarity;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();

            given(solidarityDailySummaryRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void shouldReturnTopTwoMostRecentDailySummaries() {
            given(solidarityService.getSolidarityByStockCode(stockCode))
                .willReturn(solidarity);
            given(solidarity.getMostRecentDailySummary())
                .willReturn(mostRecentDailySummary);
            given(solidarity.getSecondMostRecentDailySummary())
                .willReturn(secondMostRecentDailySummary);

            // When
            final TopTwoMostRecentDailySummaryDto actual = service.getTopTwoMostRecentDailySummaries(stockCode);

            // Then
            assertThat(actual.getMostRecentSummary(), is(mostRecentDailySummary));
            assertThat(actual.getSecondMostRecentSummary(), is(secondMostRecentDailySummary));
        }
    }

    @Nested
    class WhenGenerateDashboardItems {
        @Test
        void shouldGenerateZeroFilledDashboard() {
            // When
            service.generateZeroFilledDashboard();

            // Then
            then(dashboardItemResponseConverter).should(times(4)).convert(any(DashboardItem.class));
        }

        @Test
        void shouldGenerateDashboardItemsBySingleSolidarityDailySummary() {
            // Given
            SolidarityDailySummary summary = generateSolidarityDailySummary();

            // When
            service.generateDashboardItemsBySingleSolidarityDailySummary(summary);

            // Then
            then(dashboardItemResponseConverter).should(times(4)).convert(any(DashboardItem.class));
        }

        @Test
        void shouldGenerateDashboardItemsWithVariation() {
            // Given
            final SolidarityDailySummary mostRecentDailySummary = generateSolidarityDailySummary();
            final SolidarityDailySummary secondMostRecentDailySummary = generateSolidarityDailySummary();
            final TopTwoMostRecentDailySummaryDto topTwoMostRecentDailySummaryDto = TopTwoMostRecentDailySummaryDto.builder()
                .mostRecentSummary(mostRecentDailySummary)
                .secondMostRecentSummary(secondMostRecentDailySummary)
                .build();

            // When
            service.generateDashboardItemsWithVariation(topTwoMostRecentDailySummaryDto);

            // Then
            then(dashboardItemResponseConverter).should(times(4)).convert(any(DashboardItem.class));
        }

        private SolidarityDailySummary generateSolidarityDailySummary() {
            SolidarityDailySummary summary = mock(SolidarityDailySummary.class);
            given(summary.getStockQuantity())
                .willReturn(someLong());
            given(summary.getStake())
                .willReturn(someLong().doubleValue());
            given(summary.getMarketValue())
                .willReturn(someLong());
            given(summary.getMemberCount())
                .willReturn(someLong().intValue());

            return summary;
        }
    }
}
