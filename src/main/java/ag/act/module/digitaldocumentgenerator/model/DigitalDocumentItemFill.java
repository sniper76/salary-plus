package ag.act.module.digitaldocumentgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DigitalDocumentItemFill {
    private Boolean isLastItem;
    private String title;
    private String content;
    private String selectValue;
}
