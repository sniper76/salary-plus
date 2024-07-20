package ag.act.enums.admin;

import lombok.Getter;

@Getter
public enum UpDownType {
    UP("Up"),
    DOWN("Down");

    private final String displayName;

    UpDownType(String displayName) {
        this.displayName = displayName;
    }
}
