package ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler;

import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyItemWithValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceDateItemValueHandler implements HolderListReadAndCopyItemValueHandler {
    @Override
    public HolderListReadAndCopyItemType getItemType() {
        return HolderListReadAndCopyItemType.REFERENCE_DATE_BY_LEADER;
    }

    @Override
    public boolean supports(HolderListReadAndCopyItemType itemType) {
        return getItemType() == itemType;
    }

    @Override
    public HolderListReadAndCopyItemWithValue handle(String stockCode) {
        return HolderListReadAndCopyItemWithValue.of(
            getItemType(),
            "3월말", "6월말", "9월말", "12월말"
        );
    }
}
