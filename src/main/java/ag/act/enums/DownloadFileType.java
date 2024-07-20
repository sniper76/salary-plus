package ag.act.enums;

import lombok.Getter;

@Getter
public enum DownloadFileType {
    ZIP,
    UNKNOWN;

    public static DownloadFileType fromValue(String name) {
        try {
            return DownloadFileType.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

}
