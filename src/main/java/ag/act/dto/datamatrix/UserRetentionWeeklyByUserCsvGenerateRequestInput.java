package ag.act.dto.datamatrix;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import com.opencsv.CSVWriter;

import java.time.LocalDate;
import java.util.List;

public interface UserRetentionWeeklyByUserCsvGenerateRequestInput {
    UserRetentionWeeklyCsvDataType getUserRetentionWeeklyCsvDataType();

    CSVWriter getCsvWriter();

    LocalDate getAppRenewalDate();

    LocalDate getReferenceDate();

    List<String[]> getCsvData();

    int getEndIndex();
}
