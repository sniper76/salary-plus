package ag.act.api.admin.corporateusers;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.enums.admin.CorporateUserSearchType;
import ag.act.model.CorporateUserDetailsResponse;
import ag.act.model.GetCorporateUserDataResponse;
import ag.act.model.Paging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetCorporateUserListApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/corporate-users";
    private String adminJwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private CorporateUser corporateUser1;
    private CorporateUser corporateUser2;
    private CorporateUser corporateUser3;
    private CorporateUser corporateUser4;
    private CorporateUser corporateUser5;
    private String givenTitle;

    @BeforeEach
    void setUp() {
        adminJwt = itUtil.createJwt(itUtil.createAdminUser().getId());
        givenTitle = someAlphanumericString(10);

        corporateUser1 = createCorporateUser("123-12-12341", someAlphanumericString(2) + givenTitle);
        corporateUser2 = createCorporateUser("123-12-12342", someAlphanumericString(2));
        corporateUser3 = createCorporateUser("123-12-12343", someAlphanumericString(2));
        corporateUser4 = createCorporateUser("123-12-12344", someAlphanumericString(2));
        corporateUser5 = createCorporateUser("123-12-12345", someAlphanumericString(2));
    }

    private GetCorporateUserDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetCorporateUserDataResponse.class
        );
    }

    private CorporateUser createCorporateUser(String corporateNo, String corporateName) {
        return itUtil.createCorporateUser(corporateNo, corporateName);
    }

    private Map<String, Object> createParamsMap(CorporateUserSearchType searchType, String searchKeyword) {
        return Map.of(
            "searchType", searchType.name(),
            "searchKeyword", searchKeyword,
            "page", pageNumber.toString(),
            "size", SIZE.toString(),
            "sorts", "createdAt:desc"
        );
    }

    private void assertCorporateUserResponse(
        CorporateUser corporateUser, CorporateUserDetailsResponse campaignResponse
    ) {
        assertThat(campaignResponse.getId(), is(corporateUser.getId()));
        assertThat(campaignResponse.getCorporateNo(), is(corporateUser.getCorporateNo()));
        assertThat(campaignResponse.getCorporateName(), is(corporateUser.getCorporateName()));
        assertThat(campaignResponse.getStatus(), is(corporateUser.getStatus()));
        assertTime(campaignResponse.getCreatedAt(), corporateUser.getCreatedAt());
        assertTime(campaignResponse.getUpdatedAt(), corporateUser.getUpdatedAt());
        assertTime(campaignResponse.getDeletedAt(), corporateUser.getDeletedAt());
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    @Nested
    class WhenSearchAllCorporateUsers {
        @Nested
        class AndFirstPage {
            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = createParamsMap(CorporateUserSearchType.CORPORATE_NAME, "");
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnCampaigns() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCorporateUserDataResponse result) {
                final List<CorporateUserDetailsResponse> data = result.getData();

                assertThat(data.size(), is(SIZE));
                assertCorporateUserResponse(corporateUser5, data.get(0));
                assertCorporateUserResponse(corporateUser4, data.get(1));
            }
        }

        @Nested
        class AndSecondPage {
            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = createParamsMap(CorporateUserSearchType.CORPORATE_NAME, "");
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCorporateUserDataResponse result) {
                final List<CorporateUserDetailsResponse> data = result.getData();

                assertThat(data.size(), is(SIZE));
                assertCorporateUserResponse(corporateUser3, data.get(0));
                assertCorporateUserResponse(corporateUser2, data.get(1));
            }
        }
    }

    @Nested
    class WhenSearchCorporateNameKeyword {

        @Nested
        class AndFirstCorporateUser {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = createParamsMap(CorporateUserSearchType.CORPORATE_NAME, givenTitle);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCorporateUserDataResponse result) {
                final Paging paging = result.getPaging();
                final List<CorporateUserDetailsResponse> data = result.getData();

                assertPaging(paging, 1L);
                assertThat(data.size(), is(1));
                assertCorporateUserResponse(corporateUser1, data.get(0));
            }
        }

        @Nested
        class AndNotFoundCorporateUser {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = createParamsMap(CorporateUserSearchType.CORPORATE_NAME, someString(30));
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCorporateUserDataResponse result) {
                final Paging paging = result.getPaging();
                final List<CorporateUserDetailsResponse> data = result.getData();

                assertPaging(paging, 0L);
                assertThat(data.size(), is(0));
            }
        }
    }
}
