package ag.act.api.admin.apppreference;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.AppPreference;
import ag.act.entity.User;
import ag.act.model.AppPreferenceDataResponse;
import ag.act.model.AppPreferenceResponse;
import ag.act.model.Paging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetAllAppPreferenceApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/app-preferences";

    private Map<String, Object> params;
    private Integer pageNumber;
    private Long totalElements;
    private String jwt;
    private List<AppPreference> appPreferenceList;


    @BeforeEach
    void setUp() {
        itUtil.init();
        appPreferenceList = itUtil.findAllAppPreferences();
        appPreferenceList.sort(Comparator.comparing(AppPreference::getCreatedAt, Comparator.reverseOrder()));
        totalElements = (long) appPreferenceList.size();

        final User currentAdminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(currentAdminUser.getId());
    }

    @Nested
    class FirstPage {

        @BeforeEach
        void setUp() {
            pageNumber = PAGE_1;
            params = Map.of(
                "page", pageNumber.toString(),
                "size", SIZE.toString()
            );
        }

        @Test
        void shouldReturnAppPreference() throws Exception {
            AppPreferenceDataResponse response = callApiAndGetResult();

            assertPaging(response.getPaging(), totalElements);
            assertResponse(response.getData());
        }
    }

    private AppPreferenceDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            AppPreferenceDataResponse.class
        );
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    private void assertResponse(List<AppPreferenceResponse> results) {
        final AppPreferenceResponse appPreferenceResponse1 = results.get(0);
        final AppPreference appPreference1 = appPreferenceList.get(0);

        assertAppPreferenceResponse(appPreferenceResponse1, appPreference1);

        final AppPreferenceResponse appPreferenceResponse2 = results.get(1);
        final AppPreference appPreference2 = appPreferenceList.get(1);
        assertAppPreferenceResponse(appPreferenceResponse2, appPreference2);
    }

    private void assertAppPreferenceResponse(AppPreferenceResponse appPreferenceResponse, AppPreference appPreference) {
        assertThat(appPreferenceResponse.getId(), is(appPreference.getId()));
        assertThat(appPreferenceResponse.getAppPreferenceType(), is(appPreference.getType().name()));
        assertThat(appPreferenceResponse.getValue(), is(appPreference.getValue()));
        assertThat(appPreferenceResponse.getCreatedBy(), is(appPreference.getCreatedBy()));
        assertThat(appPreferenceResponse.getUpdatedBy(), is(appPreference.getUpdatedBy()));
        assertTime(appPreferenceResponse.getCreatedAt(), appPreference.getCreatedAt());
        assertTime(appPreferenceResponse.getUpdatedAt(), appPreference.getUpdatedAt());
    }
}
