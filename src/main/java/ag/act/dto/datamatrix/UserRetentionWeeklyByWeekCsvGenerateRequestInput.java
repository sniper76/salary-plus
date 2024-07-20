package ag.act.dto.datamatrix;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import com.opencsv.CSVWriter;

import java.time.LocalDate;

public interface UserRetentionWeeklyByWeekCsvGenerateRequestInput {

    UserRetentionWeeklyCsvDataType getUserRetentionWeeklyCsvDataType();

    CSVWriter getCsvWriter();

    LocalDate getReferenceDate();

    LocalDate getAppRenewalDate();
}
