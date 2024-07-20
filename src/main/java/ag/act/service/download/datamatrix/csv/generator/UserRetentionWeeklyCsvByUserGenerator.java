package ag.act.service.download.datamatrix.csv.generator;

import ag.act.dto.SimpleUserDto;
import ag.act.dto.datamatrix.UserRetentionDataMapPeriodDto;
import ag.act.dto.datamatrix.UserRetentionWeeklyByUserCsvGenerateRequestInput;
import ag.act.dto.datamatrix.UserRetentionWeeklyCsvGenerateRequestInput;
import ag.act.dto.datamatrix.provider.UserRetentionDataProviderRequest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType;
import ag.act.service.download.datamatrix.data.provider.UserRetentionDataByUserProviderResolver;
import ag.act.service.download.datamatrix.record.generator.UserRetentionWeeklyCsvRecordByUserGenerator;
import ag.act.service.user.UserService;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.DateTimeUtil;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserRetentionWeeklyCsvByUserGenerator
    implements UserRetentionWeeklyCsvGenerator<UserRetentionWeeklyByUserCsvGenerateRequestInput> {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final int SIZE_PER_PAGE = 500;
    private static final String DATE_DELIMITER = "~";
    private static final int DATA_START_INDEX = 1;

    private final UserRetentionWeeklyCsvRecordByUserGenerator userRetentionWeeklyCsvRecordByUserGenerator;
    private final UserRetentionDataByUserProviderResolver userRetentionDataByUserProviderResolver;
    private final UserService userService;

    @Override
    public boolean supports(UserRetentionWeeklyCsvRowDataType userRetentionWeeklyCsvRowDataType) {
        return userRetentionWeeklyCsvRowDataType == UserRetentionWeeklyCsvRowDataType.USER;
    }

    @Override
    public void generate(UserRetentionWeeklyCsvGenerateRequestInput userRetentionWeeklyCsvGenerateRequestInput) {
        process(userRetentionWeeklyCsvGenerateRequestInput);
    }

    @SuppressWarnings({"RightCurly"})
    @Override
    public void process(UserRetentionWeeklyByUserCsvGenerateRequestInput csvGenerateRequestInput) {

        final List<String[]> csvData = csvGenerateRequestInput.getCsvData();
        final LocalDate startDateFromReferenceDate = getClosestPastAppRenewalDateDayOfWeek(csvGenerateRequestInput);
        final LocalDate startDate = minStartDate(csvGenerateRequestInput.getAppRenewalDate(), csvData, startDateFromReferenceDate);

        Pageable pageable = getPageable();
        Page<SimpleUserDto> page;
        do {
            page = userService.findAllSimpleUsers(pageable);
            log.info(
                "CSVCREATE before processing {}/{}pages, totalUsers: {}",
                (pageable.getPageNumber() + 1),
                page.getTotalPages(),
                page.getTotalElements()
            );

            processUsers(startDate, csvGenerateRequestInput, page, getCsvRecordMap(csvData, startDate));
            pageable = pageable.next();
        } while (!page.isEmpty());

        try {
            CSVWriter csvWriter = csvGenerateRequestInput.getCsvWriter();
            csvWriter.flush();
        } catch (IOException e) {
            log.error("CSVCREATE - final flush() - Failed to flush CSVWriter.", e);
            throw new RuntimeException(e);
        }
    }

    private Map<Long, String[]> getCsvRecordMap(List<String[]> csvData, LocalDate startDate) {
        if (CollectionUtils.isEmpty(csvData)) {
            return Map.of();
        }

        final int endIndexOfHeaders = findEndIndexOfHeaders(csvData, startDate);

        return csvData.stream().skip(1)
            .collect(Collectors.toMap(
                this::getUserId,
                record -> {
                    if (endIndexOfHeaders >= record.length) {
                        return record;
                    }
                    return Arrays.asList(record).subList(DATA_START_INDEX, endIndexOfHeaders).toArray(String[]::new);
                }
            ));
    }

    private int findEndIndexOfHeaders(List<String[]> csvData, LocalDate startDate) {
        final DateTimeFormatter formatter = DateTimeFormatUtil.yyyy_MM_dd();
        final String headerDate = "%s%s%s".formatted(
            startDate.format(formatter),
            DATE_DELIMITER,
            DateTimeUtil.getDateBeforeNextWeek(startDate).format(formatter)
        );
        int endIndex = Arrays.asList(csvData.get(0)).indexOf(headerDate);
        return endIndex == -1 ? csvData.get(0).length : endIndex;
    }

    private long getUserId(String[] record) {
        return Long.parseLong(record[0].split("/")[0]);
    }

    private LocalDate minStartDate(LocalDate appRenewalDate, List<String[]> csvData, LocalDate startDateFromReferenceDate) {
        if (CollectionUtils.isEmpty(csvData)) {
            return appRenewalDate;
        }
        final LocalDate startDateFromCsv = getStartDateFromCsv(csvData);
        return startDateFromCsv.isBefore(startDateFromReferenceDate) ? startDateFromCsv : startDateFromReferenceDate;
    }

    @NotNull
    private LocalDate getStartDateFromCsv(List<String[]> csvData) {
        final String[] header = csvData.get(0);
        final String lastHeader = header[header.length - 1];
        final String startDateFromCsv = lastHeader.substring(0, lastHeader.indexOf(DATE_DELIMITER));
        return LocalDate.parse(startDateFromCsv, DateTimeFormatUtil.yyyy_MM_dd());
    }

    private LocalDate getClosestPastAppRenewalDateDayOfWeek(UserRetentionWeeklyByUserCsvGenerateRequestInput csvGenerateRequestInput) {
        final LocalDate referenceDate = csvGenerateRequestInput.getReferenceDate();
        final DayOfWeek appRenewalDateDayOfWeek = csvGenerateRequestInput.getAppRenewalDate().getDayOfWeek();

        // 기준일을 포함한 이전 날짜 중 가장 가까운 금요일(앱리뉴얼 요일)을 찾는다.
        return DateTimeUtil.adjustToPreviousOrSameDayOfWeek(referenceDate, appRenewalDateDayOfWeek);
    }

    private void processUsers(
        LocalDate startDate,
        UserRetentionWeeklyByUserCsvGenerateRequestInput csvGenerateRequestInput,
        Page<SimpleUserDto> simpleUserDtos,
        Map<Long, String[]> csvDataMap
    ) {
        final Map<SimpleUserDto, Map<LocalDate, String>> resultByWeekByUser = new HashMap<>();
        simpleUserDtos.stream()
            .parallel()
            .forEach(user -> processEachUser(
                startDate,
                csvGenerateRequestInput,
                user,
                resultByWeekByUser
            ));

        simpleUserDtos
            .forEach(user -> writeCsv(
                csvGenerateRequestInput,
                resultByWeekByUser.get(user),
                user,
                csvDataMap
            ));

        try {
            CSVWriter csvWriter = csvGenerateRequestInput.getCsvWriter();
            csvWriter.flush();
        } catch (IOException e) {
            log.error("CSVDOWNLOAD - flush() - after processUsers() - Failed to flush CSVWriter.", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processEachUser(
        LocalDate startDate,
        UserRetentionWeeklyByUserCsvGenerateRequestInput csvGenerateRequestInput,
        SimpleUserDto user,
        Map<SimpleUserDto, Map<LocalDate, String>> resultByWeekByUser
    ) {
        final LocalDate referenceDate = csvGenerateRequestInput.getReferenceDate();

        final var userRetentionDataByUserProviderRequest = new UserRetentionDataProviderRequest(
            UserRetentionDataMapPeriodDto.newInstance(startDate, referenceDate),
            user,
            csvGenerateRequestInput.getUserRetentionWeeklyCsvDataType()
        );

        Map<LocalDate, String> dataByWeek = userRetentionDataByUserProviderResolver
            .resolve(csvGenerateRequestInput.getUserRetentionWeeklyCsvDataType())
            .getRetentionDataMap(userRetentionDataByUserProviderRequest);

        resultByWeekByUser.put(user, dataByWeek);
    }

    private void writeCsv(
        UserRetentionWeeklyByUserCsvGenerateRequestInput csvGenerateRequestInput,
        Map<LocalDate, String> dataByWeekByUser,
        SimpleUserDto user,
        String[] record
    ) {
        CSVWriter csvWriter = csvGenerateRequestInput.getCsvWriter();
        String[] csvRecord = userRetentionWeeklyCsvRecordByUserGenerator.toCsvRecord(
            dataByWeekByUser,
            user,
            record,
            csvGenerateRequestInput.getEndIndex()
        );
        csvWriter.writeNext(
            csvRecord
        );
    }

    private void writeCsv(
        UserRetentionWeeklyByUserCsvGenerateRequestInput csvGenerateRequestInput,
        Map<LocalDate, String> dataByWeekByUser,
        SimpleUserDto user,
        Map<Long, String[]> csvDataMap
    ) {
        final Long userId = user.getId();

        writeCsv(csvGenerateRequestInput, dataByWeekByUser, user, csvDataMap.getOrDefault(userId, EMPTY_STRING_ARRAY));
    }

    private Pageable getPageable() {
        return PageRequest.of(0, SIZE_PER_PAGE, Sort.by(Sort.Direction.ASC, "createdAt"));
    }
}
