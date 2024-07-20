package ag.act.module.mydata;

import ag.act.dto.mydata.AccountTransactionDto;
import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import ag.act.dto.mydata.MyDataStockInfoDto;
import ag.act.entity.MyDataSummary;
import ag.act.entity.User;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.repository.MyDataSummaryRepository;
import ag.act.repository.UserRepository;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static ag.act.util.DateTimeUtil.getEndOfLastYear;

@Service
public class MyDataSummaryService {
    private static final long ONE_DAY = 1L;
    private final MyDataJsonService myDataJsonService;
    private final MyDataSummaryRepository myDataSummaryRepository;
    private final UserRepository userRepository;

    public MyDataSummaryService(
        MyDataJsonService myDataJsonService,
        MyDataSummaryRepository myDataSummaryRepository,
        UserRepository userRepository
    ) {
        this.myDataJsonService = myDataJsonService;
        this.myDataSummaryRepository = myDataSummaryRepository;
        this.userRepository = userRepository;
    }

    public MyDataSummary findByUserIdNoneNull(Long userId) {
        return findByUserId(userId)
            .orElseThrow(() -> new BadRequestException("사용자의 마이데이터 정보가 없습니다."));
    }

    public void updateMyDataSummary(Long userId, MyDataStockInfoDto myDataStockInfoDto) {
        final MyDataSummary myDataSummary = findByUserId(userId).orElse(MyDataSummary.empty(userId));
        final JsonMyData jsonMyData = generateJsonMyData(myDataStockInfoDto, myDataSummary.getJsonMyData());

        myDataSummary.setJsonMyData(jsonMyData);
        myDataSummary.setPensionPaidAmount(myDataStockInfoDto.getPensionPaidAmount());
        myDataSummary.setLoanPrice(myDataStockInfoDto.getLoanPrice());
        myDataSummaryRepository.save(myDataSummary);

        final User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        user.setMyDataSummary(myDataSummary);
        userRepository.save(user);
    }

    public Optional<MyDataSummary> findByUserId(Long userId) {
        return myDataSummaryRepository.findByUserId(userId);
    }

    public Page<MyDataSummary> findAllByPageable(Pageable pageable) {
        return myDataSummaryRepository.findAll(pageable);
    }

    private JsonMyData generateJsonMyData(MyDataStockInfoDto myDataStockInfoDto, JsonMyData jsonMyData) {
        final Map<String, IntermediateUserHoldingStockDto> myDataStockMap = myDataStockInfoDto.getMyDataStockMap();
        final Map<String, List<AccountTransactionDto>> accountTransactionDtoMap = myDataStockInfoDto.getAccountTransactionDtoMap();
        final Map<String, List<JsonMyDataStock>> jsonStocksMapByMyDataProdCode = jsonMyData.getJsonMyDataStocksMapByMyDataProdCode();

        createJsonMyDataStocksWithTransactions(myDataStockMap, accountTransactionDtoMap, jsonStocksMapByMyDataProdCode);
        createJsonMyDataStocksWithoutTransactionData(myDataStockMap, accountTransactionDtoMap, jsonStocksMapByMyDataProdCode);

        final List<JsonMyDataStock> jsonMyDataStockList = cleanUpTempData(flatMap(jsonStocksMapByMyDataProdCode));

        jsonMyData.setJsonMyDataStockList(jsonMyDataStockList);

        return jsonMyData;
    }

    private List<JsonMyDataStock> cleanUpTempData(List<JsonMyDataStock> jsonMyDataStocks) {
        return jsonMyDataStocks.stream()
            .filter(it -> !it.isTemp())
            .toList();
    }

