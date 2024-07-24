package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.admin.StockRanking;
import ag.act.model.MySolidarityDataArrayResponse;
import ag.act.model.MySolidarityResponse;
import ag.act.model.Status;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

public class GetMySolidarityPageableApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/home/my-solidarity";
    private String jwt;
    private User user;
    private List<Result> resultList;
    private final long totalElements = 3L;
    private final int pageSize = 10;
    private final String sorts = "createdAt:ASC";

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        mockStocks();
    }

    private void mockStocks() {
        resultList = new ArrayList<>();
        final LocalDate yesterdayLocalDate = KoreanDateTimeUtil.getYesterdayLocalDate();
        for (int i = 0; i < totalElements; i++) {
            final String stockName = "stock" + i;
            final String stockCode = someStockCode();
            final Stock stock = itUtil.createStock(stockCode, stockName);
            final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            final UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
            userHoldingStock.setCreatedAt(userHoldingStock.getCreatedAt().plusSeconds(i));
            userHoldingStock.setDisplayOrder(i + 1);
            itUtil.updateUserHoldingStock(userHoldingStock);

            itUtil.createSolidarityLeader(solidarity, user.getId());
            final SolidarityDailySummary solidarityDailySummary = itUtil.createSolidarityDailySummary();

            solidarity.setMostRecentDailySummary(solidarityDailySummary);
            itUtil.updateSolidarity(solidarity);

            final StockRanking stockRanking = itUtil.createStockRanking(stock.getCode(), yesterdayLocalDate, 1 + i);

            resultList.add(
                new Result(i, stock, solidarity, solidarityDailySummary, stockRanking)
            );
        }
    }

    @Nested
    class WhenSuccess {
        private Map<String, Object> params;

        @BeforeEach
        void setUp() {
            params = Map.of(
                "page", 0,
                "size", pageSize,
                "sorts", sorts
            );
        }

        @Test
        void shouldReturnResponse() throws Exception {
            final MvcResult response = callApi(jwt, params, status().isOk());

            final MySolidarityDataArrayResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                MySolidarityDataArrayResponse.class
            );

            assertResponse(result);
        }
    }

    @Nested
    class WhenError {
        private Map<String, Object> params;

        @BeforeEach
        void setUp() {
            params = Map.of(
                "page", 0,
                "size", pageSize,
                "sorts", sorts
            );

            jwt = someNumericString(10);
        }

        @Test
        void shouldReturnResponse() throws Exception {
            final MvcResult response = callApi(jwt, params, status().isUnauthorized());

            itUtil.assertErrorResponse(response, 401, "로그인 후에 이용해주세요.");
        }
    }

    private MvcResult callApi(String paramJwt, Map<String, Object> params, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(paramJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(MySolidarityDataArrayResponse result) {
        final List<MySolidarityResponse> responseList = result.getData();
        final ag.act.model.Paging paging = result.getPaging();
        assertPaging(paging);

        assertDataResponse(responseList.get(0), getResult(0));
        assertDataResponse(responseList.get(1), getResult(1));
        assertDataResponse(responseList.get(2), getResult(2));
    }

    private record Result(
        int index,
        Stock stock,
        Solidarity solidarity,
        SolidarityDailySummary solidarityDailySummary,
        StockRanking stockRanking
    ) {
    }

    private Result getResult(int index) {
        return resultList
            .stream()
            .filter(result -> result.index() == index)
            .findFirst()
            .orElseThrow();
    }

    private void assertDataResponse(ag.act.model.MySolidarityResponse response, Result result) {
        assertThat(response.getStatus(), is(Status.ACTIVE));
        assertThat(response.getCode(), is(result.stock().getCode()));
        assertThat(response.getName(), is(result.stock().getName()));
        assertThat(response.getMemberCount(), is(result.solidarityDailySummary().getMemberCount()));
        assertThat(response.getStake(), is(result.solidarityDailySummary().getStake().floatValue()));
        assertThat(response.getStakeRank(), is(String.valueOf(result.stockRanking().getStakeRank())));
        assertThat(response.getStakeRankDelta(), is(result.stockRanking().getStakeRankDelta()));
        assertThat(response.getMarketValueRank(), is(String.valueOf(result.stockRanking().getMarketValueRank())));
        assertThat(response.getMarketValueRankDelta(), is(result.stockRanking().getMarketValueRankDelta()));
    }

    private void assertPaging(ag.act.model.Paging paging) {
        assertThat(paging.getPage(), is(1));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (pageSize * 1.0))));
        assertThat(paging.getSize(), is(pageSize));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(sorts));
    }
}
