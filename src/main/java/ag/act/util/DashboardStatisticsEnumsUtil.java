package ag.act.util;

import ag.act.model.Status;

import java.util.List;

public class DashboardStatisticsEnumsUtil {
    public static List<Status> getUserRegistrationStatuses() {
        return List.of(Status.ACTIVE, Status.WITHDRAWAL_REQUESTED);
    }

    public static List<Status> getUserWithdrawalStatuses() {
        return List.of(Status.DELETED_BY_ADMIN, Status.DELETED_BY_USER, Status.DELETED);
    }
}
