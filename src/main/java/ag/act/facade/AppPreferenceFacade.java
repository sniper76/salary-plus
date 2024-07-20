package ag.act.facade;

import ag.act.converter.AppPreferenceResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.entity.AppPreference;
import ag.act.model.AppPreferenceResponse;
import ag.act.model.AppPreferenceUpdateRequest;
import ag.act.service.AppPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppPreferenceFacade {

    private final AppPreferenceService appPreferenceService;
    private final AppPreferenceResponseConverter appPreferenceResponseConverter;

    public SimplePageDto<AppPreferenceResponse> getAppPreferences(PageRequest pageRequest) {
        Page<AppPreference> allAppPreferences = appPreferenceService.getAllAppPreferences(pageRequest);
        return new SimplePageDto<>(allAppPreferences.map(appPreferenceResponseConverter::convert));
    }

    public AppPreferenceResponse getAppPreferenceDetails(Long appPreferenceId) {
        return appPreferenceResponseConverter.convert(appPreferenceService.getAppPreferenceDetails(appPreferenceId));
    }

    public AppPreferenceResponse updateAppPreference(Long appPreferenceId, AppPreferenceUpdateRequest request) {
        return appPreferenceResponseConverter.convert(appPreferenceService.updateAppPreference(appPreferenceId, request));
    }
}
