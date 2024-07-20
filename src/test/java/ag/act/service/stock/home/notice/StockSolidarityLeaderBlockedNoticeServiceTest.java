package ag.act.service.stock.home.notice;

import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.StockNoticeResponse;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings
class StockSolidarityLeaderBlockedNoticeServiceTest {

    @InjectMocks
    private StockSolidarityLeaderBlockedNoticeService stockSolidarityLeaderBlockedNoticeService;
    @Mock
    private BlockedUserService blockedUserService;
    @Mock
    private User user;
    @Mock
    private Stock stock;
    @Mock
    private Solidarity solidarity;
    @Mock
    private SolidarityLeader solidarityLeaderUser;

    private Long userId = someLong();
    private Long solidarityLeaderId = someLong();

    @BeforeEach
    void setUp() {
        given(stock.getSolidarity()).willReturn(solidarity);
    }

    @Nested
    class WhenBlockSolidarityUser {

        @BeforeEach
        void setUp() {
            given(blockedUserService.isBlockedUser(userId, solidarityLeaderId)).willReturn(true);
            given(solidarity.getSolidarityLeader()).willReturn(solidarityLeaderUser);
            given(solidarityLeaderUser.getUserId()).willReturn(solidarityLeaderId);
            given(user.getId()).willReturn(userId);
        }

        @Test
        void shouldGetStockSolidarityBlockedNotice() {
            List<StockNoticeResponse> notice = stockSolidarityLeaderBlockedNoticeService.getNotice(stock, user);

            assertThat(notice.isEmpty(), is(false));
        }
    }

    @Nested
    class WhenNotBlockSolidarityUser {

        @BeforeEach
        void setUp() {
            given(blockedUserService.isBlockedUser(userId, solidarityLeaderId)).willReturn(false);
            given(solidarity.getSolidarityLeader()).willReturn(solidarityLeaderUser);
            given(solidarityLeaderUser.getUserId()).willReturn(solidarityLeaderId);
            given(user.getId()).willReturn(userId);
        }

        @Test
        void shouldNotReturnNotice() {
            List<StockNoticeResponse> notice = stockSolidarityLeaderBlockedNoticeService.getNotice(stock, user);
            assertThat(notice.isEmpty(), is(true));
        }
    }

    @Nested
    class WhenSolidarityLeaderNotExist {

        @BeforeEach
        void setUp() {
            given(solidarity.getSolidarityLeader()).willReturn(null);
        }

        @Test
        void shouldNotReturnNotice() {
            List<StockNoticeResponse> notice = stockSolidarityLeaderBlockedNoticeService.getNotice(stock, user);
            assertThat(notice.isEmpty(), is(true));
        }
    }
}