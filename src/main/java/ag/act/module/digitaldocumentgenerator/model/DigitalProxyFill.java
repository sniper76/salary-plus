package ag.act.module.digitaldocumentgenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DigitalProxyFill extends DigitalDocumentFill {
    private ShareHolderMeetingFill shareHolderMeeting;
    private AcceptorFill acceptor;
    private String designatedAgentNames;
    private List<DigitalDocumentItemFill> digitalDocumentItems;
}
