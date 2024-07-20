package ag.act.api.stockhome;

import ag.act.entity.User;
import ag.act.enums.StockNotice;
import ag.act.model.StockHomeResponse;
import ag.act.model.StockNoticeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetStockHomeNoticesApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    @Nested
    class WhenBlockSolidarityLeader {

        @BeforeEach
        void setUp() {
            final User blockedSolidarityLeader = itUtil.createUser();
            itUtil.createSolidarityLeader(solidarity, blockedSolidarityLeader.getId());

            itUtil.createBlockedUser(currentUser.getId(), blockedSolidarityLeader.getId());
        }

        @Test
        void shouldReturnNotice() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());

            assertResponse(getResponse(mvcResult));
        }

        private void assertResponse(StockHomeResponse stockHomeResponse) {
            List<StockNoticeResponse> result = stockHomeResponse.getNotices();

            assertThat(result.size(), is(1));
            assertThat(result.get(0).getNoticeLevel(), is(StockNotice.BLOCK_SOLIDARITY_LEADER.getNoticeLevel()));
            assertThat(result.get(0).getMessage(), is(StockNotice.BLOCK_SOLIDARITY_LEADER.getMessage()));
        }
    }
}
