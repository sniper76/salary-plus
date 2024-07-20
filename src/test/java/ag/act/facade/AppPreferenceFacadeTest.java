package ag.act.facade;

import ag.act.converter.AppPreferenceResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.entity.AppPreference;
import ag.act.model.AppPreferenceResponse;
import ag.act.service.AppPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static ag.act.TestUtil.somePage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;


@MockitoSettings(strictness = Strictness.LENIENT)
class AppPreferenceFacadeTest {

    @InjectMocks
    private AppPreferenceFacade facade;
    @Mock
    private AppPreferenceService appPreferenceService;
    @Mock
    private AppPreferenceResponseConverter appPreferenceResponseConverter;

    @Nested
    class GetAllAppPreference {

        @Mock
        private Page<AppPreference> appPreferencePage;
        @SuppressWarnings("rawtypes")
        private Page mappedAppPreferencePage;
        @Mock
        private PageRequest pageRequest;
        private SimplePageDto<AppPreferenceResponse> actualResponse;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void setUp() {
            mappedAppPreferencePage = somePage(List.<AppPreference>of());

            given(appPreferenceService.getAllAppPreferences(pageRequest)).willReturn(appPreferencePage);
            given(appPreferencePage.getContent()).willReturn(List.of());
            given(appPreferencePage.map(any())).willReturn(mappedAppPreferencePage);
            given(appPreferenceResponseConverter.convert(any(AppPreference.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
            actualResponse = facade.getAppPreferences(pageRequest);
        }

        @Test
        void shouldReturnSuccess() {

            assertThat(actualResponse.getContent(), is(mappedAppPreferencePage.getContent()));
        }

        @Test
        void shouldCallAppPreferenceServiceGetAppPreferences() {
            then(appPreferenceService).should().getAllAppPreferences(pageRequest);
        }
    }

    @Nested
    class GetAppPreferenceDetails {

        @Mock
        private AppPreference appPreference;
        @Mock
        private AppPreferenceResponse appPreferenceResponse;
        private Long appPreferenceId;
        private AppPreferenceResponse actualResponse;

        @BeforeEach
        void setUp() {
            appPreferenceId = someLong();
            given(appPreferenceService.getAppPreferenceDetails(appPreferenceId)).willReturn(appPreference);
            given(appPreferenceResponseConverter.convert(appPreference)).willReturn(appPreferenceResponse);

            actualResponse = facade.getAppPreferenceDetails(appPreferenceId);
        }

        @Test
        void shouldReturnSuccess() {
            assertThat(actualResponse, is(appPreferenceResponse));
        }

        @Test
        void shouldCallGetAppPreferenceDetails() {
            then(appPreferenceService).should().getAppPreferenceDetails(appPreferenceId);
        }
    }

    @Nested
    class WhenUpdate {

        @Mock
        private ag.act.model.AppPreferenceUpdateRequest request;
        @Mock
        private AppPreferenceResponse appPreferenceResponse;
        @Mock
        private AppPreference appPreference;
        private Long appPreferenceId;

        @BeforeEach
        void setUp() {
            appPreferenceId = someLong();
            given(appPreferenceService.updateAppPreference(appPreferenceId, request)).willReturn(appPreference);
            given(appPreferenceResponseConverter.convert(appPreference)).willReturn(appPreferenceResponse);
        }

        @Test
        void shouldReturnSuccess() {
            AppPreferenceResponse response = facade.updateAppPreference(appPreferenceId, request);

            assertThat(response, is(appPreferenceResponse));
        }
    }
}
