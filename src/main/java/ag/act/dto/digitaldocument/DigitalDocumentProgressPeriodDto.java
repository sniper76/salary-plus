package ag.act.dto.digitaldocument;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DigitalDocumentProgressPeriodDto {

    private final LocalDateTime targetStartDateTime;
    private final LocalDateTime targetEndDateTime;

    public LocalDate getTargetEndDate() {
        return targetEndDateTime.toLocalDate();
    }

    public LocalDate getTargetStartDate() {
        return targetStartDateTime.toLocalDate();
    }
}
