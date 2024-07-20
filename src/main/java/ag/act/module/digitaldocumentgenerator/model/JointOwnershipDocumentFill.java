package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JointOwnershipDocumentFill extends DigitalDocumentFill {
    private AcceptorFill acceptor;
    private String companyRegistrationNumber;
    private String content;
}
