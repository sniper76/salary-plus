package ag.act.enums;

import java.util.EnumSet;
import java.util.Set;

public enum BatchStatus {

    START,
    IN_PROGRESS,
    FAILURE,
    SUCCESS;

    public static Set<BatchStatus> getNotFailureStatuses() {
        final Set<BatchStatus> auditLogStatuses = EnumSet.allOf(BatchStatus.class);
        auditLogStatuses.remove(BatchStatus.FAILURE);
        return auditLogStatuses;
    }
}
