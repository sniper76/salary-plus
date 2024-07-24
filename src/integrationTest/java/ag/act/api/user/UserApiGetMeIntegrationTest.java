package ag.act.api.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.RoleType;
import ag.act.model.SimpleStockResponse;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;

class UserApiGetMeIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me";
    private static final Long ZERO_ASSET_AMOUNT = 0L;

    private String jwt;
    private User user;
    private List<String> leadingSolidarityStockCodes = List.of();
    private List<String> dummyStockCode;
    private Long userId;
    private final Boolean isSolidarityLeaderConfidentialAgreementSigned = someBoolean();

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        userId = user.getId();
        jwt = itUtil.createJwt(user.getId());

        user.setIsSolidarityLeaderConfidentialAgreementSigned(isSolidarityLeaderConfidentialAgreementSigned);
        user = itUtil.updateUser(user);

        dummyStockCode = List.of(
            itUtil.createDummyUserHoldingStock(itUtil.createStock().getCode(), user).getStockCode(),
            itUtil.createDummyUserHoldingStock(itUtil.createStock().getCode(), user).getStockCode(),
            itUtil.createDummyUserHoldingStock(itUtil.createStock().getCode(), user).getStockCode()
        );
    }

    @Nested
    class WhenNormalUser {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertResponse(callApi());
        }
    }

    @Nested
    class WhenUserIsAdmin {

        @BeforeEach
        void setUp() {
            user = itUtil.createUserRole(user, RoleType.ADMIN);
            user.setAdmin(true);
        }

        @Nested
        class UserIsNotSolidarityLeader {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }
        }

        @Nested
        class UserIsSolidarityLeaderOfSomeSolidarities {

            @BeforeEach
            void setUp() {
                leadingSolidarityStockCodes = List.of(
                    mockUserAndSolidarity(),
                    mockUserAndSolidarity(),
                    mockUserAndSolidarity(),
                    mockUserAndSolidarity(),
                    mockUserAndSolidarityWithDummyUserHolingStock()
                );
            }

            private String mockUserAndSolidarity() {
                createStockAndHoldingAsNormalUser();
                return createStockAndHoldingStockAndSolidarityLeader().getCode();
            }

            private String mockUserAndSolidarityWithDummyUserHolingStock() {
                createStockAndHoldingAsNormalUser();
                return createStockAndDummyHoldingStockAndSolidarityLeader().getCode();
            }

            private void createStockAndHoldingAsNormalUser() {
                final Stock stock = itUtil.createStock();
                itUtil.createUserHoldingStock(stock.getCode(), user);
                itUtil.createSolidarity(stock.getCode());
            }

            private Stock createStockAndHoldingStockAndSolidarityLeader() {
                final Stock stock = itUtil.createStock();
                itUtil.createUserHoldingStock(stock.getCode(), user);
                itUtil.createSolidarityLeader(itUtil.createSolidarity(stock.getCode()), user.getId());
                return stock;
            }

            private Stock createStockAndDummyHoldingStockAndSolidarityLeader() {
                final Stock stock = itUtil.createStock();
                itUtil.createDummyUserHoldingStock(stock.getCode(), user);
                itUtil.createSolidarityLeader(itUtil.createSolidarity(stock.getCode()), user.getId());
                return stock;
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi(), itUtil.getTotalAssetAmount(user.getId()));
            }
        }
    }

    @Nested
    class WhenNormalUserWithBadgeVisibility {

        Map<String, Boolean> userBadgeTypeBooleanMap;

        @BeforeEach
        void setUp() {
            itUtil.createUserBadgeVisibility(user.getId());
            userBadgeTypeBooleanMap = itUtil.getUserBadgeVisibilityMapByUserId(user.getId());
        }

        @DisplayName("Should all badge visibility return isVisible data when user badge visibility data exist")
        @Test
        void shouldReturnAllBadgeVisibility() throws Exception {

            UserDataResponse userDataResponse = callApi();
            assertResponse(userDataResponse);

            userDataResponse.getData()
                .getUserBadgeVisibilities()
                .forEach(response ->
                    assertThat(response.getIsVisible(), is(userBadgeTypeBooleanMap.get(response.getLabel())))
                );
        }
    }

    @Nested
    class WhenNormalUserWithoutBadgeVisibility {
        @DisplayName("Should all badge visibility return true when user badge visibility data not exist")
        @Test
        void shouldReturnAllBadgeVisibilityTrue() throws Exception {
            UserDataResponse userDataResponse = callApi();
            assertResponse(userDataResponse);

            userDataResponse.getData().getUserBadgeVisibilities()
                .forEach(response -> assertThat(response.getIsVisible(), is(true)));
        }
    }

    private void assertResponse(UserDataResponse result) {
        assertResponse(result, ZERO_ASSET_AMOUNT);
    }

    private void assertResponse(UserDataResponse result, Long totalAssetAmount) {
        final UserResponse userResponse = result.getData();
        final User user = itUtil.findUser(userId);

        assertThat(userResponse.getId(), is(user.getId()));
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertThat(userResponse.getGender(), is(user.getGender()));
        assertThat(userResponse.getMySpeech(), is(user.getMySpeech()));
        assertThat(userResponse.getIsAgreeToReceiveMail(), is(user.getIsAgreeToReceiveMail()));
        assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
        assertThat(userResponse.getJobTitle(), is(user.getJobTitle()));
        assertThat(userResponse.getAddress(), is(user.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(user.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(user.getZipcode()));
        assertThat(userResponse.getTotalAssetAmount(), is(totalAssetAmount));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(user.getStatus()));
        assertThat(userResponse.getAuthType(), is(user.getAuthType()));
        assertThat(userResponse.getIsAdmin(), is(user.isAdmin()));
        assertThat(
            new HashSet<>(userResponse.getLeadingSolidarityStockCodes()),
            is(new HashSet<>(leadingSolidarityStockCodes))
        );
        assertUserHoldingStocksFromResponse(user, userResponse);
        assertThat(userResponse.getIsSolidarityLeaderConfidentialAgreementSigned(), is(isSolidarityLeaderConfidentialAgreementSigned));

        assertTime(userResponse.getBirthDate(), user.getBirthDate());
        assertTime(userResponse.getCreatedAt(), user.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), user.getUpdatedAt());
        assertTime(userResponse.getDeletedAt(), user.getDeletedAt());
    }

    private void assertUserHoldingStocksFromResponse(User user, UserResponse userResponse) {
        userResponse.getHoldingStocks()
            .forEach(simpleStockResponse -> {
                Optional<UserHoldingStock> userHoldingStock = itUtil.findUserHoldingStock(user.getId(), simpleStockResponse.getCode());
                assertThat(userHoldingStock.isPresent(), is(true));
                assertThat(simpleStockResponse.getCode(), is(userHoldingStock.get().getStockCode()));
                assertThat(simpleStockResponse.getName(), is(userHoldingStock.get().getStock().getName()));
                assertThat(simpleStockResponse.getStandardCode(), is(userHoldingStock.get().getStock().getStandardCode()));
            });

        final Map<String, String> userHoldingStockMap = getUserHoldingStockMap(userResponse);

        dummyStockCode.forEach(stockCode -> assertThat(userHoldingStockMap.containsKey(stockCode), is(true)));
    }

    @NotNull
    private Map<String, String> getUserHoldingStockMap(UserResponse userResponse) {
        return userResponse.getHoldingStocks().stream()
            .collect(Collectors.toMap(SimpleStockResponse::getCode, SimpleStockResponse::getName));
    }

    private UserDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return itUtil.getResult(response, UserDataResponse.class);
    }
}
