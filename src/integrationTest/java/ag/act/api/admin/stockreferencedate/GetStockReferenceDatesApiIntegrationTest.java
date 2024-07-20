package ag.act.api.admin.stockreferencedate;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.StockReferenceDateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static ag.act.TestUtil.someLocalDateTime;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetStockReferenceDatesApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/reference-dates";

    private String jwt;
    private String stockCode;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        itUtil.createStock(stockCode);
    }

    @Nested
    class WhenGetStockReferenceDates {
        private LocalDate firstReferenceDate;
        private LocalDate secondReferenceDate;
        private Long firstReferenceDateId;
        private Long secondReferenceDateId;

        @BeforeEach
        void setUp() {
            firstReferenceDate = LocalDate.now().minusDays(10);
            secondReferenceDate = LocalDate.now().minusDays(20);

            firstReferenceDateId = itUtil.createStockReferenceDate(stockCode, firstReferenceDate).getId();
            secondReferenceDateId = itUtil.createStockReferenceDate(stockCode, secondReferenceDate).getId();
            itUtil.createStockReferenceDate(someStockCode(), someLocalDateTime().toLocalDate());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, stockCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.StockReferenceDateDataArrayResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.StockReferenceDateDataArrayResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.StockReferenceDateDataArrayResponse result) {
            final List<ag.act.model.StockReferenceDateResponse> stockReferenceDates = result.getData();

            assertThat(stockReferenceDates.size(), is(2));
            assertStockReferenceDate(stockReferenceDates.get(0), firstReferenceDateId, firstReferenceDate);
            assertStockReferenceDate(stockReferenceDates.get(1), secondReferenceDateId, secondReferenceDate);
        }

        private void assertStockReferenceDate(StockReferenceDateResponse stockReferenceDate, long expectedId, LocalDate expectedDate) {
            assertThat(stockReferenceDate.getId(), is(expectedId));
            assertThat(stockReferenceDate.getReferenceDate(), is(expectedDate));
        }
    }
}
