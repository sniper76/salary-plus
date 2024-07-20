package ag.act.util;

import java.time.ZoneId;

public class ZoneIdUtil {
    public static ZoneId getSystemZoneId() {
        return ZoneId.systemDefault();
    }

    public static ZoneId getSeoulZoneId() {
        return ZoneId.of("Asia/Seoul");
    }
}
