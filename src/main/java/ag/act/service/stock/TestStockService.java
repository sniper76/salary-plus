package ag.act.service.stock;

import ag.act.converter.stock.TestStockConverter;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.TestStock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.model.JsonTestStockUser;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.parser.DateTimeParser;
import ag.act.repository.TestStockRepository;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserService;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestStockService {
    private static final long DEFAULT_QUANTITY = 500L;
    private final TestStockRepository testStockRepository;
    private final TestStockConverter testStockConverter;
    private final UserService userService;
    private final UserHoldingStockService userHoldingStockService;
    private final SolidarityLeaderService solidarityLeaderService;
    private final SolidarityService solidarityService;
    private final MyDataSummaryService myDataSummaryService;
    private final StockReferenceDateService stockReferenceDateService;

    public List<Stock> getStocks() {
        return getTestStocks().stream().map(testStockConverter::convert).toList();
    }

    public List<TestStock> getTestStocks() {
        return testStockRepository.findAll();
    }

    public Optional<TestStock> findByCode(String stockCode) {
        return testStockRepository.findByCode(stockCode);
    }

    public TestStock save(TestStock testStock) {
        return testStockRepository.save(testStock);
    }

    public void syncUserHoldingStock(TestStock savedTestStock) {
        getValidUsers(savedTestStock)
            .forEach(jsonTestStockUser ->
                getUserByNameAndBirthDate(jsonTestStockUser)
                    .ifPresent(user -> createUserHoldingStockIfNotFoundWithCatch(savedTestStock, user))
            );
    }

    private void createUserHoldingStockIfNotFoundWithCatch(TestStock savedTestStock, User user) {
        try {
            createUserHoldingStockIfNotFound(savedTestStock, user);
        } catch (Exception e) {
            log.warn(
                "Failed to find or create UserHoldingStock with TestStock for userId: {}, stockCode: {}",
                user.getId(),
                savedTestStock.getCode(),
                e
            );
        }
    }

    private void createUserHoldingStockIfNotFound(TestStock savedTestStock, User user) {
        user.getUserHoldingStocks()
            .stream()
            .filter(userHoldingStock -> savedTestStock.getCode().equalsIgnoreCase(userHoldingStock.getStockCode()))
            .findFirst()
            .orElseGet(() -> {
                UserHoldingStock userHoldingStock = new UserHoldingStock();
                userHoldingStock.setUserId(user.getId());
                userHoldingStock.setStockCode(savedTestStock.getCode());
                userHoldingStock.setQuantity(DEFAULT_QUANTITY);
                userHoldingStock.setDisplayOrder(100_000);
                userHoldingStock.setPurchasePrice(0L);
                userHoldingStock.setCashQuantity(0L);
                userHoldingStock.setSecureLoanQuantity(0L);
                userHoldingStock.setCreditQuantity(0L);

                final UserHoldingStock savedUserHoldingStock = userHoldingStockService.save(userHoldingStock);
                user.getUserHoldingStocks().add(savedUserHoldingStock);

                return savedUserHoldingStock;
            });
    }

    public void syncSolidarityLeader(TestStock testStock) {

        if (!isValidSolidarityLeader(testStock)) {
            return;
        }

        final Optional<User> user = getUserByNameAndBirthDate(testStock.getSolidarityLeader());

        if (user.isEmpty()) {
            return;
        }

        final Long userId = user.get().getId();

        solidarityLeaderService.findLeader(testStock.getCode())
            .ifPresentOrElse(
                leader -> updateLeaderUserId(userId, leader),
                () -> createNewLeader(testStock, userId)
            );

    }

    private boolean isValidSolidarityLeader(TestStock testStock) {
        return testStock.getSolidarityLeader() != null
            && StringUtils.isNotBlank(testStock.getSolidarityLeader().getName())
            && StringUtils.isNotBlank(testStock.getSolidarityLeader().getBirthDate());
    }

    private void createNewLeader(TestStock testStock, Long userId) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(testStock.getCode());
        final SolidarityLeader leader = new SolidarityLeader();
        leader.setUserId(userId);
        leader.setSolidarity(solidarity);
        leader.setMessage("안녕하세요. QA를 위한 주주대표입니다. 잘 부탁드립니다.");

        final SolidarityLeader savedLeader = solidarityLeaderService.save(leader);
        solidarity.setSolidarityLeader(savedLeader);
    }

    private void updateLeaderUserId(Long userId, SolidarityLeader leader) {
        leader.setUserId(userId);
        solidarityLeaderService.save(leader);
    }

    private Optional<User> getUserByNameAndBirthDate(JsonTestStockUser jsonTestStockUser) {
        if (!isValidJsonTestStockUser(jsonTestStockUser)) {
            return Optional.empty();
        }

        try {
            return userService.getActiveUserByNameAndBirthDate(
                jsonTestStockUser.getName(),
                DateTimeParser.parseDate(jsonTestStockUser.getBirthDate())
            );
        } catch (Exception e) {
            log.warn("Failed to find User by name and birthDate: {}", jsonTestStockUser, e);
            return Optional.empty();
        }
    }

    private boolean isValidJsonTestStockUser(JsonTestStockUser jsonTestStockUser) {
        return jsonTestStockUser != null
            && StringUtils.isNotBlank(jsonTestStockUser.getName())
            && StringUtils.isNotBlank(jsonTestStockUser.getBirthDate());
    }

    public void syncMyDataSummary(TestStock testStock) {
        getValidUsers(testStock)
            .stream()
            .map(this::getUserByNameAndBirthDate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(user ->
                myDataSummaryService.findByUserId(user.getId())
                    .ifPresentOrElse(
                        summary -> updateMyDataSummary(testStock, summary),
                        () -> createMyDataSummary(user, testStock)
                    )
            );
    }

    private MyDataSummary updateMyDataSummary(TestStock testStock, MyDataSummary myDataSummary) {
        final List<JsonMyDataStock> jsonMyDataStocks = getNewJsonMyDataStocksForTestStock(testStock);
        jsonMyDataStocks.addAll(filterJsonMyDataStocksWithoutTestStock(testStock, myDataSummary.getJsonMyData()));

        myDataSummary.getJsonMyData().setJsonMyDataStockList(jsonMyDataStocks);
        return myDataSummaryService.save(myDataSummary);
    }

    private List<JsonMyDataStock> filterJsonMyDataStocksWithoutTestStock(TestStock testStock, JsonMyData jsonMyData) {
        return jsonMyData.getJsonMyDataStockList()
            .stream()
            .filter(jsonMyDataStock -> !jsonMyDataStock.getCode().equals(testStock.getCode()))
            .toList();
    }

    private List<JsonMyDataStock> getNewJsonMyDataStocksForTestStock(TestStock testStock) {
        final List<JsonMyDataStock> jsonMyDataStocks = new ArrayList<>();
        jsonMyDataStocks.add(createTodayJsonMyDataStock(testStock));
        jsonMyDataStocks.addAll(createJsonMyDataStockBasedOnReferenceDates(testStock));
        return jsonMyDataStocks;
    }

    private MyDataSummary createMyDataSummary(User user, TestStock testStock) {
        final List<JsonMyDataStock> jsonMyDataStocks = getNewJsonMyDataStocksForTestStock(testStock);

        final JsonMyData jsonMyData = new JsonMyData();
        jsonMyData.setJsonMyDataStockList(jsonMyDataStocks);

        final MyDataSummary myDataSummary = new MyDataSummary();
        myDataSummary.setLoanPrice(0L);
        myDataSummary.setPensionPaidAmount(0L);
        myDataSummary.setUserId(user.getId());
        myDataSummary.setJsonMyData(jsonMyData);

        final MyDataSummary savedMyDataSummary = myDataSummaryService.save(myDataSummary);

        user.setMyDataSummary(savedMyDataSummary);
        userService.saveUser(user);

        return savedMyDataSummary;
    }

    private List<JsonMyDataStock> createJsonMyDataStockBasedOnReferenceDates(TestStock testStock) {
        return stockReferenceDateService.getStockReferenceDatesWithinThreeMonths(testStock.getCode())
            .stream()
            .map(StockReferenceDate::getReferenceDate)
            .map(referenceDate -> createJsonMyDataStock(testStock, referenceDate))
            .toList();
    }

    private JsonMyDataStock createTodayJsonMyDataStock(TestStock testStock) {
        return createJsonMyDataStock(testStock, KoreanDateTimeUtil.getTodayLocalDate());
    }

    private JsonMyDataStock createJsonMyDataStock(TestStock testStock, LocalDate referenceDate) {
        final Stock stock = testStockConverter.convert(testStock);
        return JsonMyDataStock.builder()
            .code(testStock.getCode())
            .myDataProdCode(stock.getStandardCode())
            .name(testStock.getName())
            .quantity(DEFAULT_QUANTITY)
            .registerDate(LocalDate.now())
            .referenceDate(referenceDate)
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private List<JsonTestStockUser> getValidUsers(TestStock savedTestStock) {
        Set<JsonTestStockUser> users = new HashSet<>();

        if (CollectionUtils.isNotEmpty(savedTestStock.getUsers())) {
            users.addAll(savedTestStock.getUsers());
        }

        if (savedTestStock.getSolidarityLeader() != null) {
            users.add(savedTestStock.getSolidarityLeader());
        }

        return users.stream().toList();
    }
}
