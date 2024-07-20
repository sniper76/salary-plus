package ag.act.module.digitaldocumentgenerator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProgressRecordFill {
    private String time;
    private String ipAddress;
    private String message;
}
