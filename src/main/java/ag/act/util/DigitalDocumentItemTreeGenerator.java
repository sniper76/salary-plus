package ag.act.util;

import ag.act.converter.digitaldocument.DigitalDocumentItemResponseConverter;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.model.DigitalDocumentItemResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DigitalDocumentItemTreeGenerator {
    private final DigitalDocumentItemResponseConverter digitalDocumentItemResponseConverter;

    public DigitalDocumentItemTreeGenerator(DigitalDocumentItemResponseConverter digitalDocumentItemResponseConverter) {
        this.digitalDocumentItemResponseConverter = digitalDocumentItemResponseConverter;
    }

    public List<DigitalDocumentItemResponse> buildTree(List<DigitalDocumentItem> items) {

        final Map<Long, List<DigitalDocumentItem>> parentIdMap = items.stream()
            .filter(this::isChild)
            .collect(Collectors.groupingBy(DigitalDocumentItem::getParentId));

        return items.stream()
            .filter(this::isParent)
            .map(item -> buildTree(convertToResponse(item), parentIdMap))
            .toList();
    }

    private DigitalDocumentItemResponse buildTree(
        DigitalDocumentItemResponse parent,
        Map<Long, List<DigitalDocumentItem>> parentIdMap
    ) {

        final List<DigitalDocumentItem> children = parentIdMap.get(parent.getId());
        if (CollectionUtils.isEmpty(children)) {
            return parent;
        }

        parent.setChildItems(
            children.stream()
                .map(item -> buildTree(convertToResponse(item), parentIdMap))
                .toList()
        );

        return parent;
    }

    private boolean isParent(DigitalDocumentItem digitalDocumentItem) {
        return digitalDocumentItem.getParentId() == null;
    }

    private boolean isChild(DigitalDocumentItem digitalDocumentItem) {
        return !isParent(digitalDocumentItem);
    }

    private DigitalDocumentItemResponse convertToResponse(DigitalDocumentItem item) {
        return digitalDocumentItemResponseConverter.convert(item);
    }
}
