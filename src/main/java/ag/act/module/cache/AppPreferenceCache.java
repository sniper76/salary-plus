package ag.act.module.cache;

import ag.act.entity.AppPreference;
import ag.act.enums.AppPreferenceType;
import ag.act.service.AppPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AppPreferenceCache {
    private final AppPreferenceService appPreferenceService;

    private final Map<AppPreferenceType, String> appPreferenceMap = new HashMap<>();

    public void load() {
        appPreferenceService.findAll()
            .forEach(this::setAppPreference);
    }

    private void setAppPreference(AppPreference appPreference) {
        appPreferenceMap.put(appPreference.getType(), appPreference.getValue());
    }

    public <T> T getValue(AppPreferenceType appPreferenceType) {
        return appPreferenceType.getValue(appPreferenceMap.get(appPreferenceType));
    }
}