    private void createJsonMyDataStocksWithTransactions(
        Map<String, IntermediateUserHoldingStockDto> myDataStockMap,
        Map<String, List<AccountTransactionDto>> accountTransactionDtoMap,
        Map<String, List<JsonMyDataStock>> jsonStocksMapByMyDataProdCode
    ) {
        final LocalDateTime endOfLastYear = getEndOfLastYear();

        for (Map.Entry<String, List<AccountTransactionDto>> entry : accountTransactionDtoMap.entrySet()) {
            final String prodCode = entry.getKey();
            final IntermediateUserHoldingStockDto stockInfoDto = myDataStockMap.get(prodCode);
            final List<AccountTransactionDto> accountTransactionDtos = entry.getValue().stream()
                .peek(accountTransactionDto -> accountTransactionDto.setReverse(stockInfoDto.getQuantity() == 0L))
                .toList();
            final var lastTransactionDateTime = accountTransactionDtos.get(accountTransactionDtos.size() - 1).getTransactionLocalDateTime();

            final List<JsonMyDataStock> processingJsonMyDataStocks = filterJsonMyDataStockListLessThanLastLocalDate(
                jsonStocksMapByMyDataProdCode.getOrDefault(stockInfoDto.getMyDataProdCode(), new ArrayList<>()),
                lastTransactionDateTime.toLocalDate()
            );

            // TODO [MyData] 이 부분은 임시로 코드를 넣어둔다.
            //      거래내역이 있는 사람중에 잘못된 기준일의 데이터를 지우기 위해서.. ----> 다음에 지워야 한다.
            if (processingJsonMyDataStocks.size() == 1) {
                if (Objects.equals(processingJsonMyDataStocks.get(0).getReferenceDate(), endOfLastYear.toLocalDate())) {
                    processingJsonMyDataStocks.clear();
                }
            }

            final List<JsonMyDataStock> newJsonMyDataStocks = new ArrayList<>(createNewJsonMyDataStocksFromTransactions(
                accountTransactionDtos,
                stockInfoDto,
                processingJsonMyDataStocks,
                lastTransactionDateTime.minusDays(1)
            ));

            jsonStocksMapByMyDataProdCode.put(prodCode, newJsonMyDataStocks);

            // 기준일이 있는 경우, 기준일의 데이터를 만든다.
            myDataJsonService.createMyDataForReferenceDates(stockInfoDto.getStockCode(), newJsonMyDataStocks);

            // 기준 날짜로 정렬 DESC (최근 날짜가 맨 위로 올라오게)
            newJsonMyDataStocks.sort(Comparator.comparing(JsonMyDataStock::getReferenceDate).reversed());

            jsonStocksMapByMyDataProdCode.put(prodCode, newJsonMyDataStocks);
        }
    }

    private void createJsonMyDataStocksWithoutTransactionData(
        Map<String, IntermediateUserHoldingStockDto> myDataStockMap,
        Map<String, List<AccountTransactionDto>> accountTransactionDtoMap,
        Map<String, List<JsonMyDataStock>> jsonStocksMapByMyDataProdCode
    ) {
        final LocalDateTime endOfLastYear = getEndOfLastYear();

        myDataStockMap
            .values()
            .stream()
            .filter(stockInfoDto -> !accountTransactionDtoMap.containsKey(stockInfoDto.getMyDataProdCode()))
            .forEach(stockInfoDto -> {
                final List<JsonMyDataStock> processingJsonMyDataStocks = filterJsonMyDataStockListLessThanLastLocalDate(
                    jsonStocksMapByMyDataProdCode.getOrDefault(stockInfoDto.getMyDataProdCode(), new ArrayList<>()),
                    endOfLastYear.toLocalDate()
                );

                // TODO [MyData] 나중에 다시 한번 체크해야 할지도
                //if (CollectionUtils.isEmpty(processingJsonMyDataStocks)) {
                //processingJsonMyDataStocks.add(myDataJsonService.createEndOfLastYearJsonMyDataStock(stockInfoDto, stockInfoDto.getQuantity()))
                //}

                processingJsonMyDataStocks.add(myDataJsonService.createTodayJsonMyDataStock(stockInfoDto, stockInfoDto.getQuantity()));

                // TODO [MyData] 나중에 다시 한번 체크해야 할지도
                // 거래내역이 없고, 기준일이 있는 경우, 기준일의 데이터를 만든다.
                myDataJsonService.forceCreateMyDataForReferenceDates(stockInfoDto.getStockCode(), processingJsonMyDataStocks);

                // 기준 날짜로 정렬 DESC (최신 날짜가 맨 위로 올라오게)
                processingJsonMyDataStocks.sort(Comparator.comparing(JsonMyDataStock::getReferenceDate).reversed());

                jsonStocksMapByMyDataProdCode.put(stockInfoDto.getMyDataProdCode(), processingJsonMyDataStocks);
            });
    }

    private List<JsonMyDataStock> filterJsonMyDataStockListLessThanLastLocalDate(
        List<JsonMyDataStock> jsonMyDataStocks,
        LocalDate lastLocalDate
    ) {
        // 기준 날짜 이후의 데이터는 제거한다, 다시 계산할거니깐.
        return jsonMyDataStocks
            .stream()
            .filter(jsonMyDataStock -> jsonMyDataStock.getReferenceDate().isBefore(lastLocalDate))
            .collect(Collectors.toList());
    }

