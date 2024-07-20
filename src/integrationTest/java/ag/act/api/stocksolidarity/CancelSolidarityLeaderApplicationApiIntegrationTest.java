package ag.act.api.stocksolidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.SimpleStringResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

class CancelSolidarityLeaderApplicationApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity/apply-leader";
    private String jwt;
    private Stock stock;
    private Solidarity solidarity;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        itUtil.createBoard(stock);
        solidarity = itUtil.createSolidarity(stock.getCode());
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stock.getCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class Success {

        private void assertResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
            final SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
        }

        @Nested
        class WhenUserHasStock {
            @BeforeEach
            void setUp() {
                itUtil.createUserHoldingStock(stock.getCode(), user);
                itUtil.createSolidarityLeaderApplicant(solidarity.getId(), user.getId());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                assertResponse(response);
            }
        }

        @Nested
        class WhenUserHasNoStock {
            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeaderApplicant(solidarity.getId(), user.getId());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                assertResponse(response);
            }
        }
    }

    @Nested
    class WhenSolidarityLeaderAlreadyNotExists {
        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stock.getCode(), user);
        }

        @DisplayName("Should return 404 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = callApi(status().isNotFound());

            itUtil.assertErrorResponse(response, 404, "해당 종목에 대한 지원 내역이 없습니다.");
        }
    }
}
