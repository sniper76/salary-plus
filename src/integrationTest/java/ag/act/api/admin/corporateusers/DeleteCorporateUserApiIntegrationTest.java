package ag.act.api.admin.corporateusers;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someCorporateNo;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class DeleteCorporateUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/corporate-users/{corporateId}";
    private String adminJwt;
    private Long corporateId;

    @BeforeEach
    void setUp() {
        User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, corporateId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenSuccess {

        @BeforeEach
        void setUp() {
            final User user = itUtil.createUser();
            final CorporateUser corporateUser = itUtil.createCorporateUser(someCorporateNo(), someString(10));
            corporateUser.setUserId(user.getId());
            itUtil.updateCorporateUser(corporateUser);
            corporateId = corporateUser.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isOk());
            final SimpleStringResponse result = itUtil.getResult(response, SimpleStringResponse.class);

            assertResponse(result);
        }

        private void assertResponse(SimpleStringResponse result) {
            assertThat(result.getStatus(), is("ok"));

            final CorporateUser corporateUser = itUtil.findCorporateUser(corporateId).orElseThrow();
            assertThat(corporateUser.getStatus(), is(Status.DELETED_BY_ADMIN));

            final User user = itUtil.findUser(corporateUser.getUserId());
            assertThat(user.getStatus(), is(Status.DELETED_BY_ADMIN));
        }
    }

    @Nested
    class WhenNotFoundCorporateUser {
        @BeforeEach
        void setUp() {
            corporateId = somePositiveLong();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "법인 사업자 정보가 존재하지 않습니다.");
        }
    }

    @Nested
    class WhenCorporateUserIsAlreadySolidarityLeader {
        @BeforeEach
        void setUp() {
            final User user = itUtil.createUser();
            final CorporateUser corporateUser = itUtil.createCorporateUser(someCorporateNo(), someString(10));
            corporateUser.setUserId(user.getId());
            itUtil.updateCorporateUser(corporateUser);
            final String stockCode = someStockCode();
            itUtil.createStock(stockCode);
            itUtil.createSolidarityLeader(itUtil.createSolidarity(stockCode), user.getId());
            corporateId = corporateUser.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "이미 주주대표로 선정되어 있는 법인사업자입니다.");
        }
    }
}
