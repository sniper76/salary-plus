package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.SlackChannel;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

class CreateSolidarityLeaderApplicantIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/solidarity-leaders/{solidarityId}";
    private static final String successMessage = "[%s] 주주연대 대표(%s)가 %s에 의해 선정되었습니다.";

    private String jwt;
    private Long solidarityId;
    private Stock stock;
    private User user;
    private ag.act.model.CreateSolidarityLeaderRequest request;
    private User adminLoginUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminLoginUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminLoginUser.getId());
    }

    @Nested
    class WhenElectSolidarityLeader {

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            solidarityId = solidarity.getId();
            user = itUtil.createUser();
            SolidarityLeaderApplicant solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(solidarityId, user.getId());
            request = genRequest(solidarityLeaderApplicant.getId());
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
    class WhenNotFoundSolidarityLeaderApplicant {

        @BeforeEach
        void setUp() {
            solidarityId = someLong();
            request = genRequest(someLong());
        }

        @Test
        void shouldReturnNotFoundException() throws Exception {
            final MvcResult mvcResult = callApi(status().isNotFound());

            itUtil.assertErrorResponse(mvcResult, 404, "해당 종목에 대한 지원 내역이 없습니다.");
        }

    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, solidarityId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
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

    private ag.act.model.CreateSolidarityLeaderRequest genRequest(Long solidarityLeaderApplicantId) {
        return new ag.act.model.CreateSolidarityLeaderRequest()
            .solidarityApplicantId(solidarityLeaderApplicantId);
    }
}
