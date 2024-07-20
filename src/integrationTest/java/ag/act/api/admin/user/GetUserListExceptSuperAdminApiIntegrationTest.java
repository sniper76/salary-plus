package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.enums.admin.UserSearchType;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someEmail;
import static ag.act.TestUtil.somePhoneNumber;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetUserListExceptSuperAdminApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users";
    private String adminJwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user8;

    private String givenName;
    private String givenNickname;
    private String givenEmail;
    private String givenPhoneNumber;

    @BeforeEach
    void setUp() throws Exception {
        itUtil.init();

        givenName = someAlphanumericString(10);
        givenNickname = someAlphanumericString(20);
        givenEmail = someEmail();
        givenPhoneNumber = somePhoneNumber();
        user1 = itUtil.createUserWithGivenName(someString(2) + givenName);
        user2 = itUtil.createUserWithGivenNickname(someString(2) + givenNickname);
        user3 = itUtil.createUserWithGivenEmail(someString(2) + givenEmail);
        user4 = itUtil.createUserWithPhoneNumber(givenPhoneNumber);
        user5 = itUtil.createAdminUser();

        itUtil.createUserWithGivenNameAndStatus(someString(2) + givenName, Status.DELETED_BY_USER);
        itUtil.createUserWithGivenNameAndStatus(someString(3) + givenName, Status.DELETED_BY_ADMIN);
        user8 =  itUtil.createUserWithGivenNameAndStatus(someString(4) + givenName, Status.ACTIVE);
        itUtil.createUserWithGivenNameAndStatus(someString(3) + givenName, Status.PROCESSING);

        itUtil.createSuperAdminUser();

        adminJwt = itUtil.createJwt(user5.getId());
    }

    private ag.act.model.GetUserResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetUserResponse.class
        );
    }

    private void assertPostResponse(User user, ag.act.model.UserResponse userResponse) {
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

    private void assertPaging(ag.act.model.Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    @Nested
    class WhenSearchAllUsers {

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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertThat(userResponses.size(), is(SIZE));
                assertPostResponse(user8, userResponses.get(0));
                assertPostResponse(user5, userResponses.get(1));
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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertThat(userResponses.size(), is(SIZE));
                assertPostResponse(user4, userResponses.get(0));
                assertPostResponse(user3, userResponses.get(1));
            }
        }
    }

    @Nested
    class WhenSearchNameKeyword {

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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 2L);
                assertThat(userResponses.size(), is(2));
                assertPostResponse(user8, userResponses.get(0));
                assertPostResponse(user1, userResponses.get(1));
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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(userResponses.size(), is(0));
            }
        }
    }

    @Nested
    class WhenSearchNicknameKeyword {

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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 1L);
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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(userResponses.size(), is(0));
            }
        }
    }

    @Nested
    class WhenSearchEmailKeyword {

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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 1L);
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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(userResponses.size(), is(0));
            }
        }
    }

    @Nested
    class WhenSearchPhoneNumber {

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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 1L);
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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(userResponses.size(), is(0));
            }
        }
    }

    @Nested
    class WhenSearchExceptCorporateUser {
        @BeforeEach
        void setUp() {
            final User user = itUtil.createUser();
            final CorporateUser corporateUser = itUtil.createCorporateUser(someString(10), someString(10));
            corporateUser.setUserId(user.getId());
            itUtil.updateCorporateUser(corporateUser);
        }

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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertThat(userResponses.size(), is(SIZE));
                assertPostResponse(user8, userResponses.get(0));
                assertPostResponse(user5, userResponses.get(1));
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

            private void assertResponse(ag.act.model.GetUserResponse result) {
                final List<ag.act.model.UserResponse> userResponses = result.getData();

                assertThat(userResponses.size(), is(SIZE));
                assertPostResponse(user4, userResponses.get(0));
                assertPostResponse(user3, userResponses.get(1));
            }
        }
    }
}
