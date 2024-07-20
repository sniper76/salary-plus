package ag.act.service.digitaldocument;

import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.enums.DigitalAnswerType;
import ag.act.model.DigitalDocumentItemRequest;
import ag.act.repository.DigitalDocumentItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DigitalDocumentItemService {
    private final DigitalDocumentItemRepository digitalDocumentItemRepository;

    public DigitalDocumentItemService(DigitalDocumentItemRepository digitalDocumentItemRepository) {
        this.digitalDocumentItemRepository = digitalDocumentItemRepository;
    }

    public DigitalDocumentItem save(DigitalDocumentItem digitalDocumentItem) {
        return digitalDocumentItemRepository.save(digitalDocumentItem);
    }

    public List<DigitalDocumentItem> findDigitalDocumentItemsByDigitalDocumentId(Long digitalDocumentId) {
        return digitalDocumentItemRepository.findDigitalDocumentItemsByDigitalDocumentId(digitalDocumentId);
    }

    public void createItems(List<DigitalDocumentItemRequest> childItems, Long digitalDocumentId) {
        processItems(childItems, digitalDocumentId, 0L, 1);
    }

    private void processItems(List<DigitalDocumentItemRequest> items, long digitalDocumentId, long parentId, int level) {
        if (items == null) {
            return;
        }
        for (DigitalDocumentItemRequest item : items) {
            DigitalDocumentItem digitalDocumentItem = new DigitalDocumentItem();
            digitalDocumentItem.setDigitalDocumentId(digitalDocumentId);
            digitalDocumentItem.setTitle(item.getTitle());
            digitalDocumentItem.setContent(item.getContent());
            digitalDocumentItem.setIsLastItem(Boolean.FALSE);
            digitalDocumentItem.setItemLevel(level);
            digitalDocumentItem.setDefaultSelectValue(
                item.getDefaultSelectValue() == null
                    ? null
                    : DigitalAnswerType.fromValue(item.getDefaultSelectValue()));
            save(digitalDocumentItem);

            if (parentId > 0) {
                digitalDocumentItem.setParentId(parentId);
                digitalDocumentItem.setGroupId(parentId);
            } else {
                digitalDocumentItem.setGroupId(digitalDocumentItem.getId());
            }

            if (item.getChildItems() != null && !item.getChildItems().isEmpty()) {
                processItems(item.getChildItems(), digitalDocumentId, digitalDocumentItem.getId(), level + 1);
            } else {
                digitalDocumentItem.setLeaderDescription(item.getLeaderDescription());
                digitalDocumentItem.setIsLastItem(Boolean.TRUE);
            }
        }
    }
}