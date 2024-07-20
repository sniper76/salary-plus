package ag.act.facade;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.facade.stock.home.StockHomeFacade;
import ag.act.facade.stock.home.StockHomeNoticeFacade;
import ag.act.facade.stock.home.StockHomeSolidarityLeaderElectionFacade;
import ag.act.model.DashboardResponse;
import ag.act.model.LeaderElectionFeatureActiveConditionResponse;
import ag.act.model.SimpleStockResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.StockHomeLeaderResponse;
import ag.act.model.StockHomeResponse;
import ag.act.model.StockHomeSectionResponse;
import ag.act.model.StockNoticeResponse;
import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.service.post.UnreadPostService;
import ag.act.service.solidarity.SolidarityDashboardService;
import ag.act.service.stock.StockService;
import ag.act.service.stock.home.StockHomeSectionService;
import ag.act.service.stock.home.StockHomeSolidarityLeaderElectionFeatureActiveConditionService;
import ag.act.service.stock.home.StockHomeSolidarityLeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockHomeFacadeTest {
    @InjectMocks
    private StockHomeFacade facade;
    @Mock
    private StockHomeSectionService stockHomeSectionService;
    @Mock
    private StockHomeSolidarityLeaderService stockHomeSolidarityLeaderService;
    @Mock
    private SolidarityDashboardService solidarityDashboardService;
    @Mock
    private StockHomeNoticeFacade stockHomeNoticeFacade;
    @Mock
    private StockHomeSolidarityLeaderElectionFeatureActiveConditionService stockHomeSolidarityLeaderElectionFeatureActiveConditionService;
    @Mock
    private UnreadPostService unreadPostService;
    @Mock
    private StockService stockService;
    @Mock
    private SimpleStockResponseConverter simpleStockResponseConverter;
    @Mock
    private List<StockHomeSectionResponse> stockHomeSectionResponseList;
    @Mock
    private SimpleStock simpleStock;
    @Mock
    private SimpleStockResponse simpleStockResponse;
    @Mock
    private DashboardResponse dashboardResponse;
    @Mock
    private StockHomeLeaderResponse stockHomeLeaderResponse;
    @Mock
    private List<StockNoticeResponse> stockNoticeResponses;
    @Mock
    private LeaderElectionFeatureActiveConditionResponse leaderElectionFeatureActiveConditionResponse;
    @Mock
    private UnreadStockBoardGroupPostStatusResponse unreadStockBoardGroupPostStatusResponse;
    @Mock
    private StockHomeSolidarityLeaderElectionFacade stockHomeSolidarityLeaderElectionFacade;
    @Mock
    private SolidarityLeaderApplicationResponse solidarityLeaderApplicationResponse;

    @Nested
    class WhenGetStockHome {

        private String stockCode;
        private StockHomeResponse actual;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();

            given(stockService.getSimpleStock(stockCode)).willReturn(simpleStock);
            given(simpleStockResponseConverter.convert(simpleStock)).willReturn(simpleStockResponse);
            given(solidarityDashboardService.getDashboard(stockCode)).willReturn(dashboardResponse);
            given(stockHomeSolidarityLeaderService.getStockHomeLeader(stockCode)).willReturn(stockHomeLeaderResponse);
            given(stockHomeSectionService.getSections(stockCode)).willReturn(stockHomeSectionResponseList);
            given(stockHomeNoticeFacade.getNotices(stockCode)).willReturn(stockNoticeResponses);
            given(stockHomeSolidarityLeaderElectionFacade.getLeaderApplication(stockCode)).willReturn(solidarityLeaderApplicationResponse);
            given(stockHomeSolidarityLeaderElectionFeatureActiveConditionService.getLeaderElectionFeatureActiveCondition(stockCode))
                .willReturn(leaderElectionFeatureActiveConditionResponse);
            given(unreadPostService.getUnreadStockBoardGroupPostStatusResponse(stockCode))
                .willReturn(unreadStockBoardGroupPostStatusResponse);

            actual = facade.getStockHome(stockCode);
        }

        @Test
        void shouldReturnStockHomeResponse() {
            assertThat(actual.getClass(), is(StockHomeResponse.class));
        }

        @Test
        void shouldReturnStockHomeResponseWithStock() {
            assertThat(actual.getStock(), is(simpleStockResponse));
        }

        @Test
        void shouldReturnStockHomeResponseWithDashboard() {
            assertThat(actual.getDashboard(), is(dashboardResponse));
        }

        @Test
        void shouldReturnStockHomeResponseWithLeader() {
            assertThat(actual.getLeader(), is(stockHomeLeaderResponse));
        }

        @Test
        void shouldReturnStockHomeResponseWithSections() {
            assertThat(actual.getSections(), is(stockHomeSectionResponseList));
        }

        @Test
        void shouldReturnStockHomeResponseWithNotices() {
            assertThat(actual.getNotices(), is(stockNoticeResponses));
        }

        @Test
        void shouldReturnStockHomeResponseWithLeaderApplication() {
            assertThat(actual.getLeaderApplication(), is(solidarityLeaderApplicationResponse));
        }

        @Test
        void shouldReturnStockHomeResponseWithLeaderElectionFeatureActiveCondition() {
            assertThat(actual.getLeaderElectionFeatureActiveCondition(), is(leaderElectionFeatureActiveConditionResponse));
        }

        @Test
        void shouldReturnStockHomeResponseWithUnreadStockBoardGroupPostStatus() {
            assertThat(actual.getUnreadStockBoardGroupPostStatus(), is(unreadStockBoardGroupPostStatusResponse));
        }

        @Test
        void shouldGetSimpleStock() {
            then(stockService).should().getSimpleStock(stockCode);
        }

        @Test
        void shouldConvertSimpleStockToSimpleStockResponse() {
            then(simpleStockResponseConverter).should().convert(simpleStock);
        }

        @Test
        void shouldGetDashboard() {
            then(solidarityDashboardService).should().getDashboard(stockCode);
        }

        @Test
        void shouldGetStockHomeLeader() {
            then(stockHomeSolidarityLeaderService).should().getStockHomeLeader(stockCode);
        }

        @Test
        void shouldGetSections() {
            then(stockHomeSectionService).should().getSections(stockCode);
        }

        @Test
        void shouldGetNotices() {
            then(stockHomeNoticeFacade).should().getNotices(stockCode);
        }

        @Test
        void shouldGetLeaderApplication() {
            then(stockHomeSolidarityLeaderElectionFacade).should().getLeaderApplication(stockCode);
        }

        @Test
        void shouldGetLeaderElectionFeatureActiveCondition() {
            then(stockHomeSolidarityLeaderElectionFeatureActiveConditionService).should().getLeaderElectionFeatureActiveCondition(stockCode);
        }

        @Test
        void shouldGetStockHomeLeaderElectionDetail() {
            then(stockHomeSolidarityLeaderElectionFacade).should().getLeaderElectionDetail(stockCode);
        }
    }
}
