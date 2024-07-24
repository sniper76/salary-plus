package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.SlackChannel;
import ag.act.model.CreateSolidarityLeaderForCorporateUserRequest;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreateSolidarityLeaderCorporateUserIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/solidarity-leaders/{solidarityId}/corporate-user";
    private static final String successMessage = "[%s] 주주연대 대표(%s)가 %s에 의해 선정되었습니다.";

    private String jwt;
    private Long solidarityId;
    private Stock stock;
    private Solidarity solidarity;
    private CreateSolidarityLeaderForCorporateUserRequest request;
    private User adminLoginUser;

    @BeforeEach
    void setUp() {
        adminLoginUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminLoginUser.getId());
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, solidarityId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private SimpleStringResponse getDataResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private CreateSolidarityLeaderForCorporateUserRequest genRequest(Long corporateUserId) {
        return new CreateSolidarityLeaderForCorporateUserRequest()
            .userId(corporateUserId);
    }

    @Nested
    class WhenElectSolidarityLeader {
        private User user;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            solidarity = itUtil.createSolidarity(stock.getCode());
            solidarityId = solidarity.getId();
            user = itUtil.createUser();
            final CorporateUser corporateUser = itUtil.createCorporateUser("123456-1234567", someString(10));
            corporateUser.setUserId(user.getId());
            itUtil.updateCorporateUser(corporateUser);
            request = genRequest(corporateUser.getUserId());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            SimpleStringResponse result = getDataResponse(callApi(status().isOk()));

            assertThat(result.getStatus(), is("ok"));
            then(slackMessageSender).should().sendSlackMessage(
                successMessage.formatted(stock.getName(), user.getName(), adminLoginUser.getName()),
                SlackChannel.ACT_SOLIDARITY_LEADER_ALERT
            );
        }
    }

    @Nested
    class WhenAlreadyExistLeader {

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            solidarity = itUtil.createSolidarity(stock.getCode());
            solidarityId = solidarity.getId();
            final User user = itUtil.createUser();
            itUtil.createSolidarityLeader(solidarity, user.getId());
            request = genRequest(user.getId());
        }

        @Test
        void shouldReturnNotFoundException() throws Exception {
            final MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, 400, "이미 주주대표가 선정되어 있는 연대입니다.");
        }

    }
}
