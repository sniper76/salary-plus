package ag.act.handler.admin;

import ag.act.api.AdminAppPreferenceApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.facade.AppPreferenceFacade;
import ag.act.model.AppPreferenceDataResponse;
import ag.act.model.AppPreferenceResponse;
import ag.act.model.AppPreferenceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@UseGuards(IsAdminGuard.class)
@RequiredArgsConstructor
public class AdminAppPreferenceApiDelegateImpl implements AdminAppPreferenceApiDelegate {

    private final AppPreferenceFacade appPreferenceFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<AppPreferenceDataResponse> getAppPreferences(Integer page, Integer size, List<String> sorts) {
        PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);

        SimplePageDto<AppPreferenceResponse> appPreferences = appPreferenceFacade.getAppPreferences(pageRequest);
        return ResponseEntity.ok(pageDataConverter.convert(appPreferences, AppPreferenceDataResponse.class));
    }

    @Override
    public ResponseEntity<AppPreferenceResponse> getAppPreferenceDetails(Long appPreferenceId) {
        return ResponseEntity.ok(appPreferenceFacade.getAppPreferenceDetails(appPreferenceId));
    }

    @Override
    public ResponseEntity<AppPreferenceResponse> updateAppPreference(
        Long appPreferenceId,
        AppPreferenceUpdateRequest appPreferenceUpdateRequest
    ) {
        return ResponseEntity.ok(appPreferenceFacade.updateAppPreference(appPreferenceId, appPreferenceUpdateRequest));
    }
}
