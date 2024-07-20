package ag.act.converter;

import ag.act.entity.AppPreference;
import ag.act.model.AppPreferenceResponse;
import org.springframework.stereotype.Service;

@Service
public class AppPreferenceResponseConverter {

    public AppPreferenceResponse convert(AppPreference appPreference) {
        return new AppPreferenceResponse()
            .id(appPreference.getId())
            .appPreferenceType(appPreference.getType().name())
            .value(appPreference.getValue())
            .createdBy(appPreference.getCreatedBy())
            .updatedBy(appPreference.getUpdatedBy())
            .createdAt(DateTimeConverter.convert(appPreference.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(appPreference.getUpdatedAt()));
    }
}
