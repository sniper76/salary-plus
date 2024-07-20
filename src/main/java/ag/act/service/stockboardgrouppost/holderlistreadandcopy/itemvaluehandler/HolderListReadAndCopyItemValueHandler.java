package ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler;

import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyItemWithValue;

public interface HolderListReadAndCopyItemValueHandler {

    HolderListReadAndCopyItemType getItemType();

    boolean supports(HolderListReadAndCopyItemType itemType);

    HolderListReadAndCopyItemWithValue handle(String stockCode);
}
