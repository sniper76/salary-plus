package ag.act.dto;

import ag.act.enums.AppPreferenceType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Getter
public class AppVersion implements Comparable<AppVersion> {
    private final int major;
    private final int minor;
    private final int patch;

    public AppVersion(String versionInput) {

        final String[] versionParts = getValidVersionString(versionInput).split("\\.");

        major = Integer.parseInt(versionParts[0]);
        minor = Integer.parseInt(versionParts[1]);
        patch = Integer.parseInt(versionParts[2]);
    }

    private String getValidVersionString(String versionInput) {
        final AppPreferenceType minAppVersionPreference = AppPreferenceType.MIN_APP_VERSION;
        final String defaultVersionString = minAppVersionPreference.getDefaultValue();
        final String versionString = getVersionString(versionInput, defaultVersionString);

        if (versionString.matches(minAppVersionPreference.getValuePattern())) {
            return versionString;
        }

        log.error("Invalid AppVersionString: {}", versionString);

        return defaultVersionString;
    }

    private String getVersionString(String versionInput, String defaultVersionString) {
        if (StringUtils.isBlank(versionInput)) {
            return defaultVersionString;
        }

        return versionInput.trim();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(AppVersion other) {
        if (other == null) {
            return 1;
        }

        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }

        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }

        return Integer.compare(this.patch, other.patch);
    }

    public static AppVersion of(String versionString) {
        return new AppVersion(versionString);
    }
}
