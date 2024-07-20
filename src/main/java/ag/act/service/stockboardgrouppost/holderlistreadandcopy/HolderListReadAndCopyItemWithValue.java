package ag.act.service.stockboardgrouppost.holderlistreadandcopy;

import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;

import java.util.Arrays;
import java.util.List;

public record HolderListReadAndCopyItemWithValue(HolderListReadAndCopyItemType itemType, List<String> values) {
    public static HolderListReadAndCopyItemWithValue of(HolderListReadAndCopyItemType itemType, String... valueArray) {
        return new HolderListReadAndCopyItemWithValue(
            itemType,
            Arrays.asList(valueArray)
        );
    }
}
