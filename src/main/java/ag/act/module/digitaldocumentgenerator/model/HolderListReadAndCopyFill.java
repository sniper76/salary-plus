package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HolderListReadAndCopyFill extends DigitalDocumentFill {
    private String leaderName;
    private String leaderAddress;
    private String companyName;
    private String ceoName;
    private String companyAddress;
    private String irPhoneNumber;
    private String deadlineDateByLeader1;
    private String deadlineDateByLeader2;
    private String referenceDateByLeader;
    private String leaderEmail;
}
