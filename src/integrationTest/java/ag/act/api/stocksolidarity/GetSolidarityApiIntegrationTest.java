package ag.act.api.stocksolidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

class GetSolidarityApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity";
    private String jwt;
    private Stock stock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        SolidarityDailySummary summary = itUtil.createSolidarityDailySummary();
        summary.setStake(0.33);
        summary.setMemberCount(40);
        itUtil.updateSolidarityDailySummary(summary);

        solidarity.setMostRecentDailySummary(summary);
        itUtil.updateSolidarity(solidarity);

        User anotherUser = itUtil.createUser(someNumericString(6));
        itUtil.createUserHoldingStock(stock.getCode(), anotherUser);
    }

    @Nested
    class WhenUserHasStock {
        @Test
        void shouldReturnSolidarityDataResponse() throws Exception {
            callAndResponse(jwt);
        }
    }

    @Nested
    class WhenUserHasNoStock {

        private String noStockJwt;

        @BeforeEach
        void setUp() {
            User user = itUtil.createUser();
            noStockJwt = itUtil.createJwt(user.getId());
        }

        @Test
        void shouldReturnSolidarityDataResponse() throws Exception {
            callAndResponse(noStockJwt);
        }
    }

    private void callAndResponse(String paramJwt) throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stock.getCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(paramJwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.SolidarityDataResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SolidarityDataResponse.class
        );

        assertResponse(result);
    }

    private void assertResponse(ag.act.model.SolidarityDataResponse result) {
        ag.act.model.SolidarityResponse response = result.getData();

        assertThat(response.getStatus(), is(Status.ACTIVE));
        assertThat(response.getName(), is(stock.getName()));
        assertThat(response.getCode(), is(stock.getCode()));
        assertThat(response.getMemberCount(), is(40));
        assertThat(response.getStake(), is(0.33f));
        assertThat(response.getRequiredMemberCount(), is(10));
        assertThat(response.getRepresentativePhoneNumber(), is(stock.getRepresentativePhoneNumber()));
    }
}
