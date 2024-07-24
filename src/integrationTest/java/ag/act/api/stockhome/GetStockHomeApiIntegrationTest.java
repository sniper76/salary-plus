package ag.act.api.stockhome;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

abstract class GetStockHomeApiIntegrationTest extends AbstractCommonIntegrationTest {
    protected static final String TARGET_API = "/api/stocks/{stockCode}/home";
    protected User currentUser;
    protected String jwt;
    protected Stock stock;
    protected Solidarity solidarity;
    private List<MockedStatic<?>> statics;


    @AfterEach
    void tearDownCommon() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUpCommon() {
        statics = List.of(mockStatic(LeaderElectionCurrentDateTimeProvider.class));
        itUtil.init();

        currentUser = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(currentUser.getId());
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);

        given(LeaderElectionCurrentDateTimeProvider.get()).willReturn(DateTimeUtil.getTodayLocalDateTime());
        given(LeaderElectionCurrentDateTimeProvider.getKoreanDateTime())
            .willReturn(
                KoreanDateTimeUtil.getNowInKoreanTime().minusHours(KOREAN_TIME_OFFSET)
            );
    }

    protected MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode())
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(matcher)
            .andReturn();
    }

    protected ag.act.model.StockHomeResponse getResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.StockHomeResponse.class
        );
    }

}
