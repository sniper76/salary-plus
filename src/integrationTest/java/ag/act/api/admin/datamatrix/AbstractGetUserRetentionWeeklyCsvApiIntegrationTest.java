package ag.act.api.admin.datamatrix;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Campaign;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ag.act.util.DateTimeUtil.getRandomDateTime;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

//TODO: 모든 csv 다운로드가 배치저장+다운로드로 변경되면 수정 예정
public abstract class AbstractGetUserRetentionWeeklyCsvApiIntegrationTest extends AbstractCommonIntegrationTest {
    protected static final String PLACEHOLDER = "";
    protected static final String TILDE = "~";
    protected static final String SLASH = "/";
    protected static final int WEEKS_SINCE_RENEWAL = 10;
    protected static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    protected User adminUser;
    protected String jwt;
    protected LocalDate appRenewalDate;

    public AbstractGetUserRetentionWeeklyCsvApiIntegrationTest() {
        final LocalDate todayLocalDate = DateTimeUtil.getTodayLocalDate();
        final LocalDate fridayThisWeek = todayLocalDate.with(DayOfWeek.FRIDAY);

        if (fridayThisWeek.isBefore(todayLocalDate)) {
            appRenewalDate = fridayThisWeek.minusWeeks(WEEKS_SINCE_RENEWAL - 1);
        } else {
            appRenewalDate = fridayThisWeek.minusWeeks(WEEKS_SINCE_RENEWAL);
        }
    }

