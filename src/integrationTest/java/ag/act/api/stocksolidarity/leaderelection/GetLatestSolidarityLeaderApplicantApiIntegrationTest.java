package ag.act.api.stocksolidarity.leaderelection;

import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static ag.act.TestUtil.someSolidarityLeaderElectionApplyStatus;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetLatestSolidarityLeaderApplicantApiIntegrationTest extends AbstractSolidarityLeaderElectionApiIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity-leader-elections/solidarity-leader-applicants/latest";

    @Nested
    class WhenUserAppliedAndElectionActive {

        @BeforeEach
        void setUp() {
            SolidarityLeaderElection election = itUtil.createSolidarityElection(
                stock.getCode(),
                SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                DateTimeUtil.getTodayLocalDateTime()
            );
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(),
                user.getId(),
                someSolidarityLeaderElectionApplyStatus(),
                election.getId()
            );
        }

        @Test
        void shouldNotReturnResponse() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());

            assertNotExistResponse(mvcResult);
        }
    }

    @Nested
    class WhenUserNotAppliedAndNotHaveApplicant {

        @Nested
        class WhenUserNotHaveApplicant {

            @Test
            void shouldNotReturnResponse() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                assertNotExistResponse(mvcResult);
            }
        }

        @Nested
        class WhenUserHaveApplicant {

            private SolidarityLeaderApplicant applicant;

            @BeforeEach
            void setUp() {
                LocalDateTime somePastDateTime = DateTimeUtil.getTodayLocalDateTime().minusYears(1);
                SolidarityLeaderElection election = itUtil.createSolidarityElection(
                    stock.getCode(),
                    SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP,
                    somePastDateTime
                );
                applicant = itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(),
                    user.getId(),
                    someSolidarityLeaderElectionApplyStatus(),
                    election.getId()
                );
            }

            @Test
            void shouldReturnResponse() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());
                SolidarityLeaderApplicationResponse response = getResponse(mvcResult);

                assertThat(response.getSolidarityLeaderApplicantId(), nullValue());
                assertThat(response.getApplyStatus(), nullValue());

                assertThat(response.getStock().getCode(), is(stock.getCode()));
                assertThat(response.getStock().getName(), is(stock.getName()));
                assertThat(response.getUser().getId(), is(user.getId()));
                assertThat(response.getUser().getNickname(), is(user.getNickname()));
                assertThat(response.getReasonsForApply(), is(applicant.getReasonsForApplying()));
                assertThat(response.getKnowledgeOfCompanyManagement(), is(applicant.getKnowledgeOfCompanyManagement()));
                assertThat(response.getGoals(), is(applicant.getGoals()));
                assertThat(response.getCommentsForStockHolder(), is(applicant.getCommentsForStockHolder()));
            }
        }

        @Test
        void shouldNotReturnResponse() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());

            assertNotExistResponse(mvcResult);
        }
    }

    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stock.getCode())
                    .header("Authorization", "Bearer " + jwt)
            ).andExpect(matcher)
            .andReturn();
    }

    private SolidarityLeaderApplicationResponse getResponse(MvcResult mvcResult) throws Exception {
        MockHttpServletResponse response = mvcResult.getResponse();
        return objectMapperUtil.toResponse(response.getContentAsString(), SolidarityLeaderApplicationResponse.class);
    }

    private void assertNotExistResponse(MvcResult result) throws Exception {
        assertThat(result.getResponse().getContentAsString(), is(""));
    }
}
