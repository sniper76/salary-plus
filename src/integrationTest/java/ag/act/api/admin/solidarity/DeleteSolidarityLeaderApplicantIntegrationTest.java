package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.DeleteSolidarityApplicantRequest;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

public class DeleteSolidarityLeaderApplicantIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/solidarity-leader-applicants/{solidarityId}";

    private String jwt;
    private Long solidarityId;
    private DeleteSolidarityApplicantRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class WhenDeleteSolidarityLeaderApplicant {

        @BeforeEach
        void setUp() {
            final Stock stock = itUtil.createStock();
            final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            solidarityId = solidarity.getId();

            final User user = itUtil.createUser();
            final SolidarityLeaderApplicant solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(solidarityId, user.getId());
            request = genRequest(solidarityLeaderApplicant.getId());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            ag.act.model.SimpleStringResponse result = getDataResponse(callApi(status().isOk()));

            assertThat(result.getStatus(), is("ok"));
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
                delete(TARGET_API, solidarityId)
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

    private DeleteSolidarityApplicantRequest genRequest(Long solidarityLeaderApplicantId) {
        return new DeleteSolidarityApplicantRequest()
            .solidarityLeaderApplicantId(solidarityLeaderApplicantId);
    }
}
