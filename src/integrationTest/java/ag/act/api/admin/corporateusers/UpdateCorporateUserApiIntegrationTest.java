package ag.act.api.admin.corporateusers;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.model.CorporateUserDataResponse;
import ag.act.model.CorporateUserDetailsResponse;
import ag.act.model.CorporateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someCorporateNo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdateCorporateUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/corporate-users/{corporateId}";
    private String adminJwt;
    private CorporateUserRequest request;
    private Long corporateId;

    @BeforeEach
    void setUp() {
        User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
    }

    private MvcResult callApi(ResultMatcher resultMatcher, CorporateUserRequest request) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, corporateId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private CorporateUserRequest genRequest(String no, String name) {
        return new CorporateUserRequest()
            .corporateNo(no)
            .corporateName(name);
    }

    @Nested
    class WhenSuccess {

        @BeforeEach
        void setUp() {
            request = genRequest(someCorporateNo(), someString(10));
            final User user = itUtil.createUser();
            final CorporateUser corporateUser = itUtil.createCorporateUser(someCorporateNo(), someString(10));
            corporateUser.setUserId(user.getId());
            itUtil.updateCorporateUser(corporateUser);
            corporateId = corporateUser.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final CorporateUserDataResponse result = itUtil.getResult(response, CorporateUserDataResponse.class);

            assertResponse(result.getData());
        }

        private void assertResponse(CorporateUserDetailsResponse result) {
            assertThat(result.getCorporateNo(), is(request.getCorporateNo()));
            assertThat(result.getCorporateName(), is(request.getCorporateName()));
        }
    }

    @Nested
    class WhenAlreadyExistCorporateNo {

        @BeforeEach
        void setUp() {
            final String corporateNo = someCorporateNo();
            final CorporateUser corporateUser = itUtil.createCorporateUser(someCorporateNo(), someString(10));
            itUtil.createCorporateUser(corporateNo, someString(10));
            request = genRequest(corporateNo, someString(10));
            corporateId = corporateUser.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isBadRequest(), request);

            itUtil.assertErrorResponse(response, 400, "이미 존재하는 법인사업자입니다.");
        }
    }
}
