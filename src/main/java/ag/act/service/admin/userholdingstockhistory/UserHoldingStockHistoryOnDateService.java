package ag.act.service.admin.userholdingstockhistory;


import ag.act.core.annotation.NoTransactional;
import ag.act.entity.MyDataSummary;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.repository.UserHoldingStockHistoryOnDateRepository;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.ChunkUtil;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@NoTransactional
@RequiredArgsConstructor
public class UserHoldingStockHistoryOnDateService {
    private static final int SIZE_PER_PAGE = 20;
    private static final int ID_SEARCH_CHUNK_SIZE = 1000;

    private final UserHoldingStockHistoryOnDateRepository userHoldingStockHistoryOnDateRepository;
    private final UserHoldingStockService userHoldingStockService;
    private final MyDataSummaryService myDataSummaryService;
    private final ChunkUtil chunkUtil;
    private final JsonMyDataStockToUserHoldingStockHistoryOnDateWriter jsonMyDataStockToUserHoldingStockHistoryOnDateWriter;
    private final UserHoldingStockToUserHoldingStockHistoryOnDateWriter userHoldingStockToUserHoldingStockHistoryOnDateWriter;

    public int createFirstUserHoldingStockHistories(LocalDate yesterdayLocalDate) {
        Pageable pageable = getPageable();
        Page<MyDataSummary> page;
        AtomicInteger resultCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        log.info("USERHOLDINGSTOCKHISTORIES start");
        do {
            page = myDataSummaryService.findAllByPageable(pageable);

            page.forEach(myDataSummary -> resultCount.addAndGet(
                jsonMyDataStockToUserHoldingStockHistoryOnDateWriter
                    .generateFillDataUntilYesterday(myDataSummary, yesterdayLocalDate))
            );

            long executionTime = System.currentTimeMillis() - startTime;
            log.info(
                "USERHOLDINGSTOCKHISTORIES exTime:{}m, pageNumber:{}, pageSize:{}, totalElements:{}, totalPages:{}, resultCount:{}",
                executionTime / 1000 / 60,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                "" + resultCount.get()
            );
            pageable = pageable.next();
        }
        while (hasNext(page));

        log.info("USERHOLDINGSTOCKHISTORIES end");
        return resultCount.get();
    }

    private boolean hasNext(Page<MyDataSummary> page) {
        return !page.isEmpty() && ((page.getNumber() + 1) < page.getTotalPages());
    }

    public int createYesterdayUserHoldingStockHistories(LocalDate yesterdayLocalDate) {
        final AtomicInteger resultCount = new AtomicInteger(0);
        final List<Long> userHoldingStockServiceAllIds = userHoldingStockService.findAllIds();
        final List<List<Long>> userHoldingStockIds = getIdChunks(userHoldingStockServiceAllIds);

        userHoldingStockIds
            .forEach(idList -> {
                userHoldingStockToUserHoldingStockHistoryOnDateWriter
                    .createYesterdayUserHoldingStockHistories(yesterdayLocalDate, idList, resultCount);

                log.info(
                    "USERHOLDINGSTOCKHISTORIES resultCount : {} / totalElements : {}",
                    "" + resultCount.get(),
                    userHoldingStockServiceAllIds.size()
                );
            });
        return resultCount.get();
    }

    private List<List<Long>> getIdChunks(List<Long> content) {
        return chunkUtil.getChunks(content, ID_SEARCH_CHUNK_SIZE);
    }

    private Pageable getPageable() {
        return PageRequest.of(0, SIZE_PER_PAGE, Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    public long countDigitalDocumentSignatureOpportunity(
        Long userId,
        List<String> digitalDocumentTypes,
        LocalDateTime startDateTime,
        LocalDate startDate
    ) {
        return userHoldingStockHistoryOnDateRepository.countStocksByDigitalDocumentDuringAndTypeInAndExistsStocksByUserIdAndDate(
            userId,
            digitalDocumentTypes,
            startDateTime,
            startDateTime.plusWeeks(1),
            startDate,
            DateTimeUtil.getDateBeforeNextWeek(startDate)
        );
    }
}
