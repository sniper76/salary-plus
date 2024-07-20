package ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler;

import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyItemWithValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeadlineDate2ItemValueHandler implements HolderListReadAndCopyItemValueHandler {
    @Override
    public HolderListReadAndCopyItemType getItemType() {
        return HolderListReadAndCopyItemType.DEADLINE_DATE_BY_LEADER2;
    }

    @Override
    public boolean supports(HolderListReadAndCopyItemType itemType) {
        return getItemType() == itemType;
    }

    @Override
    public HolderListReadAndCopyItemWithValue handle(String stockCode) {
        return HolderListReadAndCopyItemWithValue.of(
            getItemType(),
            ""
        );
    }
}
