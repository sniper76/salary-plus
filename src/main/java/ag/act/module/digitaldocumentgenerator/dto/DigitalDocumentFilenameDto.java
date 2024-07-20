package ag.act.module.digitaldocumentgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DigitalDocumentFilenameDto {
    private String stockName;
    private String postTitle;
    private String userName;
    private LocalDateTime userBirthDate;
}
