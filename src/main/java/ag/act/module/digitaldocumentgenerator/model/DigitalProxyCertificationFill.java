package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalProxyCertificationFill extends DigitalDocumentCertificationFill {
    private AcceptorFill acceptor;
    private ShareHolderMeetingFill shareHolderMeeting;
}
