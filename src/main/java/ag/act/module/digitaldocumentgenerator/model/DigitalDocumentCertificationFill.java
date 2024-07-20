package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class DigitalDocumentCertificationFill {
    private GrantorFill grantor;
    private String companyName;
    private Long originalPageCount;
    private Long attachmentPageCount;
    private String documentNo;
    private List<ProgressRecordFill> progressRecords;
}
