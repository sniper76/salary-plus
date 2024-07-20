package ag.act.service;

import ag.act.converter.dashboard.DashboardResponseConverter;
import ag.act.dto.TopTwoMostRecentDailySummaryDto;
import ag.act.entity.SolidarityDailySummary;
import ag.act.service.solidarity.SolidarityDailySummaryService;
import ag.act.service.solidarity.SolidarityDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityDashboardServiceTest {
    @InjectMocks
    private SolidarityDashboardService service;
    @Mock
    private SolidarityDailySummaryService solidarityDailySummaryService;
    @Mock
    private DashboardResponseConverter dashboardResponseConverter;
    @Mock
    private List<ag.act.model.DashboardItemResponse> dashboardItemResponses;
    @Mock
    private ag.act.model.DashboardResponse dashboardResponse;
    @Mock
    private TopTwoMostRecentDailySummaryDto topTwoMostRecentDailySummaryDto;
    @Mock
    private SolidarityDailySummary mostRecentSolidarityDailySummary;
    @Mock
    private SolidarityDailySummary secondMostRecentSolidarityDailySummary;

    private String stockCode;

    @BeforeEach
    void setUp() {
        stockCode = someStockCode();
        given(dashboardResponseConverter.convert(dashboardItemResponses, mostRecentSolidarityDailySummary.getUpdatedAt()))
            .willReturn(dashboardResponse);
        given(solidarityDailySummaryService.getTopTwoMostRecentDailySummaries(stockCode))
            .willReturn(topTwoMostRecentDailySummaryDto);

    }

    @Nested
    class WhenGetDashboard {
        @Nested
        class AndRecentSolidarityDailySummariesAreEmpty {
            @Test
            void shouldGenerateZeroFilledDashboard() {
                given(solidarityDailySummaryService.generateZeroFilledDashboard())
                    .willReturn(dashboardItemResponses);
                given(dashboardResponseConverter.convert(eq(dashboardItemResponses), any(LocalDateTime.class)))
                    .willReturn(dashboardResponse);

                // When
                final ag.act.model.DashboardResponse actual = service.getDashboard(stockCode);

                // Then
                assertThat(actual, is(dashboardResponse));
                then(dashboardResponseConverter).should().convert(eq(dashboardItemResponses), any(LocalDateTime.class));
                then(solidarityDailySummaryService).should().generateZeroFilledDashboard();
            }
        }

        @Nested
        class AndSingleRecentSolidarityDailySummaryExists {
            @Test
            void shouldGenerateMostRecentFilledDashboard() {
                given(topTwoMostRecentDailySummaryDto.getMostRecentSummary())
                    .willReturn(mostRecentSolidarityDailySummary);
                given(solidarityDailySummaryService.generateDashboardItemsBySingleSolidarityDailySummary(mostRecentSolidarityDailySummary))
                    .willReturn(dashboardItemResponses);

                // When
                final ag.act.model.DashboardResponse actual = service.getDashboard(stockCode);

                // Then
                assertThat(actual, is(dashboardResponse));
                then(solidarityDailySummaryService).should()
                    .generateDashboardItemsBySingleSolidarityDailySummary(mostRecentSolidarityDailySummary);
            }
        }

        @Nested
        class AndTwoRecentSolidarityDailySummariesExist {
            @Test
            void shouldGenerateSecondVariationFilledDashboard() {
                given(topTwoMostRecentDailySummaryDto.getMostRecentSummary())
                    .willReturn(mostRecentSolidarityDailySummary);
                given(topTwoMostRecentDailySummaryDto.getSecondMostRecentSummary())
                    .willReturn(secondMostRecentSolidarityDailySummary);
                given(solidarityDailySummaryService.generateDashboardItemsWithVariation(topTwoMostRecentDailySummaryDto))
                    .willReturn(dashboardItemResponses);

                // When
                final ag.act.model.DashboardResponse actual = service.getDashboard(stockCode);

                // Then
                assertThat(actual, is(dashboardResponse));
                then(solidarityDailySummaryService).should().generateDashboardItemsWithVariation(topTwoMostRecentDailySummaryDto);
            }
        }
    }
}
