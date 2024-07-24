package ag.act.api.admin.stockreferencedate;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateStockReferenceDateApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/reference-dates";

    private String jwt;
    private Stock stock;
    private String stockCode;
    private ag.act.model.CreateStockReferenceDateRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
    }

    private ag.act.model.CreateStockReferenceDateRequest genRequest() {
        return new ag.act.model.CreateStockReferenceDateRequest()
            .referenceDate(KoreanDateTimeUtil.getTodayLocalDate());
    }

    @Nested
    class FailToCreateStockReferenceDate {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        private void callApiToFail(String expectedErrorMessage) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stockCode)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, expectedErrorMessage);
        }

        @Nested
        class WhenReferenceDateIsMissing {
            @BeforeEach
            void setUp() {
                request.setReferenceDate(null);
            }

            @Test
            void shouldReturnErrorResponse() throws Exception {
                callApiToFail("기준일을 확인해주세요.");
            }
        }

        @Nested
        class WhenSameReferenceDateAlreadyExists {
            @BeforeEach
            void setUp() {
                itUtil.createStockReferenceDate(stockCode, request.getReferenceDate());
            }

            @Test
            void shouldReturnErrorResponse() throws Exception {
                callApiToFail("해당종목에 이미 존재하는 기준일입니다.");
            }
        }
    }

    @Nested
    class WhenCreateStockReferenceDate {
        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stockCode)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.StockReferenceDateDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.StockReferenceDateDataResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.StockReferenceDateDataResponse result) {

            final ag.act.model.StockReferenceDateResponse stockReferenceDateResponse = result.getData();

            assertThat(stockReferenceDateResponse.getStockCode(), is(stock.getCode()));
            assertThat(stockReferenceDateResponse.getReferenceDate(), is(request.getReferenceDate()));
        }
    }
}
