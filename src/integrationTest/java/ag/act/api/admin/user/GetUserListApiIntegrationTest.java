package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.ActUser;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.admin.UserFilterType;
import ag.act.enums.admin.UserSearchType;
import ag.act.itutil.holder.ActEntityTestHolder;
import ag.act.model.GetUserResponse;
import ag.act.model.Paging;
import ag.act.model.Status;
import ag.act.model.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someEmail;
import static ag.act.TestUtil.somePhoneNumber;
import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static ag.act.util.StatusUtil.getDeleteStatusesForUserList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetUserListApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users";

    private static final ActEntityTestHolder<User, Long> usersTestHolder = new ActEntityTestHolder<>();
    private static final ActEntityTestHolder<UserHoldingStock, Long> userHoldingStocksTestHolder = new ActEntityTestHolder<>();

    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private String givenName;
    private Long givenUserId;
    private String givenNickname;
    private String givenEmail;
    private String givenPhoneNumber;

    @BeforeEach
    void setUp() throws Exception {
        itUtil.init();
        final User superAdmin = itUtil.createSuperAdminUser();
        jwt = itUtil.createJwt(superAdmin.getId());

        usersTestHolder.initialize(itUtil.findUsersNotSuperAdminAndNotStatusIn(getDeleteStatusesForUserList()));
        userHoldingStocksTestHolder.initialize(itUtil.findAllUserHoldingStocks());

        cleanExistingUserHoldingStocks(userHoldingStocksTestHolder.getItems());

        givenName = someAlphanumericString(10);
        givenNickname = someAlphanumericString(20);
        givenEmail = someEmail();
        givenPhoneNumber = somePhoneNumber();

        user1 = itUtil.createUserWithGivenName(someString(2) + givenName);
        usersTestHolder.addOrSet(user1);
        user2 = itUtil.createUserWithGivenNickname(someString(2) + givenNickname);
        usersTestHolder.addOrSet(user2);
        user3 = itUtil.createUserWithGivenEmail(someString(2) + givenEmail);
        usersTestHolder.addOrSet(user3);
        user4 = itUtil.createUserWithPhoneNumber(givenPhoneNumber);
        usersTestHolder.addOrSet(user4);
        givenUserId = user4.getId();
    }

    private void cleanExistingUserHoldingStocks(List<UserHoldingStock> userHoldingStocks) {
        userHoldingStocks
            .forEach(userHoldingStock -> itUtil.updateUserHoldingStock(userHoldingStock));
    }

    @Nested
    class WhenSearchAllUsers {

        private int totalElements;

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", UserSearchType.NAME.toString(),
                    "searchKeyword", "",
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetUserResponse result) {
                final List<UserResponse> userResponses = result.getData();
                totalElements = usersTestHolder.getItems().size();

                assertThat(userResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), totalElements, result.getPaging().getSorts());
                assertPostResponse(user4, userResponses.get(0));
                assertPostResponse(user3, userResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "searchType", UserSearchType.NAME.toString(),
                    "searchKeyword", "",
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetUserResponse result) {
                final List<UserResponse> userResponses = result.getData();
                totalElements = usersTestHolder.getItems().size();

                assertThat(userResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), totalElements, result.getPaging().getSorts());
                assertPostResponse(user2, userResponses.get(0));
                assertPostResponse(user1, userResponses.get(1));
            }
        }
    }

    @Nested
    class WhenSearchAllUsersOrderByTotalAssetAmount {

        private long totalElements;

        @BeforeEach
        void setUp() {
            cleanExistUsers(usersTestHolder.getItems());

            user1 = createUserHoldingStock(4L, user1);
            user2 = createUserHoldingStock(3L, user2);
            user3 = createUserHoldingStock(2L, user3);
            user4 = createUserHoldingStock(1L, user4);
        }

        private User createUserHoldingStock(Long quantity, User user) {
            Stock stock = itUtil.createStock(someStockCode());
            stock.setClosingPrice(1000); // set all stock have same price to assert
            stock = itUtil.updateStock(stock);
            itUtil.createUserHoldingStock(stock.getCode(), user, quantity);
            return setTotalAssetAmount(user, stock);
        }

        private User setTotalAssetAmount(User user, Stock stock) {
            Optional<UserHoldingStock> userHoldingStock = itUtil.findUserHoldingStock(user.getId(), stock.getCode());
            userHoldingStock.ifPresent(holdingStock ->
                user.setTotalAssetAmount(holdingStock.getQuantity() * stock.getClosingPrice())
            );

            return itUtil.updateUser(user);
        }

        private void cleanExistUsers(List<User> users) {
            users.stream()
                .peek(user -> user.setTotalAssetAmount(someLongBetween(0L, 90L)))
                .forEach(user -> itUtil.updateUser(user));
        }

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "sorts", List.of("totalAssetAmount:DESC")
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetUserResponse result) {
                final List<UserResponse> userResponses = result.getData();
                totalElements = usersTestHolder.getItems().size();

                assertThat(userResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), totalElements, result.getPaging().getSorts());
                assertPostResponse(user1, userResponses.get(0));
                assertPostResponse(user2, userResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString(),
                    "sorts", List.of("totalAssetAmount:DESC")
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetUserResponse result) {
                final List<UserResponse> userResponses = result.getData();
                totalElements = usersTestHolder.getItems().size();

                assertThat(userResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), totalElements, result.getPaging().getSorts());
                assertPostResponse(user3, userResponses.get(0));
                assertPostResponse(user4, userResponses.get(1));
            }
        }
    }

    @Nested
    class WhenSearchUser {
        @Nested
        class ByNameKeyword {

            @Nested
            class AndFirstUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.NAME.toString(),
                        "searchKeyword", givenName,
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 1L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(1));
                    assertPostResponse(user1, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.NAME.toString(),
                        "searchKeyword", someString(10),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 0L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(0));
                }
            }
        }

        @Nested
        class ByUserIdKeyword {

            @Nested
            class AndFirstUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.USER_ID.toString(),
                        "searchKeyword", givenUserId,
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 1L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(1));
                    assertPostResponse(user4, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.USER_ID.toString(),
                        "searchKeyword", someNumericString(7),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 0L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(0));
                }
            }
        }

        @Nested
        class ByNicknameKeyword {

            @Nested
            class AndFirstUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.NICKNAME.toString(),
                        "searchKeyword", givenNickname,
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 1L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(1));
                    assertPostResponse(user2, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.NICKNAME.toString(),
                        "searchKeyword", someString(10),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 0L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(0));
                }
            }
        }

        @Nested
        class ByEmailKeyword {

            @Nested
            class AndFirstUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.EMAIL.toString(),
                        "searchKeyword", givenEmail,
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 1L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(1));
                    assertPostResponse(user3, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.EMAIL.toString(),
                        "searchKeyword", someString(10),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 0L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(0));
                }
            }
        }

        @Nested
        class ByPhoneNumberKeyword {

            @Nested
            class AndFirstUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.PHONE_NUMBER.toString(),
                        "searchKeyword", givenPhoneNumber,
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 1L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(1));
                    assertPostResponse(user4, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", UserSearchType.PHONE_NUMBER.toString(),
                        "searchKeyword", somePhoneNumber(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 0L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(0));
                }
            }
        }
    }

    @Nested
    class WhenFilteringUsers {
        @Nested
        class OnlyAdmin {
            private User adminUser;

            @Nested
            class AndFirstUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "filterType", UserFilterType.ADMIN.toString(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );

                    adminUser = itUtil.createAdminUser();
                    usersTestHolder.addOrSet(adminUser);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();
                    final long totalElements = usersTestHolder.getItems().stream().filter(ActUser::isAdmin).count();

                    assertPaging(paging, totalElements, getSortsOrDefault(params));
                    assertThat(userResponses.size(), both(greaterThanOrEqualTo(1)).and(lessThanOrEqualTo(SIZE)));
                    assertPostResponse(adminUser, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                private long totalElementsOfExistingAdminUsers;

                @BeforeEach
                void setUp() {
                    totalElementsOfExistingAdminUsers = usersTestHolder.getItems().stream().filter(ActUser::isAdmin).count();
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "filterType", UserFilterType.ADMIN.toString(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, totalElementsOfExistingAdminUsers, getSortsOrDefault(params));
                    if (totalElementsOfExistingAdminUsers > 0) {
                        assertThat(userResponses.size(), both(greaterThanOrEqualTo(1)).and(lessThanOrEqualTo(SIZE)));
                    } else {
                        assertThat(userResponses.size(), is(0));
                    }
                }
            }
        }

        @Nested
        class OnlyAcceptorUser {

            @Nested
            class AndFirstUser {

                private User user6;

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "filterType", UserFilterType.ACCEPTOR_USER.toString(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );

                    user6 = itUtil.createAcceptorUser();
                    usersTestHolder.addOrSet(user6);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();
                    final long totalElements = usersTestHolder.getItems().stream().filter(ActUser::isAcceptor).count();

                    assertPaging(paging, totalElements, getSortsOrDefault(params));
                    assertThat(userResponses.size(), both(greaterThanOrEqualTo(1)).and(lessThanOrEqualTo(SIZE)));
                    assertPostResponse(user6, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                private long totalElementsOfExistingAcceptorUsers;

                @BeforeEach
                void setUp() {
                    totalElementsOfExistingAcceptorUsers = usersTestHolder.getItems().stream().filter(ActUser::isAcceptor).count();
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "filterType", UserFilterType.ACCEPTOR_USER.toString(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, totalElementsOfExistingAcceptorUsers, getSortsOrDefault(params));
                    if (totalElementsOfExistingAcceptorUsers > 0) {
                        assertThat(userResponses.size(), both(greaterThanOrEqualTo(1)).and(lessThanOrEqualTo(SIZE)));
                    } else {
                        assertThat(userResponses.size(), is(0));
                    }
                }
            }
        }

        @Nested
        class OnlySolidarityLeader {

            @BeforeEach
            void setUp() {
                itUtil.findAllSolidarities()
                    .forEach(solidarity -> {
                        solidarity.setStatus(Status.DELETED);
                        itUtil.updateSolidarity(solidarity);
                    });
            }

            @Nested
            class AndFirstUser {
                private User user7;

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "filterType", UserFilterType.SOLIDARITY_LEADER.toString(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );

                    user7 = itUtil.createUser();
                    usersTestHolder.addOrSet(user7);
                    final Stock stock = itUtil.createStock();
                    final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
                    itUtil.createSolidarityLeader(solidarity, user7.getId());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 1L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(1));
                    assertPostResponse(user7, userResponses.get(0));
                }
            }

            @Nested
            class AndNotFoundUser {

                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "filterType", UserFilterType.SOLIDARITY_LEADER.toString(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString()
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetUserResponse result) {
                    final Paging paging = result.getPaging();
                    final List<UserResponse> userResponses = result.getData();

                    assertPaging(paging, 0L, getSortsOrDefault(params));
                    assertThat(userResponses.size(), is(0));
                }
            }
        }
    }

    private GetUserResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetUserResponse.class
        );
    }

    private void assertPostResponse(User user, UserResponse userResponse) {
        assertThat(userResponse.getId(), is(user.getId()));
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertThat(userResponse.getGender(), is(user.getGender()));
        assertThat(userResponse.getMySpeech(), is(user.getMySpeech()));
        assertThat(DateTimeConverter.convert(userResponse.getBirthDate()), is(user.getBirthDate()));
        assertThat(userResponse.getIsAgreeToReceiveMail(), is(user.getIsAgreeToReceiveMail()));
        assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
        assertThat(userResponse.getJobTitle(), is(user.getJobTitle()));
        assertThat(userResponse.getAddress(), is(user.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(user.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(user.getZipcode()));
        assertThat(userResponse.getTotalAssetAmount(), is(user.getTotalAssetAmount()));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(user.getStatus()));
        assertThat(userResponse.getAuthType(), is(user.getAuthType()));
        assertTime(userResponse.getCreatedAt(), user.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), user.getUpdatedAt());
        assertTime(userResponse.getDeletedAt(), user.getDeletedAt());
        assertThat(userResponse.getIsAdmin(), is(user.isAdmin()));
        assertThat(userResponse.getPhoneNumber(), is(itUtil.decrypt(user.getHashedPhoneNumber())));
    }

    private void assertPaging(Paging paging, long totalElements, Object sorts) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts(), is(sorts));
    }

    private Object getSortsOrDefault(Map<String, Object> params) {
        return params.getOrDefault("sorts", List.of(CREATED_AT_DESC));
    }
}