package ag.act.dto;

import ag.act.enums.DigitalAnswerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalDocumentItemWithAnswerDto {
    private String title;
    private String content;
    private DigitalAnswerType selectValue;
    private Boolean isLastItem;

    public DigitalDocumentItemWithAnswerDto(
        String title,
        String content,
        DigitalAnswerType selectValue,
        Boolean isLastItem
    ) {
        this.title = title;
        this.content = content;
        this.selectValue = selectValue;
        this.isLastItem = isLastItem;
    }
}
