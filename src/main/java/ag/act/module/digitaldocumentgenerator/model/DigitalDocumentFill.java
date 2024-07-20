package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DigitalDocumentFill {
    private String digitalDocumentNo;
    private GrantorFill grantor;
    private String companyName;
    private String signatureImageSrc;
    private String attachingFilesDescription;
    private String version;
}
