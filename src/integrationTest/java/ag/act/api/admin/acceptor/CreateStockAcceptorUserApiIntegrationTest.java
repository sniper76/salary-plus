package ag.act.api.admin.acceptor;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.CreateStockAcceptorUserRequest;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateStockAcceptorUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/acceptor-users";

    private Stock stock;
    private User user;
    private CreateStockAcceptorUserRequest request;
    private String adminJwt;

    @BeforeEach
    void setUp() {
        final User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        stock = itUtil.createStock();
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenCreateAcceptor {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            request = new CreateStockAcceptorUserRequest().userId(user.getId());
        }

        @DisplayName("Should return 200 when success")
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final SimpleStringResponse simpleStringResponse = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleStringResponse.class
            );

            assertThat(simpleStringResponse.getStatus(), is("ok"));

            final User databaseUser = itUtil.findUser(user.getId());
            assertThat(databaseUser.getRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getType() == RoleType.ACCEPTOR_USER), is(true));

            final List<StockAcceptorUserHistory> acceptorUserHistoryList = itUtil.getAllStockAcceptorUserHistory(
                stock.getCode(), user.getId()
            );
            assertThat(acceptorUserHistoryList.size(), is(1));
            for (final StockAcceptorUserHistory acceptorUserHistory : acceptorUserHistoryList) {
                assertThat(acceptorUserHistory.getUserId(), is(user.getId()));
                assertThat(acceptorUserHistory.getName(), is(user.getName()));
                assertThat(acceptorUserHistory.getBirthDate(), is(user.getBirthDate()));
                assertThat(acceptorUserHistory.getHashedPhoneNumber(), is(user.getHashedPhoneNumber()));
            }
        }
    }

    @Nested
    class WhenAlreadyExistSameAcceptor {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            itUtil.createUserRole(user, RoleType.ACCEPTOR_USER);
            itUtil.createStockAcceptorUser(stock.getCode(), user);
            request = new CreateStockAcceptorUserRequest().userId(user.getId());
        }

        @DisplayName("Should return 400 when already exists acceptor")
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "해당 종목에 이미 수임인으로 선정된 사용자입니다.");
        }
    }
}
