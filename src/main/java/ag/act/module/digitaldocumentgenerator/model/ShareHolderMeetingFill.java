package ag.act.module.digitaldocumentgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShareHolderMeetingFill {
    private String name;
    private boolean regularShareHolderMeeting;
    private DateFill date;

    @SuppressWarnings("unused")
    //do not delete. method to let freemarker fetch regularShareHolderMeeting
    public boolean getRegularShareHolderMeeting() {
        return regularShareHolderMeeting;
    }
}
