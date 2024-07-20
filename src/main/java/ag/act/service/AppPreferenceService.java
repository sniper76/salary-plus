package ag.act.service;

import ag.act.entity.AppPreference;
import ag.act.enums.AppPreferenceType;
import ag.act.exception.BadRequestException;
import ag.act.model.AppPreferenceUpdateRequest;
import ag.act.repository.AppPreferenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AppPreferenceService {
    private final AppPreferenceRepository appPreferenceRepository;

    public List<AppPreference> findAll() {
        return appPreferenceRepository.findAll();
    }

    public Page<AppPreference> getAllAppPreferences(PageRequest pageRequest) {
        return appPreferenceRepository.findAll(pageRequest);
    }

    public AppPreference getAppPreferenceDetails(Long appPreferenceId) {
        return getAppPreference(appPreferenceId);
    }

    private AppPreference getAppPreference(Long appPreferenceId) {
        return appPreferenceRepository.findById(appPreferenceId)
            .orElseThrow(() -> new BadRequestException("App Preference 정보를 찾을 수 없습니다."));
    }

    public AppPreference updateAppPreference(Long appPreferenceId, AppPreferenceUpdateRequest request) {
        final AppPreference appPreference = getAppPreference(appPreferenceId);

        final String currentValueInput = request.getCurrentValue();
        final String newValueInput = request.getNewValue().trim();
        validateUpdateRequest(currentValueInput, newValueInput, appPreference);

        appPreference.setValue(newValueInput);

        return appPreferenceRepository.save(appPreference);
    }

    private void validateUpdateRequest(
        String currentValueInput,
        String newValueInput,
        AppPreference appPreference
    ) {
        final String currentValue = appPreference.getValue();
        final AppPreferenceType type = appPreference.getType();

        if (!currentValue.equals(currentValueInput) || !type.isValueMatches(newValueInput)) {
            throw new BadRequestException("App Preference 값을 업데이트 할 수 없습니다. 값을 다시 확인해주세요.");
        }
    }
}
