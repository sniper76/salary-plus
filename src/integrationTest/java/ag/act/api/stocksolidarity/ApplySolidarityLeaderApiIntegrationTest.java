package ag.act.api.stocksolidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.SlackChannel;
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
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApplySolidarityLeaderApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity/apply-leader";
    private static final int APPLY_VERSION = 1;

    private String jwt;
    private Stock stock;
    private Solidarity solidarity;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        itUtil.createBoard(stock);
        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class Success {
        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));

            then(slackMessageSender).should().sendSlackMessage(
                "[%s] 주주연대에 [%s]님이 주주대표로 지원하였습니다.".formatted(stock.getName(), user.getName()),
                SlackChannel.ACT_SOLIDARITY_LEADER_APPLICANT_ALERT
            );

            assertVersion();
        }

        private void assertVersion() {
            final Optional<SolidarityLeaderApplicant> solidarityLeaderApplicant = itUtil.findDefaultVersionSolidarityLeaderApplicant(
                solidarity.getId(),
                user.getId()
            );

            assertThat(solidarityLeaderApplicant.isPresent(), is(true));
            assertThat(solidarityLeaderApplicant.get().getVersion(), is(APPLY_VERSION));
        }
    }

    @Nested
    class WhenSolidarityLeaderAlreadyExists {
        @BeforeEach
        void setUp() {
            itUtil.createSolidarityLeader(solidarity, user.getId());
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "해당 연대에 이미 주주대표가 존재합니다.");
        }
    }

    @Nested
    class WhenUserAlreadyHasBeenAppliedForLeader {
        @BeforeEach
        void setUp() {
            itUtil.createSolidarityLeaderApplicant(solidarity.getId(), user.getId());
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "이미 주주대표 지원이 완료되었습니다.");
        }
    }
}