    private List<JsonMyDataStock> createNewJsonMyDataStocksFromTransactions(
        List<AccountTransactionDto> accountTransactionDtos,
        IntermediateUserHoldingStockDto stockInfoDto,
        List<JsonMyDataStock> currentJsonMyDataStocks,
        LocalDateTime theDayBeforeLastUpdatedAt
    ) {

        final AtomicLong quantity = new AtomicLong(stockInfoDto.getQuantity());
        final List<JsonMyDataStock> newJsonMyDataStocks = myDataJsonService.createNewJsonMyDataStocks(
            accountTransactionDtos,
            stockInfoDto,
            theDayBeforeLastUpdatedAt,
            quantity
        );

        updateStockName(currentJsonMyDataStocks, stockInfoDto.getStockName());

        if (isNoChangesButHaveCurrentData(currentJsonMyDataStocks, newJsonMyDataStocks)) {
            return updateCurrentJsonMyDataStocksToTheLatest(currentJsonMyDataStocks, stockInfoDto);
        }

        currentJsonMyDataStocks.addAll(0, newJsonMyDataStocks); // Order by referenceDate desc
        currentJsonMyDataStocks.add(0, createTodayJsonMyDataStock(stockInfoDto, currentJsonMyDataStocks.get(0)));

        return myDataJsonService.mergeJsonMyDataStocksInTheSameReferenceDate(currentJsonMyDataStocks);
    }

    private JsonMyDataStock createTodayJsonMyDataStock(IntermediateUserHoldingStockDto stockInfoDto, JsonMyDataStock jsonMyDataStock) {
        return myDataJsonService.createTodayJsonMyDataStock(stockInfoDto, jsonMyDataStock.getQuantity());
    }

    private List<JsonMyDataStock> updateCurrentJsonMyDataStocksToTheLatest(
        List<JsonMyDataStock> currentJsonMyDataStocks,
        IntermediateUserHoldingStockDto stockInfoDto
    ) {
        final JsonMyDataStock latestJsonMyDataStock = currentJsonMyDataStocks.get(0);

        if (KoreanDateTimeUtil.isInThisYear(latestJsonMyDataStock.getReferenceDate())) {
            // 이번 년도면 마지막 데이터를 최신 날짜로 업데이트를 해 주고
            latestJsonMyDataStock.setReferenceDate(KoreanDateTimeUtil.getTodayLocalDate().minusDays(ONE_DAY));
            latestJsonMyDataStock.setRegisterDate(KoreanDateTimeUtil.getTodayLocalDate());
            return currentJsonMyDataStocks;
        }

        if (KoreanDateTimeUtil.isInLastYear(latestJsonMyDataStock.getReferenceDate())) {
            // 오늘 데이터를 넣어준다.
            currentJsonMyDataStocks.add(0,
                myDataJsonService.createYesterdayJsonMyDataStock(stockInfoDto, latestJsonMyDataStock.getQuantity()));
        }

        return currentJsonMyDataStocks;
    }

    private void updateStockName(List<JsonMyDataStock> currentJsonMyDataStocks, String stockName) {
        currentJsonMyDataStocks.forEach(jsonMyDataStock -> jsonMyDataStock.setName(stockName));
    }

    private boolean isNoChangesButHaveCurrentData(List<JsonMyDataStock> currentJsonMyDataStocks, List<JsonMyDataStock> newJsonMyDataStocks) {
        // 다음 인증시 변동이 없다면, 기준일/등록일만 최신화 해준다.
        return newJsonMyDataStocks.isEmpty() && !currentJsonMyDataStocks.isEmpty();
    }

    private List<JsonMyDataStock> flatMap(Map<String, List<JsonMyDataStock>> jsonStocksMapByMyDataProdCode) {
        return jsonStocksMapByMyDataProdCode.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream())
            .toList();
    }

    public void deleteAllByUserId(Long userId) {
        myDataSummaryRepository.deleteAllByUserId(userId);
    }

    public MyDataSummary save(MyDataSummary myDataSummary) {
        return myDataSummaryRepository.save(myDataSummary);
    }

    public boolean hasCreatedMyDataDuring(Long userId, LocalDateTime startWeekDateTime) {
        return myDataSummaryRepository.existsByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            userId,
            startWeekDateTime,
            startWeekDateTime.plusWeeks(1)
        );
    }
}