    @BeforeEach
    void setUp() {
        itUtil.init();
        mockAppRenewalDate();
        dbCleaner.clean();

        adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    private void mockAppRenewalDate() {
        given(appRenewalDateProvider.get()).willReturn(appRenewalDate);
    }


    protected void addToList(List<String> expectedCells, double element) {
        BigDecimal bigDecimalValue = new BigDecimal(Double.toString(element));
        bigDecimalValue = bigDecimalValue.setScale(2, RoundingMode.HALF_UP);

        expectedCells.add(
            decimalFormat.format(bigDecimalValue)
        );
    }

    protected LocalDate getLastDateOfWeek(LocalDate weekStart) {
        return DateTimeUtil.getDateBeforeNextWeek(weekStart);
    }

    protected void registerDuringWeek(User user, LocalDate weekStart) {
        LocalDate randomRegisterDate = generateRandomDateWithinWeek(weekStart);

        itUtil.createMyDataSummary(user);
        createUserVerificationHistoryGivenCreatedAt(
            user, VerificationType.PIN, VerificationOperationType.REGISTER, randomRegisterDate
        );
    }

    protected void registerAt(User user, LocalDate date) {
        itUtil.createMyDataSummary(user);
        createUserVerificationHistoryGivenCreatedAt(
            user, VerificationType.PIN, VerificationOperationType.REGISTER, date
        );
    }

    protected void updateUserCreatedAt(User user, LocalDateTime createdAt) {
        user.setCreatedAt(createdAt);
        itUtil.updateUser(user);
    }

    protected void createMyDataSummary(User user, LocalDateTime createdAt) {
        MyDataSummary myDataSummary = itUtil.createMyDataSummary(user);
        myDataSummary.setCreatedAt(createdAt);
        itUtil.updateMyDataSummary(myDataSummary);
    }

    protected void verificationDuringWeek(User user, LocalDate weekStart) {
        LocalDate randomVerificationDate = generateRandomDateWithinWeek(weekStart);

        createUserVerificationHistoryGivenCreatedAt(
            user, VerificationType.PIN, VerificationOperationType.VERIFICATION, randomVerificationDate
        );
    }

    protected void verificationForThreeWeeksInARow(User user, LocalDate weekStart) {
        verificationDuringWeek(user, weekStart);
        verificationDuringWeek(user, weekStart.plusWeeks(1));
        verificationDuringWeek(user, weekStart.plusWeeks(2));
    }

    protected void digitalDocumentSaveDuringWeek(User user, LocalDate weekStart) {
        LocalDate randomDate = generateRandomDateWithinWeek(weekStart);

        createUserVerificationHistoryGivenCreatedAt(
            user, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, randomDate
        );
    }

    protected void digitalDocumentSaveDuringWeek(User user, LocalDate weekStart, Long digitalDocumentId) {
        LocalDate randomDate = generateRandomDateWithinWeek(weekStart);

        createUserVerificationHistoryGivenCreatedAt(
            user, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, randomDate, digitalDocumentId
        );
    }

    protected void signDigitalDocumentDuringWeek(User user, LocalDate weekStart, DigitalDocumentType digitalDocumentType) {
        LocalDate randomDate = generateRandomDateWithinWeek(weekStart);

        createUserVerificationHistoryGivenCreatedAt(
            user, VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, randomDate
        );

        createDigitalDocumentUserGivenCreatedAt(user, digitalDocumentType, randomDate);
    }

    private void createDigitalDocumentUserGivenCreatedAt(User user, DigitalDocumentType digitalDocumentType, LocalDate randomDate) {
        Stock stock = itUtil.createStock();
        Post post = itUtil.createPost(itUtil.createBoard(stock), adminUser.getId());
        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, adminUser, digitalDocumentType);

        DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, user, stock);
        digitalDocumentUser.setCreatedAt(getRandomDateTime(randomDate));
        itUtil.updateDigitalDocumentUser(digitalDocumentUser);
    }

    protected void createUserVerificationHistoryGivenCreatedAt(
        User user,
        VerificationType verificationType,
        VerificationOperationType operationType,
        LocalDate createdAtDate
    ) {
        var savedUserVerificationHistory = itUtil.createUserVerificationHistory(
            user, verificationType, operationType
        );
        savedUserVerificationHistory.setCreatedAt(getRandomDateTime(createdAtDate));
        itUtil.updateUserVerificationHistory(savedUserVerificationHistory);
    }

    protected void createUserVerificationHistoryGivenCreatedAt(
        User user,
        VerificationType verificationType,
        VerificationOperationType operationType,
        LocalDate createdAtDate,
        Long digitalDocumentUserId
    ) {
        var savedUserVerificationHistory = itUtil.createUserVerificationHistory(
            user, verificationType, operationType, digitalDocumentUserId
        );
        savedUserVerificationHistory.setCreatedAt(getRandomDateTime(createdAtDate));
        itUtil.updateUserVerificationHistory(savedUserVerificationHistory);
    }

    protected LocalDate generateRandomDateWithinWeek(LocalDate weekStart) {
        return weekStart.plusDays(someIntegerBetween(0, 6));
    }

    protected LocalDate generateRandomDateFromAppRenewalDate() {
        return appRenewalDate.plusDays(someIntegerBetween(2, 6));
    }

    protected String generateExpectedRowForWeekWithoutRegister(int week) {
        return IntStream.rangeClosed(1, WEEKS_SINCE_RENEWAL)
            .mapToObj(i -> i < week ? PLACEHOLDER : "0")
            .collect(Collectors.joining(","));
    }

    protected Post createPost(Stock stock) {
        final User admin = itUtil.createAdminUser();
        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.ETC);
        return itUtil.createPost(board, admin.getId());
    }

    protected DigitalDocument createDigitalDocument(LocalDateTime startTime, LocalDateTime endTime, Post post, Stock stock) {
        final User acceptorUser = itUtil.createAcceptorUser();
        final DigitalDocumentType digitalDocumentType = someEnum(DigitalDocumentType.class);

        return itUtil.createDigitalDocument(
            post,
            stock,
            acceptorUser,
            digitalDocumentType,
            KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDateTime(startTime),
            KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDateTime(endTime),
            DateTimeUtil.getEndOfThisYearLocalDate()
        );
    }

    protected DigitalDocument createDigitalDocument(
        User acceptorUser, LocalDate startDate, LocalDate endDate, Post post, Stock stock
    ) {
        final DigitalDocumentType digitalDocumentType = someEnum(DigitalDocumentType.class);
        return createDigitalDocument(digitalDocumentType, acceptorUser, startDate, endDate, post, stock);
    }

    protected DigitalDocument createDigitalDocument(
        DigitalDocumentType digitalDocumentType, User acceptorUser, LocalDate startDate, LocalDate endDate, Post post, Stock stock
    ) {
        return itUtil.createDigitalDocument(
            post,
            stock,
            acceptorUser,
            digitalDocumentType,
            KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(startDate),
            KoreanDateTimeUtil.toUtcDateTimeFromKoreanLocalDate(endDate),
            DateTimeUtil.getEndOfThisYearLocalDate()
        );
    }

    protected Campaign createCampaign(Stock stock, Post post) {
        final StockGroup stockGroup = createStockGroup(stock);
        return itUtil.createCampaign(someString(5), post.getId(), stockGroup.getId());
    }

    private StockGroup createStockGroup(Stock stock) {
        final StockGroup stockGroup = itUtil.createStockGroup(someString(5));
        itUtil.createStockGroupMapping(stock.getCode(), stockGroup.getId());
        return stockGroup;
    }

    protected void saveDigitalDocument(User user, LocalDate week, DigitalDocument digitalDocument, Stock stock) {
        DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, user, stock);
        digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.COMPLETE);
        itUtil.updateDigitalDocumentUser(digitalDocumentUser);
        digitalDocumentSaveDuringWeek(user, week, digitalDocumentUser.getId());
    }

    protected LocalDate generateRandomDateBeforeReferenceDateWithinWeek(LocalDate weekStart, LocalDate referenceDate) {
        final long daysDifference = ChronoUnit.DAYS.between(weekStart, referenceDate);
        return referenceDate.minusDays(someLongBetween(1L, daysDifference));
    }

    protected LocalDate generateRandomDateSameOrAfterReferenceDateWithinWeek(LocalDate weekStart, LocalDate referenceDate) {
        final long daysDifference = ChronoUnit.DAYS.between(referenceDate, DateTimeUtil.getDateBeforeNextWeek(weekStart));
        return referenceDate.plusDays(someLongBetween(0L, daysDifference));
    }
}
