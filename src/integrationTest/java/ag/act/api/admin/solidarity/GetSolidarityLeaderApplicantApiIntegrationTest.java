package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SolidarityLeaderApplicationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings("linelength")
class GetSolidarityLeaderApplicantApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId}";

    private Stock stock;
    private User currentUser;
    private String jwt;
    private SolidarityLeaderElection election;
    private SolidarityLeaderApplicant applicant;
    private User applyUser;

    @BeforeEach
    void setUp() {
        itUtil.init();

        stock = itUtil.createStock();
        Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        SolidarityLeaderElectionStatusGroup someSolidarityLeaderElectionStatusGroup = someThing(
            SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
            SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP,
            SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP,
            SolidarityLeaderElectionStatusGroup.FINISHED_EARLY_STATUS_GROUP,
            SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
            SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_WITH_NO_WINNER_STATUS_GROUP
        );

        election = itUtil.createSolidarityElection(
            stock.getCode(),
            someSolidarityLeaderElectionStatusGroup,
            TestUtil.someLocalDateTimeInThePast()
        );

        applyUser = itUtil.createUser();
        applicant = itUtil.createSolidarityLeaderApplicant(
            solidarity.getId(),
            applyUser.getId(),
            SolidarityLeaderElectionApplyStatus.COMPLETE,
            election.getId()
        );
    }

    @Nested
    class WhenUserIsAdmin {

        @BeforeEach
        void setUp() {
            currentUser = itUtil.createAdminUser();
            jwt = itUtil.createJwt(currentUser.getId());
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());
            SolidarityLeaderApplicationResponse response = objectMapperUtil.toResponse(
                mvcResult.getResponse().getContentAsString(),
                SolidarityLeaderApplicationResponse.class
            );

            assertResponse(response);
        }
    }

    @Nested
    class WhenUserIsNotAdmin {

        @BeforeEach
        void setUp() {
            currentUser = itUtil.createUser();
            jwt = itUtil.createJwt(currentUser.getId());
        }

        @Test
        void shouldReturnForbidden() throws Exception {
            callApi(status().isForbidden());
        }
    }

    private void assertResponse(SolidarityLeaderApplicationResponse response) {
        assertThat(response.getUser().getId(), is(applyUser.getId()));
        assertThat(response.getUser().getNickname(), is(applyUser.getNickname()));

        assertThat(response.getStock().getCode(), is(stock.getCode()));
        assertThat(response.getStock().getName(), is(stock.getName()));
        assertThat(response.getStock().getStandardCode(), is(stock.getStandardCode()));

        assertThat(response.getApplyStatus(), is(applicant.getApplyStatus().name()));
        assertThat(response.getSolidarityLeaderApplicantId(), is(applicant.getId()));
        assertThat(response.getReasonsForApply(), is(applicant.getReasonsForApplying()));
        assertThat(response.getKnowledgeOfCompanyManagement(), is(applicant.getKnowledgeOfCompanyManagement()));
        assertThat(response.getGoals(), is(applicant.getGoals()));
        assertThat(response.getCommentsForStockHolder(), is(applicant.getCommentsForStockHolder()));
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode(), election.getId(), applicant.getId())
                    .headers(headers(jwt(jwt)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

}
