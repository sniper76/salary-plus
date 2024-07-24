package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.model.AddDummyStockToUserRequest;
import ag.act.model.Status;
import ag.act.repository.UserHoldingStockOnReferenceDateRepository;
import ag.act.repository.UserHoldingStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someLocalDate;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class AddDummyStockToUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/dummy-stock";

    @Autowired
    private UserHoldingStockRepository userHoldingStockRepository;

    @Autowired
    private UserHoldingStockOnReferenceDateRepository userHoldingStockOnReferenceDateRepository;

    private String jwt;
    private Long userId;
    private User user;
    private String stockCode;
    private Long stockReferenceDateId;
    private StockReferenceDate stockReferenceDate;
    private AddDummyStockToUserRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        user = itUtil.createAdminUser();
        userId = user.getId();

        stockCode = someStockCode();
        stockReferenceDate = itUtil.createStockReferenceDate(stockCode, someLocalDate());
    }

    @Nested
    class AddDummyStockToUserSuccessfully {

        @BeforeEach
        void setUp() {
            itUtil.createStock(stockCode);
            stockReferenceDateId = stockReferenceDate.getId();
            request = genRequest(stockCode, stockReferenceDateId);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertResponse(result);
            assertUserStockFromDatabase();
            assertUserStockOnReferenceDateFromDatabase();
        }

        private void assertUserStockFromDatabase() {
            final UserHoldingStock userHoldingStock =
                userHoldingStockRepository.findFirstByUserIdAndStockCode(userId, stockCode).orElseThrow();

            assertThat(userHoldingStock.getStock().getCode(), is(stockCode));
            assertThat(userHoldingStock.getUserId(), is(userId));
        }

        private void assertUserStockOnReferenceDateFromDatabase() {
            final UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate =
                userHoldingStockOnReferenceDateRepository.findByUserIdAndStockCodeAndReferenceDate(
                    userId, stockCode, stockReferenceDate.getReferenceDate()).orElseThrow();

            assertThat(userHoldingStockOnReferenceDate.getStock().getCode(), is(stockCode));
            assertThat(userHoldingStockOnReferenceDate.getUserId(), is(userId));
            assertThat(userHoldingStockOnReferenceDate.getReferenceDate(), is(stockReferenceDate.getReferenceDate()));
        }
    }

    @Nested
    class FailToAddToDummyStock {
        @DisplayName("로그인 사용자가 어드민이 아닌 경우")
        @Nested
        class WhenNotAdminLoginUser {
            @BeforeEach
            void setUp() {
                jwt = itUtil.createJwt(itUtil.createUser().getId());
                itUtil.createStock(stockCode);
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 403 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isForbidden());

                itUtil.assertErrorResponse(response, 403, "인가되지 않은 접근입니다.");
            }
        }


        @DisplayName("타겟 사용자의 정보를 찾을 수 없는 경우")
        @Nested
        class WhenNotFoundUser {
            @BeforeEach
            void setUp() {
                userId = someLong();
                itUtil.createStock(stockCode);
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 404 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotFound() throws Exception {
                final MvcResult response = callApi(status().isNotFound());

                itUtil.assertErrorResponse(response, 404, "회원정보를 찾을 수 없습니다.");
            }
        }

        @DisplayName("타겟 사용자가 탈퇴한 경우")
        @Nested
        class WhenWithdrawnUser {
            @BeforeEach
            void setUp() {
                itUtil.changeUserStatus(user, someThing(Status.DELETED_BY_USER, Status.DELETED_BY_ADMIN));
                itUtil.updateUser(user);
                itUtil.createStock(stockCode);
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 410 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotFound() throws Exception {
                final MvcResult response = callApi(status().isGone());

                itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
            }
        }

        @DisplayName("타겟 사용자가 어드민 권한이 없는 경우")
        @Nested
        class WhenNotAdminUser {
            @BeforeEach
            void setUp() {
                final User user = itUtil.createUser();
                userId = user.getId();
                itUtil.createStock(stockCode);
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 사용자는 어드민 권한이 없습니다.");
            }
        }

        @DisplayName("종목이 빈 값인 경우")
        @Nested
        class WhenEmptyStockCode {
            @BeforeEach
            void setUp() {
                itUtil.createStock(stockCode);
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(null, stockReferenceDateId);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 종목의 기준일 정보가 아닙니다.");
            }
        }

        @DisplayName("종목이 존재하지 않는 경우")
        @Nested
        class WhenNotFoundStockCode {
            @BeforeEach
            void setUp() {
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotFound() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "존재하지 않는 종목입니다.");
            }
        }

        @DisplayName("이미 해당 종목을 보유하고 있는 경우")
        @Nested
        class WhenAlreadyHaveStock {
            @BeforeEach
            void setUp() {
                itUtil.createStock(stockCode);
                itUtil.createUserHoldingStock(stockCode, user);
                stockReferenceDateId = stockReferenceDate.getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "이미 해당 종목을 보유하고 있습니다.");
            }
        }

        @DisplayName("해당 기준일 정보가 없는 경우")
        @Nested
        class WhenNotFoundStockReferenceDate {
            @BeforeEach
            void setUp() {
                itUtil.createStock(stockCode);
                request = genRequest(stockCode, someLong());
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotFound() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 기준일 정보가 없습니다.");
            }
        }

        @DisplayName("해당 종목의 해당 기준일 정보가 없는 경우")
        @Nested
        class WhenNotMatchStockReferenceDate {
            @BeforeEach
            void setUp() {
                itUtil.createStock(stockCode);
                stockReferenceDateId = itUtil.createStockReferenceDate(someStockCode(), someLocalDate()).getId();
                request = genRequest(stockCode, stockReferenceDateId);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 종목의 기준일 정보가 아닙니다.");
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, userId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private AddDummyStockToUserRequest genRequest(String stockCode, Long stockReferenceDateId) {
        return new AddDummyStockToUserRequest().stockCode(stockCode).stockReferenceDateId(stockReferenceDateId);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));
    }
}
