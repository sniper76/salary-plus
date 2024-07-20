package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someSolidarityLeaderElectionApplicationItem;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.COMPLETE;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.DELETED_BY_USER;
import static ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus.SAVE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("LineLength")
public class GetSolidarityLeaderApplicationIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId}";

    private User user;
    private String nickName;
    private String stockCode;
    private String jwt;
    private Solidarity solidarity;
    private SolidarityLeaderApplicant solidarityLeaderApplicant;
    private SolidarityLeaderElectionApplyStatus applyStatus;
    private String reasonsForApplying;
    private String knowledgeOfCompanyManagement;
    private String goals;
    private String commentsForStockHolder;
    private Long solidarityLeaderElectionId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final Stock stock = itUtil.createStock();
        stockCode = stock.getCode();

        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        itUtil.createUserHoldingStock(stockCode, user);

        solidarity = itUtil.createSolidarity(stockCode);
        SolidarityLeaderElection solidarityElection = itUtil.createSolidarityElection(stockCode, DateTimeUtil.getTodayLocalDateTime());
        solidarityLeaderElectionId = solidarityElection.getId();
    }

    @Nested
    class WhenComplete {

        @BeforeEach
        void setUp() {
            applyStatus = COMPLETE;

            reasonsForApplying = someSolidarityLeaderElectionApplicationItem();
            knowledgeOfCompanyManagement = someSolidarityLeaderElectionApplicationItem();
            goals = someSolidarityLeaderElectionApplicationItem();
            commentsForStockHolder = someSolidarityLeaderElectionApplicationItem();

            solidarityLeaderApplicant = createSolidarityLeaderApplicant(user);

            nickName = user.getNickname();
        }

        @Nested
        class WhenFail {

            @BeforeEach
            void setUp() {
                stockCode = itUtil.createStock().getCode();
                itUtil.createUserHoldingStock(stockCode, user);
            }

            @Test
            @DisplayName("지원서의 종목과 요청시 종목이 일치하지 않으면 지원서를 조회할 수 없다.")
            void shouldReturnNotFound() throws Exception {
                MvcResult result = callApiAndGetResult(status().isNotFound());
                itUtil.assertErrorResponse(result, 404, "해당 종목에 대한 지원 내역이 없습니다.");
            }

        }

        @Nested
        class WhenNotAuthorGet {

            @BeforeEach
            void setUp() {
                User anotherUser = itUtil.createUser();

                itUtil.createUserHoldingStock(stockCode, anotherUser);
                jwt = itUtil.createJwt(anotherUser.getId());

                nickName = user.getNickname();
            }

            @Test
            @DisplayName("최종 제출된 지원서를 다른 주주가 조회할 수 있다.")
            void shouldReturnApplicationResponse() throws Exception {
                SolidarityLeaderApplicationResponse response = toResponse(callApiAndGetResult(status().isOk()));

                assertSolidarityApplication(response, solidarityLeaderApplicant);
            }

        }

        @Nested
        class WhenAuthorGet {

            @BeforeEach
            void setUp() {
                jwt = itUtil.createJwt(user.getId());
                nickName = user.getNickname();
            }

            @Test
            @DisplayName("최종 제출된 지원서를 작성자가 조회 가능하다.")
            void shouldReturnApplicationResponse() throws Exception {
                SolidarityLeaderApplicationResponse response = toResponse(callApiAndGetResult(status().isOk()));

                assertSolidarityApplication(response, solidarityLeaderApplicant);
            }
        }
    }

    @Nested
    class WhenDeleteByUser {

        @BeforeEach
        void setUp() {
            applyStatus = DELETED_BY_USER;

            reasonsForApplying = someSolidarityLeaderElectionApplicationItem();
            knowledgeOfCompanyManagement = someSolidarityLeaderElectionApplicationItem();
            goals = someSolidarityLeaderElectionApplicationItem();
            commentsForStockHolder = someSolidarityLeaderElectionApplicationItem();

            solidarityLeaderApplicant = createSolidarityLeaderApplicant(user);

            nickName = user.getNickname();
        }

        @Test
        @DisplayName("지원서가 삭제 상태이면 지원서를 조회할 수 없다.")
        void shouldReturnNotFound() throws Exception {
            MvcResult result = callApiAndGetResult(status().isNotFound());
            itUtil.assertErrorResponse(result, 404, "해당 종목에 대한 지원 내역이 없습니다.");
        }
    }

    @Nested
    class WhenSave {

        @Nested
        class WhenAuthorGet {

            @Nested
            @DisplayName("선출 프로세스가 진행 중일 때")
            class WhenElectionExist {

                @BeforeEach
                void setUp() {
                    applyStatus = SAVE;

                    reasonsForApplying = null;
                    knowledgeOfCompanyManagement = someSolidarityLeaderElectionApplicationItem();
                    goals = someSolidarityLeaderElectionApplicationItem();
                    commentsForStockHolder = someSolidarityLeaderElectionApplicationItem();

                    final User user = itUtil.createUser();
                    itUtil.createUserHoldingStock(stockCode, user);

                    solidarityLeaderApplicant = createSolidarityLeaderApplicant(user);

                    jwt = itUtil.createJwt(user.getId());
                    nickName = user.getNickname();
                }

                @Test
                @DisplayName("임시 저장한 지원서를 작성자가 조회 가능하다.")
                void shouldReturnApplicationResponse() throws Exception {
                    SolidarityLeaderApplicationResponse response = toResponse(callApiAndGetResult(status().isOk()));

                    assertSolidarityApplication(response, solidarityLeaderApplicant);
                }
            }

            @Nested
            @DisplayName("선출 프로세스가 진행 중이 아닐 때")
            class WhenElectionNotExist {
                @BeforeEach
                void setUp() {
                    applyStatus = SAVE;

                    final User user = itUtil.createUser();
                    itUtil.createUserHoldingStock(stockCode, user);

                    reasonsForApplying = someSolidarityLeaderElectionApplicationItem();
                    knowledgeOfCompanyManagement = someSolidarityLeaderElectionApplicationItem();
                    goals = someSolidarityLeaderElectionApplicationItem();
                    commentsForStockHolder = someSolidarityLeaderElectionApplicationItem();

                    solidarityLeaderApplicant = itUtil.createSolidarityLeaderApplicant(
                        solidarityLeaderElectionId,
                        solidarity.getId(),
                        user.getId(),
                        applyStatus,
                        reasonsForApplying,
                        knowledgeOfCompanyManagement,
                        goals,
                        commentsForStockHolder
                    );

                    jwt = itUtil.createJwt(user.getId());
                    nickName = user.getNickname();
                }

                @Test
                @DisplayName("임시 저장한 지원서를 작성자가 조회 가능하다.")
                void shouldReturnApplicationResponse() throws Exception {
                    SolidarityLeaderApplicationResponse response = toResponse(callApiAndGetResult(status().isOk()));

                    assertSolidarityApplication(response, solidarityLeaderApplicant);
                }
            }
        }

        @Nested
        class WhenNotAuthorGet {

            @BeforeEach
            void setUp() {
                applyStatus = SAVE;

                reasonsForApplying = null;
                knowledgeOfCompanyManagement = someSolidarityLeaderElectionApplicationItem();
                goals = someSolidarityLeaderElectionApplicationItem();
                commentsForStockHolder = someSolidarityLeaderElectionApplicationItem();

                final User anotherUser = itUtil.createUser();
                itUtil.createUserHoldingStock(stockCode, anotherUser);

                solidarityLeaderApplicant = createSolidarityLeaderApplicant(anotherUser);
            }

            @Test
            @DisplayName("임시 저장한 지원서는 작성자가 아닌 주주가 조회할 수 없다.")
            void shouldReturnBadRequest() throws Exception {
                MvcResult result = callApiAndGetResult(status().isBadRequest());
                itUtil.assertErrorResponse(result, 400, "임시 저장된 지원서는 작성자만 확인할 수 있습니다.");
            }
        }
    }

    private MvcResult callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, stockCode, solidarityLeaderElectionId, solidarityLeaderApplicant.getId())
                    .header(AUTHORIZATION, "Bearer " + jwt)
            ).andExpect(resultMatcher)
            .andReturn();
    }

    private SolidarityLeaderApplicationResponse toResponse(MvcResult mvcResult) throws Exception {
        return objectMapperUtil.toResponse(mvcResult.getResponse().getContentAsString(), SolidarityLeaderApplicationResponse.class);
    }

    private void assertSolidarityApplication(SolidarityLeaderApplicationResponse response, SolidarityLeaderApplicant expected) {
        final SolidarityLeaderApplicantResponse applicantUser = response.getUser();
        assertThat(applicantUser.getId(), is(expected.getUserId()));
        assertThat(applicantUser.getNickname(), is(nickName));
        assertThat(response.getSolidarityLeaderApplicantId(), is(expected.getId()));
        assertThat(response.getApplyStatus(), is(applyStatus.name()));
        assertThat(response.getReasonsForApply(), is(reasonsForApplying));
        assertThat(response.getKnowledgeOfCompanyManagement(), is(knowledgeOfCompanyManagement));
        assertThat(response.getGoals(), is(goals));
        assertThat(response.getCommentsForStockHolder(), is(commentsForStockHolder));
    }

    private SolidarityLeaderApplicant createSolidarityLeaderApplicant(User user) {
        return itUtil.createSolidarityLeaderApplicant(
            solidarityLeaderElectionId,
            solidarity.getId(),
            user.getId(),
            applyStatus,
            reasonsForApplying,
            knowledgeOfCompanyManagement,
            goals,
            commentsForStockHolder
        );
    }

}
