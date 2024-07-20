package ag.act.service.download.datamatrix;

import ag.act.dto.datamatrix.UserRetentionWeeklyCsvGenerateRequestInput;
import ag.act.dto.datamatrix.UserRetentionWeeklyCsvRequestDto;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.download.AbstractCsvDownloadProcessor;
import ag.act.service.download.datamatrix.csv.generator.UserRetentionWeeklyCsvGeneratorResolver;
import ag.act.service.download.datamatrix.headerwriter.UserRetentionWeeklyCsvHeaderWriterResolver;
import ag.act.util.AppRenewalDateProvider;
import ag.act.util.CSVWriterFactory;
import ag.act.util.DateTimeUtil;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static ag.act.util.DateTimeUtil.getTodayLocalDate;

@Slf4j
@Service
public class UserRetentionWeeklyCsvDownloadProcessor extends AbstractCsvDownloadProcessor {
    private final AppRenewalDateProvider appRenewalDateProvider;
    private final UserRetentionWeeklyCsvHeaderWriterResolver userRetentionWeeklyCsvHeaderWriterResolver;
    private final UserRetentionWeeklyCsvGeneratorResolver userRetentionWeeklyCsvGeneratorResolver;

    public UserRetentionWeeklyCsvDownloadProcessor(
        CSVWriterFactory csvWriterFactory,
        AppRenewalDateProvider appRenewalDateProvider,
        UserRetentionWeeklyCsvHeaderWriterResolver userRetentionWeeklyCsvHeaderWriterResolver,
        UserRetentionWeeklyCsvGeneratorResolver userRetentionWeeklyCsvGeneratorResolver
    ) {
        super(csvWriterFactory);
        this.appRenewalDateProvider = appRenewalDateProvider;
        this.userRetentionWeeklyCsvHeaderWriterResolver = userRetentionWeeklyCsvHeaderWriterResolver;
        this.userRetentionWeeklyCsvGeneratorResolver = userRetentionWeeklyCsvGeneratorResolver;
    }

    @SuppressWarnings("RightCurly")
    public void download(
        HttpServletResponse response,
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType
    ) throws Exception {
        final LocalDate date = getTodayLocalDate();
        writeCsv(userRetentionWeeklyCsvDataType, response.getOutputStream(), date);
    }

    @SuppressWarnings("RightCurly")
    public void download(
        HttpServletResponse response,
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        UserRetentionWeeklyCsvRequestDto userRetentionWeeklyCsvRequestDto
    ) throws Exception {
        writeCsv(userRetentionWeeklyCsvDataType, response.getOutputStream(), userRetentionWeeklyCsvRequestDto);
    }

    public ByteArrayOutputStream create(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        List<String[]> csvData,
        LocalDate referenceDate
    ) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeCsv(userRetentionWeeklyCsvDataType, outputStream, referenceDate, csvData);
        return outputStream;
    }

    private void writeCsv(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        OutputStream outputStream,
        LocalDate date
    ) throws IOException {
        try (CSVWriter csvWriter = initializeCsvWriter(outputStream)) {

            setHeaders(userRetentionWeeklyCsvDataType, csvWriter, date);

            userRetentionWeeklyCsvGeneratorResolver.resolve(userRetentionWeeklyCsvDataType)
                .generate(new UserRetentionWeeklyCsvGenerateRequestInput(
                        userRetentionWeeklyCsvDataType,
                        csvWriter,
                        appRenewalDateProvider.get(),
                        date
                    )
                );
        }
    }

    private void writeCsv(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        OutputStream outputStream,
        UserRetentionWeeklyCsvRequestDto userRetentionWeeklyCsvRequestDto
    ) throws IOException {
        try (CSVWriter csvWriter = initializeCsvWriter(outputStream)) {

            final LocalDate digitalDocumentTargetStartDate = userRetentionWeeklyCsvRequestDto.getDigitalDocumentProgressPeriodDto()
                .getTargetStartDate();

            DayOfWeek referenceDateDayOfWeek = appRenewalDateProvider.get().getDayOfWeek();
            final LocalDate headerStartDate = DateTimeUtil.adjustToPreviousOrSameDayOfWeek(
                digitalDocumentTargetStartDate,
                referenceDateDayOfWeek
            );

            final LocalDate today = getTodayLocalDate();

            setHeaders(userRetentionWeeklyCsvDataType, csvWriter, headerStartDate, today);

            userRetentionWeeklyCsvGeneratorResolver.resolve(userRetentionWeeklyCsvDataType)
                .generate(new UserRetentionWeeklyCsvGenerateRequestInput(
                        userRetentionWeeklyCsvDataType,
                        csvWriter,
                        headerStartDate,
                        userRetentionWeeklyCsvRequestDto
                    )
                );
        }
    }

    private void writeCsv(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        OutputStream outputStream,
        LocalDate referenceDate,
        List<String[]> csvData
    ) throws IOException {
        try (CSVWriter csvWriter = initializeCsvWriter(outputStream)) {

            int headerSize = setHeadersAndGetSize(userRetentionWeeklyCsvDataType, csvWriter, referenceDate);

            userRetentionWeeklyCsvGeneratorResolver.resolve(userRetentionWeeklyCsvDataType)
                .generate(new UserRetentionWeeklyCsvGenerateRequestInput(
                        userRetentionWeeklyCsvDataType,
                        csvWriter,
                        appRenewalDateProvider.get(),
                        referenceDate,
                        csvData,
                        headerSize
                    )
                );
        }
    }

    private void setHeaders(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        CSVWriter csvWriter,
        LocalDate today
    ) {
        userRetentionWeeklyCsvHeaderWriterResolver.resolve(userRetentionWeeklyCsvDataType)
            .setHeaders(csvWriter, appRenewalDateProvider.get(), today);
    }

    private void setHeaders(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        CSVWriter csvWriter,
        LocalDate startDate,
        LocalDate endDate
    ) {
        userRetentionWeeklyCsvHeaderWriterResolver.resolve(userRetentionWeeklyCsvDataType)
            .setHeaders(csvWriter, startDate, endDate);
    }

    private int setHeadersAndGetSize(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        CSVWriter csvWriter,
        LocalDate referenceDate
    ) {
        return userRetentionWeeklyCsvHeaderWriterResolver.resolve(userRetentionWeeklyCsvDataType)
            .setHeadersAndGetSize(csvWriter, appRenewalDateProvider.get(), referenceDate);
    }
}
