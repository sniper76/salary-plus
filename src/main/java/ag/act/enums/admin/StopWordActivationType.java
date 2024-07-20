package ag.act.enums.admin;

import ag.act.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum StopWordActivationType {
    ALL("전체", List.of(Status.ACTIVE, Status.INACTIVE_BY_ADMIN)),
    ACTIVE("활성화", List.of(Status.ACTIVE)),
    INACTIVE("비활성화", List.of(Status.INACTIVE_BY_ADMIN));

    private final String displayName;
    private final List<Status> statusList;

    public static StopWordActivationType fromValue(String filterTypeName) {
        try {
            return StopWordActivationType.valueOf(filterTypeName.toUpperCase());
        } catch (Exception e) {
            return ALL;
        }
    }

    public static String getDisplayNameByStatus(Status status) {
        if (status == Status.ACTIVE) {
            return ACTIVE.displayName;
        }
        return INACTIVE.displayName;
    }

    public static boolean containsStopWordStatus(Status beforeStatus, Status afterStatus) {
        return ALL.statusList.containsAll(List.of(beforeStatus, afterStatus));
    }
}
