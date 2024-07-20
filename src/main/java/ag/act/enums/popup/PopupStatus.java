package ag.act.enums.popup;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public enum PopupStatus {
    READY,
    PROCESSING,
    COMPLETE,
    ALL;

    public static PopupStatus fromValue(String popupStatusName) {
        try {
            return PopupStatus.valueOf(popupStatusName.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            return ALL;
        }
    }

    public static String fromTargetDatetime(LocalDateTime targetStartDatetime, LocalDateTime targetEndDatetime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(targetStartDatetime)) {
            return PopupStatus.READY.name();
        } else if (now.isAfter(targetEndDatetime)) {
            return PopupStatus.COMPLETE.name();
        } else {
            return PopupStatus.PROCESSING.name();
        }
    }
}
