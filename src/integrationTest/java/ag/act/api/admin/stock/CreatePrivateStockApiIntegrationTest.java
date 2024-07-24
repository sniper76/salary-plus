package ag.act.api.admin.stock;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.PrivateStock;
import ag.act.entity.User;
import ag.act.model.CreatePrivateStockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreatePrivateStockApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/private-stocks";

    private User currentUser;
    private CreatePrivateStockRequest request;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
    }

    @Nested
    class WhenUserIsAdmin {

        @BeforeEach
        void setUp() {
            currentUser = itUtil.createAdminUser();
            jwt = itUtil.createJwt(currentUser.getId());
        }

        @Nested
        class WhenNormal {

            @BeforeEach
            void setUp() {
                request = genRequest();
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                itUtil.assertSimpleOkay(mvcResult);
            }
        }

        @Nested
        class WhenCodeDuplicated {

            private final String code = someStockCode().substring(0, 6);

            @BeforeEach
            void setUp() {
                PrivateStock privateStock = itUtil.createPrivateStock();
                privateStock.setCode(code);
                itUtil.updatePrivateStock(privateStock);
                request = genRequest().code(code);
            }

            @Test
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "Stock with code " + code + " already exists");
            }
        }

        @Nested
        class WhenCodeSizeInvalid {
            @BeforeEach
            void setUp() {
                request = genRequest().code(someNumericString(1, 5));
            }

            @Test
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "종목 코드는 6글자로 입력해주세요.");
            }
        }
    }

    @Nested
    class WhenUserIsNotAdmin {

        @BeforeEach
        void setUp() {
            currentUser = itUtil.createUser();
            jwt = itUtil.createJwt(currentUser.getId());
            request = genRequest();
        }

        @Test
        void shouldReturnError() throws Exception {
            MvcResult mvcResult = callApi(status().isForbidden());

            itUtil.assertErrorResponse(mvcResult, 403, "인가되지 않은 접근입니다.");
        }
    }

    private CreatePrivateStockRequest genRequest() {
        String code = someStockCode().substring(0, 6);
        return new CreatePrivateStockRequest()
            .code(code)
            .name(someString(5, 20))
            .stockType("보통주")
            .standardCode(code + someString(6))
            .closingPrice(somePositiveInteger())
            .totalIssuedQuantity(somePositiveInteger().longValue());
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            ).andExpect(resultMatcher)
            .andReturn();
    }
}
