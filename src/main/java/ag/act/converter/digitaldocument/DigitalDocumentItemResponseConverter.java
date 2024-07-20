package ag.act.converter.digitaldocument;

import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.model.DigitalDocumentItemResponse;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentItemResponseConverter {
    public DigitalDocumentItemResponse convert(DigitalDocumentItem item) {
        return new DigitalDocumentItemResponse()
            .id(item.getId())
            .title(item.getTitle())
            .content(item.getContent())
            .defaultSelectValue(item.getDefaultSelectValue() == null ? null : item.getDefaultSelectValue().name())
            .leaderDescription(item.getLeaderDescription())
            ;
    }
}
