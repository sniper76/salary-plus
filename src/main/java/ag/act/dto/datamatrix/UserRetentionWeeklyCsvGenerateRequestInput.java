package ag.act.dto.datamatrix;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import com.opencsv.CSVWriter;

import java.time.LocalDate;
import java.util.List;


public class UserRetentionWeeklyCsvGenerateRequestInput
    implements UserRetentionWeeklyByUserCsvGenerateRequestInput,
    UserRetentionWeeklyByWeekCsvGenerateRequestInput,
    UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput {

    private UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType;
    private CSVWriter csvWriter;
    private LocalDate appRenewalDate;
    private LocalDate referenceDate;
    private List<String[]> csvData;
    private int endIndex;
    private LocalDate referenceStartDate;
    private UserRetentionWeeklyCsvRequestDto userRetentionWeeklyCsvRequestDto;

    public UserRetentionWeeklyCsvGenerateRequestInput(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        CSVWriter csvWriter,
        LocalDate appRenewalDate,
        LocalDate referenceDate
    ) {
        this(userRetentionWeeklyCsvDataType, csvWriter, appRenewalDate, referenceDate, List.of(), -1);
    }

    public UserRetentionWeeklyCsvGenerateRequestInput(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        CSVWriter csvWriter,
        LocalDate referenceStartDate,
        UserRetentionWeeklyCsvRequestDto userRetentionWeeklyCsvRequestDto
    ) {
        this.userRetentionWeeklyCsvDataType = userRetentionWeeklyCsvDataType;
        this.csvWriter = csvWriter;
        this.referenceStartDate = referenceStartDate;
        this.userRetentionWeeklyCsvRequestDto = userRetentionWeeklyCsvRequestDto;
    }

    public UserRetentionWeeklyCsvGenerateRequestInput(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        CSVWriter csvWriter,
        LocalDate appRenewalDate,
        LocalDate referenceDate,
        List<String[]> csvData,
        int endIndex
    ) {
        this.userRetentionWeeklyCsvDataType = userRetentionWeeklyCsvDataType;
        this.csvWriter = csvWriter;
        this.appRenewalDate = appRenewalDate;
        this.referenceDate = referenceDate;
        this.csvData = csvData;
        this.endIndex = endIndex;
    }

    public UserRetentionWeeklyCsvDataType getUserRetentionWeeklyCsvDataType() {
        return userRetentionWeeklyCsvDataType;
    }

    public CSVWriter getCsvWriter() {
        return csvWriter;
    }

    public LocalDate getAppRenewalDate() {
        return appRenewalDate;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public List<String[]> getCsvData() {
        return csvData;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public UserRetentionWeeklyCsvRequestDto getUserRetentionWeeklyCsvRequestDto() {
        return userRetentionWeeklyCsvRequestDto;
    }

    public LocalDate getReferenceStartDate() {
        return referenceStartDate;
    }

    @Override
    public LocalDate getDigitalDocumentStartDate() {
        return this.userRetentionWeeklyCsvRequestDto.getDigitalDocumentProgressPeriodDto().getTargetStartDate();
    }

    @Override
    public LocalDate getDigitalDocumentEndDate() {
        return this.userRetentionWeeklyCsvRequestDto.getDigitalDocumentProgressPeriodDto().getTargetEndDate();
    }
}

