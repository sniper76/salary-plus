package ag.act.configuration.initial;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.AppPreference;
import ag.act.enums.AppPreferenceType;
import ag.act.module.cache.AppPreferenceCache;
import ag.act.repository.AppPreferenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Order(2)
@Component
@RequiredArgsConstructor
@Transactional
public class AppPreferenceLoader implements InitialLoader {
    private final AppPreferenceRepository appPreferenceRepository;
    private final AppPreferenceCache appPreferenceCache;

    @Override
    public void load() {
        Arrays.stream(AppPreferenceType.values())
            .forEach(this::saveIfNotExist);

        appPreferenceCache.load();
    }

    private void saveIfNotExist(AppPreferenceType type) {
        if (existBy(type)) {
            return;
        }

        AppPreference appPreference = new AppPreference();
        appPreference.setType(type);
        appPreference.setValue(type.getDefaultValue());
        appPreference.setCreatedBy(ActUserProvider.getSystemUserId());
        appPreference.setUpdatedBy(ActUserProvider.getSystemUserId());

        appPreferenceRepository.save(appPreference);
    }

    private boolean existBy(AppPreferenceType type) {
        return appPreferenceRepository.findByType(type).isPresent();
    }
}
