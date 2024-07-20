package ag.act.api.stocksolidarity.leaderelection;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.model.GetSolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.util.badge.BadgeLabelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someLocalDateTime;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetSolidarityLeaderApplicantsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API =
        "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants";

    @Autowired
    private BadgeLabelGenerator badgeLabelGenerator;

    private String jwt;
    private User user;

    private String stockCode;
    private Long solidarityLeaderElectionId;
    private Long solidarityId;
    private User user1;
    private User user2;
    private User user3;
    private UserHoldingStock userHoldingStock1;
    private UserHoldingStock userHoldingStock2;
    private UserHoldingStock userHoldingStock3;
    private SolidarityLeaderApplicant solidarityLeaderApplicant1;
    private SolidarityLeaderApplicant solidarityLeaderApplicant2;
    private SolidarityLeaderApplicant solidarityLeaderApplicant3;

    @BeforeEach
    void setUp() {
        itUtil.init();

        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        final Stock stock = itUtil.createStock();
        stockCode = stock.getCode();
        final Solidarity solidarity = itUtil.createSolidarity(stockCode);
        solidarityId = solidarity.getId();

        final SolidarityLeaderElection solidarityLeaderElection = itUtil.createSolidarityElection(stockCode, someLocalDateTime());
        solidarityLeaderElectionId = solidarityLeaderElection.getId();

        user1 = itUtil.createUser();
        userHoldingStock1 = itUtil.createUserHoldingStock(stockCode, user1);
        solidarityLeaderApplicant1 = itUtil.createSolidarityLeaderApplicant(solidarityId, user1.getId(), COMPLETE, solidarityLeaderElectionId);

        user2 = itUtil.createUser();
        userHoldingStock2 = itUtil.createUserHoldingStock(stockCode, user2);
        solidarityLeaderApplicant2 = itUtil.createSolidarityLeaderApplicant(solidarityId, user2.getId(), COMPLETE, solidarityLeaderElectionId);

        user3 = itUtil.createUser();
        userHoldingStock3 = itUtil.createUserHoldingStock(stockCode, user3);
        solidarityLeaderApplicant3 = itUtil.createSolidarityLeaderApplicant(solidarityId, user3.getId(), COMPLETE, solidarityLeaderElectionId);

        // 임시저장인 경우 , 조회X
        final User user4 = itUtil.createUser();
        itUtil.createUserHoldingStock(stockCode, user4);
        itUtil.createSolidarityLeaderApplicant(solidarityId, user4.getId(), SAVE, solidarityLeaderElectionId);

        // 해당 선거가 아닌 다른 선거에 지원한 경우, 조회X
        final User user5 = itUtil.createUser();
        final Long solidarityLeaderElectionId2 = itUtil.createSolidarityElection(stockCode, someLocalDateTime()).getId();
        itUtil.createSolidarityLeaderApplicant(solidarityId, user5.getId(), COMPLETE, solidarityLeaderElectionId2);
    }

    @Nested
    class WhenUserHaveStock {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stockCode, user);
        }

        @Test
        void shouldReturnSolidarityLeaderApplicants() throws Exception {
            final MvcResult mvcResult = callApiAndGetResult(status().isOk());
            final GetSolidarityLeaderApplicantResponse result = itUtil.getResult(mvcResult, GetSolidarityLeaderApplicantResponse.class);
            final List<SolidarityLeaderApplicantResponse> responses = result.getData();

            assertThat(responses.size(), is(3));
            assertResponse(responses.get(0), solidarityLeaderApplicant1, user1, userHoldingStock1);
            assertResponse(responses.get(1), solidarityLeaderApplicant2, user2, userHoldingStock2);
            assertResponse(responses.get(2), solidarityLeaderApplicant3, user3, userHoldingStock3);

        }

        private void assertResponse(
            SolidarityLeaderApplicantResponse response,
            SolidarityLeaderApplicant solidarityLeaderApplicant,
            User user,
            UserHoldingStock userHoldingStock
        ) {
            final String individualStockQuantity = badgeLabelGenerator.generateStockQuantityBadge(userHoldingStock.getQuantity());

            assertThat(response.getSolidarityApplicantId(), is(solidarityLeaderApplicant.getId()));
            assertThat(response.getId(), is(user.getId()));
            assertThat(response.getNickname(), is(user.getNickname()));
            assertThat(response.getProfileImageUrl(), is(user.getProfileImageUrl()));
            assertThat(response.getIndividualStockCountLabel(), is(individualStockQuantity));
            assertThat(response.getCommentsForStockHolder(), is(solidarityLeaderApplicant.getCommentsForStockHolder()));
        }

    }

    @Nested
    class WhenUserDoNotHaveStock {

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            final MvcResult response = callApiAndGetResult(status().isForbidden());

            itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
        }
    }

    @Nested
    class WhenApplicantInRegistrationPendingPeriod {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stockCode, user);

            final SolidarityLeaderElection solidarityLeaderElection = itUtil.createSolidarityElection(
                stockCode,
                new SolidarityLeaderElectionStatusGroup(
                    SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PENDING_PERIOD,
                    SolidarityLeaderElectionStatusDetails.IN_PROGRESS
                ),
                LocalDateTime.now()
            );
            solidarityLeaderElectionId = solidarityLeaderElection.getId();

            User user6 = itUtil.createUser();
            itUtil.createUserHoldingStock(stockCode, user6);
            solidarityLeaderApplicant1 = itUtil.createSolidarityLeaderApplicant(
                solidarityId,
                user6.getId(),
                SAVE,
                solidarityLeaderElectionId
            );
        }

        @Test
        void shouldReturnEmptyList() throws Exception {
            final MvcResult mvcResult = callApiAndGetResult(status().isOk());
            final GetSolidarityLeaderApplicantResponse result = itUtil.getResult(mvcResult, GetSolidarityLeaderApplicantResponse.class);
            final List<SolidarityLeaderApplicantResponse> responses = result.getData();

            assertThat(responses.size(), is(0));
        }
    }

    private MvcResult callApiAndGetResult(ResultMatcher matcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stockCode, solidarityLeaderElectionId)
                    .header("Authorization", "Bearer " + jwt)
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(matcher)
            .andReturn();
    }
}