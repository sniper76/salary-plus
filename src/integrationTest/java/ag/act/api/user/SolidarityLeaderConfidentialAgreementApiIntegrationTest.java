package ag.act.api.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.model.CreateSolidarityLeaderConfidentialAgreementRequest;
import ag.act.model.GetSolidarityLeaderConfidentialAgreementResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import org.jetbrains.annotations.NotNull;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("LineLength")
class SolidarityLeaderConfidentialAgreementApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API_FORM = "/api/users/solidarity-leader-confidential-agreement-document-form";
    private static final String TARGET_API = "/api/users/solidarity-leader-confidential-agreement-document";

    private static final String AGREEMENT_ITEM_1 = "(1) 본인은 회사의 기밀은 물론 서비스를 이용하면서 취득한 사항 및 기타 주주행동의 비밀을 누설하거나 개인적인 목적(개인 투자 목적을 포함)으로 사용하지 않겠습니다.";
    private static final String AGREEMENT_ITEM_2 = "(2) 본인은 회사가 제공하는 종목별 주주대표 관련 서비스를 이용함에 따라 필요한 본인의 정보(성명, 생년월일, 전화번호, 이메일 등)을 회사가 수집함에 동의합니다.";
    private static final String AGREEMENT_ITEM_3 = "(3) 본인은 여하한 사유를 이유로, 본 서약의 내용에 따라 의무를 위반하여 회사에게 발생한 손해(회사가 서비스 제공에 투입한 비용 등 포함)에 대하여 배상할 것을 확약합니다.";
    private static final String AGREEMENT_ITEM_4 = "(4) 본인은 회사가 제공하는 종목별 주주대표 관련 서비스를 이용함에 있어, 주주대표 선출 절차, 주주대표 활동, 주주대표 해임 절차 중에는 회사가 제공하는 서비스에서 탈퇴하지 아니할 것에 동의합니다.";

    private String jwt;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class Success {

        @Nested
        class WhenGetForm {

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callGetApi(status().isOk());

                final GetSolidarityLeaderConfidentialAgreementResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    GetSolidarityLeaderConfidentialAgreementResponse.class
                );

                final String content = result.getContent();
                assertThat(content.contains(user.getName()), is(Boolean.TRUE));
                assertThat(content.contains(
                    DateTimeUtil.getFormattedDateTime(user.getBirthDate(), "yyyy년 MM월 dd일")), is(Boolean.TRUE)
                );
                assertThat(content.contains(AGREEMENT_ITEM_1), is(Boolean.TRUE));
                assertThat(content.contains(AGREEMENT_ITEM_2), is(Boolean.TRUE));
                assertThat(content.contains(AGREEMENT_ITEM_3), is(Boolean.TRUE));
                assertThat(content.contains(AGREEMENT_ITEM_4), is(Boolean.TRUE));
            }
        }

        @Nested
        class WhenCreatePostSignedFlag {
            private CreateSolidarityLeaderConfidentialAgreementRequest request;

            @BeforeEach
            void setUp() {
                final Stock stock = itUtil.createStock();
                createUserHoldingStockForTestStock(stock);

                request = new CreateSolidarityLeaderConfidentialAgreementRequest();
                request.setStockCode(stock.getCode());
            }

            private void createUserHoldingStockForTestStock(Stock stock) {
                final UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
                userHoldingStock.setStatus(null); // make it null for TestStocks
                itUtil.updateUserHoldingStock(userHoldingStock);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callPostApi(status().isOk(), request);

                final SimpleStringResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                final User databaseUser = itUtil.findUser(user.getId());

                assertThat(result.getStatus(), is("ok"));
                assertThat(databaseUser.getIsSolidarityLeaderConfidentialAgreementSigned(), is(Boolean.TRUE));
            }
        }
    }

    @Nested
    class Error {

        @Nested
        class WhenDoesNotHasUserHoldingStock {
            private CreateSolidarityLeaderConfidentialAgreementRequest request;

            @BeforeEach
            void setUp() {
                final Stock stock = itUtil.createStock();

                request = new CreateSolidarityLeaderConfidentialAgreementRequest();
                request.setStockCode(stock.getCode());
            }

            @Test
            void shouldReturnError() throws Exception {
                MvcResult response = callPostApi(status().isBadRequest(), request);
                itUtil.assertErrorResponse(response, 400, "보유하지 않은 종목에 비밀유지 서약서를 제출할 수 없습니다.");
            }
        }
    }

    @NotNull
    private MvcResult callGetApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API_FORM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @NotNull
    private MvcResult callPostApi(ResultMatcher resultMatcher, CreateSolidarityLeaderConfidentialAgreementRequest request) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
