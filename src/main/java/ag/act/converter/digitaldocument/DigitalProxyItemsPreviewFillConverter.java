package ag.act.converter.digitaldocument;

import ag.act.model.DigitalDocumentItemRequest;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentItemFill;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DigitalProxyItemsPreviewFillConverter {

    public List<DigitalDocumentItemFill> convert(List<DigitalDocumentItemRequest> items) {
        if (items == null) {
            return Collections.emptyList();
        }

        List<DigitalDocumentItemFill> result = new ArrayList<>();

        for (DigitalDocumentItemRequest item : items) {
            DigitalDocumentItemFill digitalDocumentItemFill = new DigitalDocumentItemFill();

            digitalDocumentItemFill.setContent(item.getContent());
            digitalDocumentItemFill.setTitle(item.getTitle());
            digitalDocumentItemFill.setIsLastItem(
                hasChildItems(item) ? Boolean.FALSE : Boolean.TRUE
            );
            digitalDocumentItemFill.setSelectValue(item.getDefaultSelectValue());

            result.add(digitalDocumentItemFill);

            if (hasChildItems(item)) {
                result.addAll(convert(item.getChildItems()));
            }
        }

        return result;
    }

    private boolean hasChildItems(DigitalDocumentItemRequest item) {
        return item.getChildItems() != null && !item.getChildItems().isEmpty();
    }
}
