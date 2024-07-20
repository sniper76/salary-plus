package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.enums.SlackChannel;
import ag.act.module.krx.KrxStockAggregator;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BatchExecutionExceptionIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/batch/update-stocks";
    private static final String errorMessage = "[UPDATE_STOCKS] failed due to [Batch] KRX 서버에서 데이터를 가져오는 중에 오류가 발생하였습니다.";
    private Map<String, Integer> request;

    @MockBean
    private KrxStockAggregator krxStockAggregator;

    @BeforeEach
    void setUp() {
        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);

        String date = DateTimeUtil.getLatestStockMarketClosingDate();
        given(krxStockAggregator.getStocksFromKrxService(date)).willThrow(new NullPointerException());
    }

    @DisplayName("Should throw batch execution exception when call " + TARGET_API)
    @Test
    void shouldThrowBatchExecutionException() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("x-api-key", "b0e6f688a1a08462201ef69f4")
            )
            .andExpect(status().isInternalServerError())
            .andReturn();

        assertThat(Objects.requireNonNull(response.getResolvedException()).getMessage(), is(errorMessage));
        then(slackMessageSender).should().sendSlackMessage(errorMessage, SlackChannel.ACT_BATCH_ALERT);
    }
}
