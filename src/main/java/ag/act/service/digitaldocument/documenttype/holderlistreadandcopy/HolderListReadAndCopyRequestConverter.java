package ag.act.service.digitaldocument.documenttype.holderlistreadandcopy;

import ag.act.entity.digitaldocument.HolderListReadAndCopy;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.model.HolderListReadAndCopyItemRequest;
import ag.act.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HolderListReadAndCopyRequestConverter {

    public List<HolderListReadAndCopy> convert(
        Long digitalDocumentId,
        List<HolderListReadAndCopyItemRequest> requests
    ) {
        final HolderListReadAndCopyItemRequestWrapper requestWrapper = getHolderListReadAndCopyItemRequestWrapper(requests);

        return HolderListReadAndCopyItemType.getSortedList()
            .stream()
            .map(type -> convert(digitalDocumentId, type, requestWrapper))
            .toList();
    }

    private HolderListReadAndCopy convert(
        Long digitalDocumentId,
        HolderListReadAndCopyItemType itemType,
        HolderListReadAndCopyItemRequestWrapper requestWrapper
    ) {
        return convert(digitalDocumentId, itemType, requestWrapper.getValidatedItemValue(itemType));
    }

    private HolderListReadAndCopy convert(Long digitalDocumentId, HolderListReadAndCopyItemType itemType, String itemValue) {
        HolderListReadAndCopy holderListReadAndCopy = new HolderListReadAndCopy();
        holderListReadAndCopy.setDigitalDocumentId(digitalDocumentId);
        holderListReadAndCopy.setStatus(Status.ACTIVE);
        holderListReadAndCopy.setItemType(itemType);
        holderListReadAndCopy.setItemValue(itemValue);

        return holderListReadAndCopy;
    }

    private HolderListReadAndCopyItemRequestWrapper getHolderListReadAndCopyItemRequestWrapper(
        List<HolderListReadAndCopyItemRequest> itemRequests
    ) {

        final Map<HolderListReadAndCopyItemType, String> requestMap = new HashMap<>();

        itemRequests.forEach(itemRequest -> {
            final HolderListReadAndCopyItemType type = getItemType(itemRequest);
            requestMap.put(type, itemRequest.getItemValue());
        });

        return new HolderListReadAndCopyItemRequestWrapper(requestMap);
    }

    private HolderListReadAndCopyItemType getItemType(HolderListReadAndCopyItemRequest itemRequest) {
        return HolderListReadAndCopyItemType.fromValue(itemRequest.getItemType());
    }


    @SuppressWarnings("InnerClassMayBeStatic")
    @RequiredArgsConstructor
    private class HolderListReadAndCopyItemRequestWrapper {
        private final Map<HolderListReadAndCopyItemType, String> itemRequestMap;

        public String getValidatedItemValue(HolderListReadAndCopyItemType itemType) {
            return itemType.getFormattedValue(itemRequestMap.get(itemType));
        }
    }
}
