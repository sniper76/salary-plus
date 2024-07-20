package ag.act.service.download.datamatrix.csv.generator;

import ag.act.dto.datamatrix.UserRetentionDataMapPeriodDto;
import ag.act.dto.datamatrix.UserRetentionWeeklyByWeekCsvGenerateRequestInput;
import ag.act.dto.datamatrix.UserRetentionWeeklyCsvGenerateRequestInput;
import ag.act.dto.datamatrix.provider.UserRetentionDataByWeekProviderRequest;
import ag.act.dto.datamatrix.provider.UserRetentionDataProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType;
import ag.act.service.download.datamatrix.data.provider.UserRetentionDataByWeekProviderResolver;
import ag.act.service.download.datamatrix.record.generator.UserRetentionWeeklyCsvRecordByWeekGenerator;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
@Component
@RequiredArgsConstructor
public class UserRetentionWeeklyCsvByWeekGenerator implements UserRetentionWeeklyCsvGenerator<UserRetentionWeeklyByWeekCsvGenerateRequestInput> {

    private final UserRetentionDataByWeekProviderResolver userRetentionDataByWeekProviderResolver;
    private final UserRetentionWeeklyCsvRecordByWeekGenerator userRetentionWeeklyCsvRecordByWeekGenerator;

    @Override
    public boolean supports(UserRetentionWeeklyCsvRowDataType userRetentionWeeklyCsvRowDataType) {
        return userRetentionWeeklyCsvRowDataType == UserRetentionWeeklyCsvRowDataType.WEEKLY;
    }

    @Override
    public void generate(UserRetentionWeeklyCsvGenerateRequestInput userRetentionWeeklyCsvGenerateRequestInput) {
        process(userRetentionWeeklyCsvGenerateRequestInput);
    }

    @Override
    public void process(UserRetentionWeeklyByWeekCsvGenerateRequestInput csvGenerateRequestInput) {
        Stream.iterate(
                csvGenerateRequestInput.getAppRenewalDate(),
                referenceWeek -> referenceWeek.isBefore(csvGenerateRequestInput.getReferenceDate()),
                referenceWeek -> referenceWeek.plusWeeks(1)
            )
            .forEach(referenceWeek -> processEachWeek(csvGenerateRequestInput, referenceWeek));
    }

    private void processEachWeek(
        UserRetentionWeeklyByWeekCsvGenerateRequestInput csvGenerateRequestInput,
        LocalDate referenceWeek
    ) throws RuntimeException {
        UserRetentionDataByWeekProviderRequest userRetentionDataByWeekProviderRequest = new UserRetentionDataProviderRequest(
            UserRetentionDataMapPeriodDto.newInstance(
                csvGenerateRequestInput.getAppRenewalDate(),
                csvGenerateRequestInput.getReferenceDate(),
                referenceWeek
            )
        );

        Map<LocalDate, Double> dataByWeek = userRetentionDataByWeekProviderResolver
            .resolve(csvGenerateRequestInput.getUserRetentionWeeklyCsvDataType())
            .getRetentionDataMap(userRetentionDataByWeekProviderRequest);

        CSVWriter csvWriter = csvGenerateRequestInput.getCsvWriter();

        csvWriter.writeNext(
            userRetentionWeeklyCsvRecordByWeekGenerator.toCsvRecord(referenceWeek, dataByWeek)
        );

        try {
            csvWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
