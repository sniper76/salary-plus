package ag.act.enums.solidarity.election;

import java.util.Arrays;
import java.util.List;

public enum SolidarityLeaderElectionApplyStatus {

    SAVE,
    COMPLETE,
    DELETED_BY_USER,
    DELETED_BY_ADMIN,
    EXPIRED;

    public static SolidarityLeaderElectionApplyStatus fromValue(String value) {
        return Arrays.stream(values())
            .filter(v -> v.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static boolean isSave(SolidarityLeaderElectionApplyStatus status) {
        return SAVE == status;
    }

    public static List<SolidarityLeaderElectionApplyStatus> getApplyStatuses() {
        return Arrays.asList(SAVE, COMPLETE);
    }

    public static List<SolidarityLeaderElectionApplyStatus> getAllStatuses() {
        return Arrays.stream(SolidarityLeaderElectionApplyStatus.values())
            .toList();
    }
}
