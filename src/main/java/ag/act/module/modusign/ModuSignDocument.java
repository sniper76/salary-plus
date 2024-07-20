package ag.act.module.modusign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuSignDocument {

    private String id;
    private String title;
    private String status; // ON_PROCESSING
    private List<ModuSignParticipant> participants;
    private String participantId;

    public String getParticipantId() {
        if (StringUtils.isNotBlank(participantId)) {
            return participantId;
        }

        if (CollectionUtils.isEmpty(participants)) {
            return null;
        }
        return participants.get(0).getId();
    }

}
