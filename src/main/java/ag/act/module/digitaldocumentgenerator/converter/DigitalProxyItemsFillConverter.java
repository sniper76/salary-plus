package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.dto.DigitalDocumentItemWithAnswerDto;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentItemFill;
import ag.act.repository.DigitalDocumentItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DigitalProxyItemsFillConverter {
    private final DigitalDocumentItemRepository digitalDocumentItemRepository;

    public DigitalProxyItemsFillConverter(DigitalDocumentItemRepository digitalDocumentItemRepository) {
        this.digitalDocumentItemRepository = digitalDocumentItemRepository;
    }

    public List<DigitalDocumentItemFill> convert(DigitalDocumentUser digitalDocumentUser) {
        final List<DigitalDocumentItemWithAnswerDto> everyItemsWithAnswerByUser =
            digitalDocumentItemRepository.findEveryItemsWithAnswerByUser(
                digitalDocumentUser.getDigitalDocumentId(),
                digitalDocumentUser.getUserId()
            );

        return everyItemsWithAnswerByUser.stream().map(
            dto -> {
                DigitalDocumentItemFill digitalDocumentItemFill = new DigitalDocumentItemFill();

                digitalDocumentItemFill.setContent(dto.getContent());
                digitalDocumentItemFill.setTitle(dto.getTitle());
                digitalDocumentItemFill.setIsLastItem(dto.getIsLastItem());
                if (dto.getSelectValue() != null) {
                    digitalDocumentItemFill.setSelectValue(dto.getSelectValue().name());
                }

                return digitalDocumentItemFill;
            }
        ).toList();
    }
}
