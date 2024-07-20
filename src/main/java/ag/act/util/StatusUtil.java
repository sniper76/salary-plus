package ag.act.util;

import ag.act.entity.ActEntity;
import ag.act.model.Status;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.model.Status.INACTIVE_BY_ADMIN;
import static ag.act.model.Status.INACTIVE_BY_USER;

public class StatusUtil {
    private static final EnumSet<Status> POST_EXCLUDED_STATUSES = EnumSet.of(
        Status.INACTIVE_BY_USER,
        Status.INACTIVE_BY_ADMIN,
        Status.DELETED_BY_ADMIN,
        Status.DELETED
    );
    private static final List<Status> POST_STATUSES = EnumSet.complementOf(POST_EXCLUDED_STATUSES).stream().toList();
    private static final List<Status> STATUSES_FOR_ACTIVE_POST_LIST = List.of(Status.ACTIVE);
    private static final List<Status> DELETED_AND_WITHDRAWAL_STATUSES = Stream.concat(
        getDeleteStatuses().stream(),
        Stream.of(Status.WITHDRAWAL_REQUESTED)
    ).toList();

    public static List<Status> getDeleteStatuses() {
        return List.of(Status.DELETED_BY_ADMIN, Status.DELETED_BY_USER);
    }

    public static List<Status> getDeletedStatusesForPostDetails() {
        return List.of(
            Status.DELETED_BY_ADMIN,
            Status.DELETED
        );
    }

    public static List<Status> getDeleteStatusesForUserList() {
        return Stream.concat(
            getDeleteStatuses().stream(),
            Stream.of(Status.PROCESSING)
        ).toList();
    }

    public static boolean isDeletedOrWithdrawal(Status status) {
        return DELETED_AND_WITHDRAWAL_STATUSES.contains(status);
    }

    public static boolean isDeletedContent(Status status) {
        return getDeleteStatuses().contains(status);
    }

    public static boolean isActive(ActEntity actEntity) {
        return actEntity.getStatus() == Status.ACTIVE;
    }

    public static List<Status> getStatusesForPostList() {
        return getStatusesForPostList(Boolean.FALSE);
    }

    public static List<Status> getStatusesForPostList(boolean isNotDeleted) {
        return isNotDeleted ? STATUSES_FOR_ACTIVE_POST_LIST : POST_STATUSES;
    }

    public static List<Status> getPossibleUpdateStatusesForAdmin() {
        return List.of(Status.ACTIVE, Status.DELETED_BY_ADMIN);
    }

    public static List<Status> getDeletedStatusesForComment() {
        return List.of(Status.DELETED);
    }

    public static List<Status> getInactiveStatuses() {
        return List.of(INACTIVE_BY_ADMIN, INACTIVE_BY_USER);
    }
}
