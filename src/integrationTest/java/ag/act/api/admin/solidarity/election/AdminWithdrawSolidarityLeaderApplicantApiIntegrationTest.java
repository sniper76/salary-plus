package ag.act.api.admin.solidarity.election;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.BlockedSolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.WithdrawSolidarityLeaderApplicantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class AdminWithdrawSolidarityLeaderApplicantApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/solidarity-leader-applicants/{solidarityLeaderApplicantId}/withdraw";

    private String jwt;
    private Stock stock;
    private Solidarity solidarity;
    private SolidarityLeaderApplicant applicant;
    private WithdrawSolidarityLeaderApplicantRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();

        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
        stock.setSolidarityId(solidarity.getId());
        itUtil.updateStock(stock);
    }

    @Nested
    @DisplayName("선출이 진행중일때(후보자등록기간, 투표기간)")
    class WhenElectionOnGoing {

        private User someUser;

        @BeforeEach
        void setUp() {
            SolidarityLeaderElectionStatusGroup statusGroup = someThing(
                SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_STATUS_GROUP,
                SolidarityLeaderElectionStatusGroup.VOTE_STATUS_GROUP
            );
            SolidarityLeaderElection election = itUtil.createSolidarityElection(stock.getCode(), statusGroup);

            someUser = itUtil.createUser();
            applicant = itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(),
                someUser.getId(),
                SolidarityLeaderElectionApplyStatus.COMPLETE,
                election.getId()
            );
        }

        @Test
        @DisplayName("철회 사유 입력 없이는 지원을 철회할 수 없다.")
        void shouldReturnBadRequest() throws Exception {
            request = new WithdrawSolidarityLeaderApplicantRequest().reason(someThing(null, "", " "));

            MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, BAD_REQUEST_STATUS, "지원 철회 사유를 입력하세요.");
        }

        @Test
        @DisplayName("관리자는 사용자의 지원을 철회할 수 있다.")
        void shouldReturnSuccess() throws Exception {
            request = new WithdrawSolidarityLeaderApplicantRequest().reason(someString(30));

            callApi(status().isOk());

            assertBlockedApplicant();
        }

        private void assertBlockedApplicant() {
            Optional<BlockedSolidarityLeaderApplicant> blockedApplicant = itUtil.findBlockedSolidarityLeaderApplicant(
                stock.getCode(),
                someUser.getId()
            );
            assertThat(blockedApplicant.isPresent(), is(Boolean.TRUE));

            BlockedSolidarityLeaderApplicant blockedSolidarityLeaderApplicant = blockedApplicant.get();
            assertThat(blockedSolidarityLeaderApplicant.getSolidarityId(), is(stock.getSolidarityId()));
            assertThat(blockedSolidarityLeaderApplicant.getReasons(), is(request.getReason()));
            assertThat(blockedSolidarityLeaderApplicant.getStockCode(), is(stock.getCode()));
        }

        @Nested
        @DisplayName("사용자가 어드민이 아닐때,")
        class WhenUserIsNotAdmin {

            @BeforeEach
            void setUp() {
                User someUser = itUtil.createUser();
                jwt = itUtil.createJwt(someUser.getId());
            }

            @Test
            @DisplayName("사용자의 지원을 철회할 수 없다.")
            void shouldReturnForbidden() throws Exception {
                request = new WithdrawSolidarityLeaderApplicantRequest().reason(someString(30));

                MvcResult mvcResult = callApi(status().isForbidden());

                itUtil.assertErrorResponse(mvcResult, FORBIDDEN_STATUS, "인가되지 않은 접근입니다.");
            }
        }
    }

    @Nested
    @DisplayName("선출이 진행중이 아닐때(후보자등록대기기간, 종료)")
    class WhenElectionNotOnGoing {

        @BeforeEach
        void setUp() {
            SolidarityLeaderElectionStatusGroup statusGroup = someThing(
                SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_WITH_NO_WINNER_STATUS_GROUP,
                SolidarityLeaderElectionStatusGroup.FINISHED_EARLY_STATUS_GROUP,
                SolidarityLeaderElectionStatusGroup.FINISHED_BY_NO_CANDIDATE_STATUS_GROUP,
                SolidarityLeaderElectionStatusGroup.CANDIDATE_REGISTER_PENDING_STATUS_GROUP,
                SolidarityLeaderElectionStatusGroup.FINISHED_ON_TIME_STATUS_GROUP
            );

            User someUser = itUtil.createUser();
            SolidarityLeaderElection election = itUtil.createSolidarityElection(stock.getCode(), statusGroup);
            applicant = itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(),
                someUser.getId(),
                SolidarityLeaderElectionApplyStatus.COMPLETE,
                election.getId()
            );
        }

        @Test
        @DisplayName("관리자는 사용자의 지원을 철회할 수 없다.")
        void shouldReturnBadRequest() throws Exception {
            request = new WithdrawSolidarityLeaderApplicantRequest().reason(someString(30));

            MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, BAD_REQUEST_STATUS, "관리자가 지원을 철회할 수 있는 기간이 아닙니다.");
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                post(TARGET_API, stock.getCode(), applicant.getId())
                    .header(AUTHORIZATION, "Bearer " + jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapperUtil.toRequestBody(request))
            ).andExpect(resultMatcher)
            .andReturn();
    }
}
