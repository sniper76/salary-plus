package ag.act.util;

import java.util.UUID;

@SuppressWarnings("AbbreviationAsWordInName")
public class UUIDUtil {

    public static UUID nameUUIDFromString(String input) {
        return UUID.nameUUIDFromBytes(input.getBytes());
    }

    public static UUID randomUUID() {
        return UUID.randomUUID();
    }
}
