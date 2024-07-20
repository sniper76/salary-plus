package ag.act.service.download.csv;

import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.exception.InternalServerException;
import ag.act.repository.DigitalDocumentItemRepository;
import ag.act.repository.DigitalDocumentUserRepository;
import ag.act.service.digitaldocument.answer.DigitalDocumentItemUserAnswerService;
import ag.act.service.download.AbstractCsvDownloadProcessor;
import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;
import ag.act.service.download.csv.record.DigitalDocumentCsvRecordGenerator;
import ag.act.service.download.csv.record.DigitalDocumentCsvRecordGeneratorResolver;
import ag.act.service.user.UserService;
import ag.act.util.CSVWriterFactory;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"RightCurly"})
@Service
public class DigitalDocumentCsvDownloadProcessor extends AbstractCsvDownloadProcessor {
    private static final int PAGE_SIZE = 50;
    private final DigitalDocumentUserRepository digitalDocumentUserRepository;
    private final DigitalDocumentItemRepository digitalDocumentItemRepository;
    private final DigitalDocumentItemUserAnswerService digitalDocumentItemUserAnswerService;
    private final DigitalDocumentCsvRecordGeneratorResolver defaultDigitalDocumentCsvRecordGenerator;
    private final UserService userService;

    public DigitalDocumentCsvDownloadProcessor(
        CSVWriterFactory csvWriterFactory,
        DigitalDocumentUserRepository digitalDocumentUserRepository,
        DigitalDocumentItemRepository digitalDocumentItemRepository,
        DigitalDocumentItemUserAnswerService digitalDocumentItemUserAnswerService,
        DigitalDocumentCsvRecordGeneratorResolver defaultDigitalDocumentCsvRecordGenerator,
        UserService userService
    ) {
        super(csvWriterFactory);
        this.digitalDocumentUserRepository = digitalDocumentUserRepository;
        this.digitalDocumentItemRepository = digitalDocumentItemRepository;
        this.digitalDocumentItemUserAnswerService = digitalDocumentItemUserAnswerService;
        this.defaultDigitalDocumentCsvRecordGenerator = defaultDigitalDocumentCsvRecordGenerator;
        this.userService = userService;
    }

    public void download(HttpServletResponse response, List<Long> digitalDocumentIds) {
        if (CollectionUtils.isEmpty(digitalDocumentIds)) {
            return;
        }

        Pageable pageable = getPageable();
        final DigitalDocumentCsvRecordGenerator digitalDocumentCsvRecordGenerator = defaultDigitalDocumentCsvRecordGenerator.resolve();

        try (CSVWriter csvWriter = initializeCsvWriter(response.getOutputStream())) {
            writeHeaders(csvWriter, digitalDocumentIds.get(0), digitalDocumentCsvRecordGenerator);

            Page<DigitalDocumentUser> page;
            do {
                page = getDigitalDocumentUsers(pageable, digitalDocumentIds);
                generateCsvChunk(page, csvWriter, digitalDocumentCsvRecordGenerator);
                response.flushBuffer();
                pageable = pageable.next();
            } while (!page.isEmpty());
        } catch (Exception e) {
            throw new InternalServerException("CSV 전자문서 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    private PageRequest getPageable() {
        return PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "digitalDocumentId", "issuedNumber"));
    }

    private Page<DigitalDocumentUser> getDigitalDocumentUsers(Pageable pageable, List<Long> digitalDocumentIds) {
        return digitalDocumentUserRepository.findAllByDigitalDocumentIdInAndDigitalDocumentAnswerStatusIn(
            digitalDocumentIds,
            List.of(DigitalDocumentAnswerStatus.COMPLETE),
            pageable
        );
    }

    private void generateCsvChunk(
        Page<DigitalDocumentUser> digitalDocumentUsers,
        CSVWriter csvWriter,
        DigitalDocumentCsvRecordGenerator digitalDocumentCsvRecordGenerator
    ) {
        final var answersByUserIdByDocumentIdMap = getAnswersByUserIdByDocumentId(digitalDocumentUsers);

        for (DigitalDocumentUser digitalDocumentUser : digitalDocumentUsers) {
            csvWriter.writeNext(
                digitalDocumentCsvRecordGenerator.toCsvRecord(
                    new DigitalDocumentCsvRecordInputDto(
                        digitalDocumentUser,
                        getUserAnswerDtoList(digitalDocumentUser, answersByUserIdByDocumentIdMap),
                        () -> userService.findAllSimpleUsersInAllStatus(getUserIds(digitalDocumentUsers))
                    )
                )
            );
        }
    }

    private List<Long> getUserIds(Page<DigitalDocumentUser> digitalDocumentUsers) {
        return digitalDocumentUsers.stream().map(DigitalDocumentUser::getUserId).toList();
    }

    @NotNull
    private Map<Long, Map<Long, List<DigitalDocumentItemUserAnswer>>> getAnswersByUserIdByDocumentId(
        Page<DigitalDocumentUser> digitalDocumentUsers
    ) {
        final Map<Long, Map<Long, List<DigitalDocumentItemUserAnswer>>> answersByUserIdByDocumentId = new HashMap<>();

        final Map<Long, List<DigitalDocumentUser>> digitalDocumentUserByDigitalDocumentIdMap = digitalDocumentUsers
            .getContent()
            .stream()
            .collect(Collectors.groupingBy(DigitalDocumentUser::getDigitalDocumentId));

        digitalDocumentUserByDigitalDocumentIdMap.keySet()
            .forEach(
                digitalDocumentId -> answersByUserIdByDocumentId.put(
                    digitalDocumentId,
                    digitalDocumentItemUserAnswerService.getUserAnswersMapByUserId(
                        digitalDocumentId,
                        digitalDocumentUserByDigitalDocumentIdMap.get(digitalDocumentId).stream().map(DigitalDocumentUser::getUserId).toList()
                    )
                )
            );

        return answersByUserIdByDocumentId;
    }

    private List<DigitalDocumentItemUserAnswer> getUserAnswerDtoList(
        DigitalDocumentUser digitalDocumentUser,
        Map<Long, Map<Long, List<DigitalDocumentItemUserAnswer>>> answersByUserIdByDocumentId
    ) {
        return Optional.ofNullable(answersByUserIdByDocumentId.get(digitalDocumentUser.getDigitalDocumentId()))
            .map(answersByUserId -> answersByUserId.get(digitalDocumentUser.getUserId()))
            .orElse(List.of());
    }

    private void writeHeaders(CSVWriter csvWriter, Long digitalDocumentId, DigitalDocumentCsvRecordGenerator digitalDocumentCsvRecordGenerator) {
        final String[] headers = Stream.concat(
                digitalDocumentCsvRecordGenerator.getHeaders().stream(),
                findExtraItemTitleHeaders(digitalDocumentId).stream()
            )
            .toArray(String[]::new);

        csvWriter.writeNext(headers);
    }

    private List<String> findExtraItemTitleHeaders(Long digitalDocumentId) {
        return digitalDocumentItemRepository.findAllByDigitalDocumentIdAndIsLastItemTrueOrderByIdAsc(digitalDocumentId)
            .stream()
            .map(DigitalDocumentItem::getTitle)
            .toList();
    }
}
