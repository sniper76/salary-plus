package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.SimpleUserProfileDataResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class GetSimpleProfileApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/{userId}/profile";

    @Autowired
    private GlobalBoardManager globalBoardManager;
    private String jwt;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        this.user = itUtil.createUser();
    }

    @Nested
    class WhenStockCodeIsNotProvided {

        @Nested
        class AndUserExist {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.UserProfileDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.UserProfileDataResponse.class
                );

                assertResponse(result);
            }

            private void assertResponse(ag.act.model.UserProfileDataResponse result) {
                final ag.act.model.UserProfileResponse userResponse = result.getData();
                assertThat(userResponse.getNickname(), is(user.getNickname()));
                assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
                assertThat(userResponse.getIndividualStockCountLabel(), nullValue());
                assertThat(userResponse.getTotalAssetLabel(), nullValue());
            }
        }

        @Nested
        class AndUserIsDeletedByUser {

            @BeforeEach
            void setUp() {
                user.setStatus(Status.DELETED_BY_USER);
                user.setDeletedAt(LocalDateTime.now());
                itUtil.updateUser(user);
            }

            @DisplayName("Should return 410 and error message `탈퇴한 회원입니다.` when call " + TARGET_API)
            @Test
            void shouldReturnErrorWith410() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isGone())
                    .andReturn();

                itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
            }
        }

        @Nested
        class AndUserIsDeletedByAdmin {

            @BeforeEach
            void setUp() {
                user.setStatus(Status.DELETED_BY_ADMIN);
                user.setDeletedAt(LocalDateTime.now());
                itUtil.updateUser(user);
            }

            @DisplayName("Should return 410 and error message `탈퇴한 회원입니다.` when call " + TARGET_API)
            @Test
            void shouldReturnErrorWith410() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isGone())
                    .andReturn();

                itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
            }
        }

    }

    @Nested
    class WhenStockCodeProvided {

        private String stockCode;

        @BeforeEach
        void setUp() {
            Stock stock1 = itUtil.createStock();
            itUtil.createBoard(stock1);
            itUtil.createSolidarity(stock1.getCode());
            itUtil.createUserHoldingStock(stock1.getCode(), user);

            Stock stock2 = itUtil.createStock();
            stock2.setClosingPrice(100_000_000); // 1억
            stock2 = itUtil.updateStock(stock2);
            stockCode = stock2.getCode();

            itUtil.createBoard(stock2);
            itUtil.createSolidarity(stock2.getCode());
            final long quantity = 1; // 1주
            itUtil.createUserHoldingStock(stock2.getCode(), user, quantity);
        }

        @Nested
        class AndStockCodeIsForNormal {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .param("stockCode", stockCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final SimpleUserProfileDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleUserProfileDataResponse.class
                );

                assertResponse(result, user, "1주+");
            }
        }

        @Nested
        class AndStockCodeIsForGlobalBoard {
            @BeforeEach
            void setUp() {
                stockCode = globalBoardManager.getStockCode();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .param("stockCode", stockCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final SimpleUserProfileDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleUserProfileDataResponse.class
                );

                assertResponse(result, user, null);
            }
        }

        @Nested
        class AndUserDoesNotHaveThatStockAnymore {

            @BeforeEach
            void setUp() {
                stockCode = someAlphanumericString(6);
            }

            @DisplayName("Should return 410 and error message `현재 주식을 보유하지 않은 회원입니다.` when call " + TARGET_API)
            @Test
            void shouldReturnErrorWith410() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .param("stockCode", stockCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isGone())
                    .andReturn();

                itUtil.assertErrorResponse(response, 410, "현재 주식을 보유하지 않은 회원입니다.");
            }
        }

        @Nested
        class AndUserIsDeletedByUser {

            @BeforeEach
            void setUp() {
                user.setStatus(Status.DELETED_BY_USER);
                user.setDeletedAt(LocalDateTime.now());
                itUtil.updateUser(user);
            }

            @DisplayName("Should return 410 and error message `탈퇴한 회원입니다.` when call " + TARGET_API)
            @Test
            void shouldReturnErrorWith410() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .param("stockCode", stockCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isGone())
                    .andReturn();

                itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
            }
        }

        @Nested
        class AndUserIsDeletedByAdmin {

            @BeforeEach
            void setUp() {
                user.setStatus(Status.DELETED_BY_ADMIN);
                user.setDeletedAt(LocalDateTime.now());
                itUtil.updateUser(user);
            }

            @DisplayName("Should return 410 and error message `탈퇴한 회원입니다.` when call " + TARGET_API)
            @Test
            void shouldReturnErrorWith410() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API, user.getId())
                            .param("stockCode", stockCode)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isGone())
                    .andReturn();

                itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
            }
        }

    }

    private void assertResponse(SimpleUserProfileDataResponse result, User user, String individualStockCountLabel) {
        final ag.act.model.SimpleUserProfileResponse userResponse = result.getData();
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getIndividualStockCountLabel(), is(individualStockCountLabel));
        assertThat(userResponse.getTotalAssetLabel(), is("1억+"));
    }
}
