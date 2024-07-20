package ag.act.facade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.facade.stock.home.StockHomeNoticeFacade;
import ag.act.model.StockNoticeResponse;
import ag.act.service.stock.StockService;
import ag.act.service.stock.home.notice.StockNoticeService;
import ag.act.service.stock.home.notice.StockSolidarityLeaderBlockedNoticeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someStockCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

@MockitoSettings
class StockHomeNoticeFacadeTest {

    @InjectMocks
    private StockHomeNoticeFacade facade;
    @Mock
    private List<StockNoticeService> stockNoticeServiceList;
    @Mock
    private StockSolidarityLeaderBlockedNoticeService stockSolidarityLeaderBlockedNoticeService;
    @Mock
    private StockService stockService;
    @Mock
    private User user;
    @Mock
    private Stock stock;
    @Mock
    private StockNoticeResponse stockNoticeResponse;

    private MockedStatic<ActUserProvider> actUserProviderMockedStatic;

    @BeforeEach
    void setUp() {
        actUserProviderMockedStatic = mockStatic(ActUserProvider.class);
        String stockCode = someStockCode();
        given(stockNoticeServiceList.stream()).willReturn(Stream.of(stockSolidarityLeaderBlockedNoticeService));
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(stockService.getStock(stockCode)).willReturn(stock);
        given(stockSolidarityLeaderBlockedNoticeService.getNotice(stock, user))
            .willReturn(List.of(stockNoticeResponse));

        facade.getNotices(stockCode);
    }

    @AfterEach
    void clean() {
        actUserProviderMockedStatic.close();
    }

    @Test
    void shouldCallGetNotices() {
        then(stockSolidarityLeaderBlockedNoticeService).should().getNotice(stock, user);
    }
}