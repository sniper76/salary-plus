package ag.act.dto.datamatrix;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import com.opencsv.CSVWriter;

import java.time.LocalDate;

public interface UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput {

    UserRetentionWeeklyCsvDataType getUserRetentionWeeklyCsvDataType();

    CSVWriter getCsvWriter();

    LocalDate getDigitalDocumentStartDate();

    LocalDate getDigitalDocumentEndDate();

    LocalDate getReferenceStartDate();

    UserRetentionWeeklyCsvRequestDto getUserRetentionWeeklyCsvRequestDto();
}
