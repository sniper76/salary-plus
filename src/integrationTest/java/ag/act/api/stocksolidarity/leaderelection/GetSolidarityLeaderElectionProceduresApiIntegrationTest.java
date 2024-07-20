package ag.act.api.stocksolidarity.leaderelection;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.model.SolidarityLeaderElectionProcedureResponse;
import ag.act.model.SolidarityLeaderElectionProceduresDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetSolidarityLeaderElectionProceduresApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/solidarity-leader-elections/procedures";

    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();

        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Test
    void shouldReturnSortedProcedures() throws Exception {
        SolidarityLeaderElectionProceduresDataResponse result = callApiAndGetResult();
        List<SolidarityLeaderElectionProcedureResponse> response = result.getData();

        final int expectedSize = 5;
        assertThat(response.size(), is(expectedSize));

        assertApplyBegin(response.get(0), SolidarityLeaderElectionProcedure.APPLY_BEGIN.name());
        assertRecruitApplicants(response.get(1), SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.name());
        assertStartVoting(response.get(2), SolidarityLeaderElectionProcedure.START_VOTING.name());
        assertCloseVoting(response.get(3), SolidarityLeaderElectionProcedure.CLOSE_VOTING.name());
        assertLeaderElected(response.get(4), SolidarityLeaderElectionProcedure.LEADER_ELECTED.name());
    }

    private void assertApplyBegin(SolidarityLeaderElectionProcedureResponse actual, String expectedName) {
        assertThat(actual.getName(), is(expectedName));
        assertThat(actual.getTitle(), is("주주대표 후보자 최초 지원(모집 개설)"));
        assertThat(actual.getDurationDays(), is(0));
        assertThat(actual.getDescription(), is("* 주주대표 지원자가 나오면 지원자 모집이 시작됩니다."));
        assertThat(actual.getDisplayOrder(), is(0));
    }

    private void assertRecruitApplicants(SolidarityLeaderElectionProcedureResponse actual, String expectedName) {
        assertThat(actual.getName(), is(expectedName));
        assertThat(actual.getTitle(), is("지원자 모집(4일)"));
        assertThat(actual.getDurationDays(), is(4));
        assertThat(actual.getDescription(), is("* 모든 지원자가 지원을 취소할 경우 선출과정이 초기화됩니다."));
        assertThat(actual.getDisplayOrder(), is(1));
    }

    @SuppressWarnings("LineLength")
    private void assertStartVoting(SolidarityLeaderElectionProcedureResponse actual, String expectedName) {
        assertThat(actual.getName(), is(expectedName));
        assertThat(actual.getTitle(), is("주주대표 선출 투표 시작(3일)"));
        assertThat(actual.getDurationDays(), is(3));
        assertThat(actual.getDescription(), is("* 선출기준\n- 투표시작시점 기준, 찬성표가 액트 내 결집 지분의 25% 이상 & 반대표를 초과시.\n- 투표시작시점 기준, 찬성표가 액트 내 결집 지분의 50% 초과시(조기마감)."));
        assertThat(actual.getDisplayOrder(), is(2));
    }

    private void assertCloseVoting(SolidarityLeaderElectionProcedureResponse actual, String expectedName) {
        assertThat(actual.getName(), is(expectedName));
        assertThat(actual.getTitle(), is("주주대표 선출 투표 마감"));
        assertThat(actual.getDurationDays(), is(0));
        assertThat(actual.getDescription(), nullValue());
        assertThat(actual.getDisplayOrder(), is(3));
    }

    private void assertLeaderElected(SolidarityLeaderElectionProcedureResponse actual, String expectedName) {
        assertThat(actual.getName(), is(expectedName));
        assertThat(actual.getTitle(), is("주주대표 선출"));
        assertThat(actual.getDurationDays(), is(0));
        assertThat(actual.getDescription(), nullValue());
        assertThat(actual.getDisplayOrder(), is(4));
    }


    private SolidarityLeaderElectionProceduresDataResponse callApiAndGetResult() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get(TARGET_API)
                    .header(AUTHORIZATION, "Bearer " + jwt)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andReturn();

        return itUtil.getResult(mvcResult, SolidarityLeaderElectionProceduresDataResponse.class);
    }
}
