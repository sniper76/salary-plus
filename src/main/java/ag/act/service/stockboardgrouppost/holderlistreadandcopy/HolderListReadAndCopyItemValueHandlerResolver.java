package ag.act.service.stockboardgrouppost.holderlistreadandcopy;

import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler.HolderListReadAndCopyItemValueHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HolderListReadAndCopyItemValueHandlerResolver {
    private final List<HolderListReadAndCopyItemValueHandler> itemValueHandlerList;

    public HolderListReadAndCopyItemValueHandler resolve(HolderListReadAndCopyItemType itemType) {
        return itemValueHandlerList.stream()
            .filter(handler -> handler.supports(itemType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported item type: " + itemType));
    }
}
