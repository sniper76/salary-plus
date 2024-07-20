package ag.act.api.admin.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.DeleteDummyStockFromUserRequest;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someLocalDate;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class DeleteDummyStockFromUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/dummy-stock";

    private String jwt;
    private Long userId;
    private User user;
    private String stockCode;
    private DeleteDummyStockFromUserRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        user = itUtil.createAdminUser();
        userId = user.getId();
        stockCode = someStockCode();
        itUtil.createStock(stockCode);
        itUtil.createUserHoldingStockOnReferenceDate(stockCode, userId, someLocalDate());
    }

    @Nested
    class DeleteDummyStockFromUserSuccessfully {

        @BeforeEach
        void setUp() {
            itUtil.createDummyUserHoldingStock(stockCode, user);
            request = genRequest(stockCode);
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
            final boolean exists = itUtil.hasDummyUserStock(userId, stockCode);

            assertThat(exists, is(false));
        }

        private void assertUserStockOnReferenceDateFromDatabase() {
            final boolean exists = itUtil.hasUserStockOnReferenceDate(userId, stockCode);

            assertThat(exists, is(false));
        }
    }

    @Nested
    class FailToDeleteToDummyStock {
        @DisplayName("로그인 사용자가 어드민이 아닌 경우")
        @Nested
        class WhenNotAdminLoginUser {
            @BeforeEach
            void setUp() {
                jwt = itUtil.createJwt(itUtil.createUser().getId());
                itUtil.createDummyUserHoldingStock(stockCode, user);
                request = genRequest(stockCode);
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
                itUtil.createDummyUserHoldingStock(stockCode, user);
                request = genRequest(stockCode);
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
                itUtil.createDummyUserHoldingStock(stockCode, user);
                request = genRequest(stockCode);
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
                itUtil.createDummyUserHoldingStock(stockCode, user);
                request = genRequest(stockCode);
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
                itUtil.createDummyUserHoldingStock(stockCode, user);
                request = genRequest(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "종목 코드를 확인해주세요.");
            }
        }

        @DisplayName("해당 종목이 더미 데이터가 아닌 경우")
        @Nested
        class WhenNotDummyStock {
            @BeforeEach
            void setUp() {
                itUtil.createUserHoldingStock(stockCode, user);
                request = genRequest(stockCode);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotFound() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 사용자가 해당 더미 종목을 가지고 있지 않습니다.");
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, userId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private DeleteDummyStockFromUserRequest genRequest(String stockCode) {
        return new DeleteDummyStockFromUserRequest().stockCode(stockCode);
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        assertThat(result.getStatus(), is("ok"));
    }
}
