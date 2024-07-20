package ag.act.service.download.datamatrix.csv.generator;

import ag.act.dto.datamatrix.UserRetentionDataMapPeriodDto;
import ag.act.dto.datamatrix.UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput;
import ag.act.dto.datamatrix.UserRetentionWeeklyCsvGenerateRequestInput;
import ag.act.dto.datamatrix.UserRetentionWeeklyCsvRequestDto;
import ag.act.dto.datamatrix.provider.UserRetentionDataOfDigitalDocumentByWeekProviderRequest;
import ag.act.dto.datamatrix.provider.UserRetentionDataProviderRequest;
import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType;
import ag.act.exception.BadRequestException;
import ag.act.service.digitaldocument.campaign.CampaignService;
import ag.act.service.download.datamatrix.data.provider.UserRetentionDataByWeekProviderResolver;
import ag.act.service.download.datamatrix.record.generator.UserRetentionWeeklyCsvRecordByWeekGenerator;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ag.act.util.DateTimeUtil.getTodayLocalDate;

@SuppressWarnings({"unchecked", "LineLength"})
@Component
@RequiredArgsConstructor
public class UserRetentionWeeklyCsvOfDigitalDocumentByWeekGenerator
    implements UserRetentionWeeklyCsvGenerator<UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput> {

    private final UserRetentionWeeklyCsvRecordByWeekGenerator userRetentionWeeklyCsvRecordByWeekGenerator;
    private final CampaignService campaignService;
    private final UserRetentionDataByWeekProviderResolver userRetentionDataByWeekProviderResolver;

    @Override
    public boolean supports(UserRetentionWeeklyCsvRowDataType userRetentionWeeklyCsvRowDataType) {
        return userRetentionWeeklyCsvRowDataType == UserRetentionWeeklyCsvRowDataType.WEEKLY_OF_DIGITAL_DOCUMENT_PERIOD;
    }

    @Override
    public void generate(UserRetentionWeeklyCsvGenerateRequestInput userRetentionWeeklyCsvGenerateRequestInput) {
        process(userRetentionWeeklyCsvGenerateRequestInput);
    }

    @Override
    public void process(UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput csvGenerateRequestInput) {
        final List<Long> digitalDocumentIds = getDigitalDocumentIds(csvGenerateRequestInput);
        final LocalDate today = getTodayLocalDate();

        final DigitalDocumentProgressPeriodDto digitalDocumentProgressPeriodDto = csvGenerateRequestInput.getUserRetentionWeeklyCsvRequestDto()
            .getDigitalDocumentProgressPeriodDto();
        final LocalDate retentionSearchStartDate = csvGenerateRequestInput.getReferenceStartDate();

        Stream.iterate(
                retentionSearchStartDate,
                referenceWeek -> referenceWeek.isBefore(digitalDocumentProgressPeriodDto.getTargetEndDate()),
                referenceWeek -> referenceWeek.plusWeeks(1)
            )
            .forEach(referenceWeek -> processEachWeek(
                csvGenerateRequestInput,
                referenceWeek,
                today,
                digitalDocumentIds
            ));
    }

    private void processEachWeek(
        UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput csvGenerateRequestInput,
        LocalDate referenceWeek,
        LocalDate today,
        List<Long> digitalDocumentIds
    ) throws RuntimeException {
        final var userRetentionDataMapPeriodDto = UserRetentionDataMapPeriodDto.newInstance(
            csvGenerateRequestInput.getReferenceStartDate(),
            today,
            referenceWeek
        );

        final UserRetentionDataOfDigitalDocumentByWeekProviderRequest userRetentionDataOfDigitalDocumentByWeekProviderRequest =
            new UserRetentionDataProviderRequest(
                digitalDocumentIds,
                userRetentionDataMapPeriodDto,
                csvGenerateRequestInput.getUserRetentionWeeklyCsvRequestDto().getDigitalDocumentProgressPeriodDto()
            );

        final UserRetentionWeeklyCsvDataType csvDataType = csvGenerateRequestInput.getUserRetentionWeeklyCsvDataType();
        final Map<LocalDate, Double> dataByWeek = userRetentionDataByWeekProviderResolver
            .resolve(csvDataType)
            .getRetentionDataMap(userRetentionDataOfDigitalDocumentByWeekProviderRequest);

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

    private List<Long> getDigitalDocumentIdsByCampaignId(Long campaignId) {
        final Long sourcePostId = campaignService.getSourcePostId(campaignId);
        return campaignService.findAllDigitalDocumentIdsBySourcePostId(sourcePostId);
    }

    private List<Long> getDigitalDocumentIds(UserRetentionWeeklyByWeekOfDigitalDocumentGenerateRequestInput csvGenerateInput) {
        final UserRetentionWeeklyCsvRequestDto userRetentionWeeklyCsvRequestDto = csvGenerateInput.getUserRetentionWeeklyCsvRequestDto();

        return Optional.ofNullable(userRetentionWeeklyCsvRequestDto.getCampaignId())
            .map(this::getDigitalDocumentIdsByCampaignId)
            .orElseGet(() -> Optional.ofNullable(userRetentionWeeklyCsvRequestDto.getDigitalDocumentId())
                .map(List::of)
                .orElseThrow(() -> new BadRequestException("전자문서 id 혹은 캠페인 id 중 하나는 전달되어야 합니다.")));
    }
}
