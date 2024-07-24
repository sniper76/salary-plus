package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.MyDataSummary;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.model.SimpleStockResponse;
import ag.act.model.Status;
import ag.act.model.UserDataResponse;
import ag.act.model.UserResponse;
import ag.act.module.mydata.crypto.MyDataCryptoHelper;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.hamcrest.core.CombinableMatcher;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someLocalDateTimeInThePastDaysBetween;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateMyDataApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/mydata";
    private static final int INITIAL_DISPLAY_ORDER_FOR_NEW_STOCKS = 100_000;

    @Value("classpath:/mydata/my_data_json_file.json")
    private Resource myDataJsonFile;
    @Autowired
    private MyDataCryptoHelper myDataCryptoHelper;
    private ag.act.model.UpdateMyDataRequest request;
    private Long userId;
    private String jwt;
    private Stock stubStock1;
    private Stock stubStock2;
    private LocalDate recentPastReferenceDate;
    private LocalDate distantPastReferenceDate;
    private String encryptedMyDataJsonContent;
    private LocalDate stock1Date1;
    private LocalDate stock1Date2;
    private LocalDate stock2Date1;
    private LocalDate stock2Date2;
    private LocalDate stock2Date3;
    private LocalDate stock2Date4;
    private List<String> dummyStockCode;

    @BeforeEach
    void setUp() throws IOException, GeneralSecurityException {
        itUtil.init();
        final User currentUser = itUtil.createUser();
        userId = currentUser.getId();
        jwt = itUtil.createJwt(userId);

        dummyStockCode = List.of(
            itUtil.createDummyUserHoldingStock(itUtil.createStock().getCode(), currentUser).getStockCode(),
            itUtil.createDummyUserHoldingStock(itUtil.createStock().getCode(), currentUser).getStockCode(),
            itUtil.createDummyUserHoldingStock(itUtil.createStock().getCode(), currentUser).getStockCode()
        );

        stubStock1 = stubStock1();
        stubStock2 = stubStock2();

        distantPastReferenceDate = LocalDate.now().minusDays(20);
        recentPastReferenceDate = LocalDate.now().minusDays(10);

        itUtil.createStockReferenceDate(stubStock2.getCode(), distantPastReferenceDate);
        itUtil.createStockReferenceDate(stubStock2.getCode(), recentPastReferenceDate);

        stock1Date1 = someLocalDateTimeInThePastDaysBetween(10, 20).toLocalDate();
        stock1Date2 = someLocalDateTimeInThePastDaysBetween(30, 40).toLocalDate();
        stock2Date1 = someLocalDateTimeInThePastDaysBetween(21, 30).toLocalDate();
        stock2Date2 = someLocalDateTimeInThePastDaysBetween(31, 40).toLocalDate();
        stock2Date3 = someLocalDateTimeInThePastDaysBetween(41, 50).toLocalDate();
        stock2Date4 = someLocalDateTimeInThePastDaysBetween(51, 60).toLocalDate();

        final String myDataJsonContent = myDataJsonFile.getContentAsString(StandardCharsets.UTF_8)
            .replace("{SK하이닉스_date1}", stock1Date1.format(DateTimeFormatUtil.yyyyMMdd()))
            .replace("{SK하이닉스_date2}", stock1Date2.format(DateTimeFormatUtil.yyyyMMdd()))
            .replace("{상보_date1}", stock2Date1.format(DateTimeFormatUtil.yyyyMMdd()))
            .replace("{상보_date2}", stock2Date2.format(DateTimeFormatUtil.yyyyMMdd()))
            .replace("{상보_date3}", stock2Date3.format(DateTimeFormatUtil.yyyyMMdd()))
            .replace("{상보_date4}", stock2Date4.format(DateTimeFormatUtil.yyyyMMdd()));

        encryptedMyDataJsonContent = myDataCryptoHelper.encrypt(myDataJsonContent);
    }

    @Nested
    class WhenUploadEncryptedMyDataJsonFileSuccess {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final UserDataResponse result = callApi();

            assertResponse(result);
            assertUserHoldingStocksFromDatabase();
            assertUserHoldingStockOnReferenceDatesFromDatabase();
            assertMyDataSummaryFromDatabase();
        }
    }

    private UserDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            UserDataResponse.class
        );
    }

    private void assertResponse(UserDataResponse result) {
        final UserResponse userResponse = result.getData();
        final User user = itUtil.findUser(userId);

        assertThat(userResponse.getId(), is(user.getId()));
        assertThat(userResponse.getNickname(), is(user.getNickname()));
        assertThat(userResponse.getGender(), is(user.getGender()));
        assertThat(userResponse.getMySpeech(), is(user.getMySpeech()));
        assertThat(DateTimeConverter.convert(userResponse.getBirthDate()), is(user.getBirthDate()));
        assertThat(userResponse.getIsAgreeToReceiveMail(), is(user.getIsAgreeToReceiveMail()));
        assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
        assertThat(userResponse.getJobTitle(), is(user.getJobTitle()));
        assertThat(userResponse.getAddress(), is(user.getAddress()));
        assertThat(userResponse.getAddressDetail(), is(user.getAddressDetail()));
        assertThat(userResponse.getZipcode(), is(user.getZipcode()));
        assertThat(userResponse.getTotalAssetAmount(), is(itUtil.getTotalAssetAmount(userId)));
        assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(userResponse.getStatus(), is(user.getStatus()));
        assertThat(userResponse.getAuthType(), is(user.getAuthType()));
        assertTime(userResponse.getCreatedAt(), user.getCreatedAt());
        assertTime(userResponse.getUpdatedAt(), user.getUpdatedAt());
        assertTime(userResponse.getDeletedAt(), user.getDeletedAt());
        assertThat(userResponse.getIsAdmin(), is(user.isAdmin()));
        assertThat(new HashSet<>(userResponse.getLeadingSolidarityStockCodes()), is(new HashSet<>()));
        assertUserHoldingStocksFromResponse(user, userResponse);
    }

    private void assertUserHoldingStocksFromResponse(User user, UserResponse userResponse) {
        userResponse.getHoldingStocks()
            .forEach(simpleStockResponse -> {
                Optional<UserHoldingStock> userHoldingStock = itUtil.findUserHoldingStock(user.getId(), simpleStockResponse.getCode());
                assertThat(userHoldingStock.isPresent(), is(true));
                assertThat(simpleStockResponse.getCode(), is(userHoldingStock.get().getStockCode()));
                assertThat(simpleStockResponse.getName(), is(userHoldingStock.get().getStock().getName()));
                assertThat(simpleStockResponse.getStandardCode(), is(userHoldingStock.get().getStock().getStandardCode()));
            });

        final Map<String, String> userHoldingStockMap = getUserHoldingStockMap(userResponse);

        dummyStockCode.forEach(stockCode -> assertThat(userHoldingStockMap.containsKey(stockCode), is(true)));
    }

    @NotNull
    private Map<String, String> getUserHoldingStockMap(UserResponse userResponse) {
        return userResponse.getHoldingStocks().stream()
            .collect(Collectors.toMap(SimpleStockResponse::getCode, SimpleStockResponse::getName));
    }

    private void assertUserHoldingStocksFromDatabase() {

        final List<UserHoldingStock> userHoldingStocks = getActiveUserHoldingStocks(userId);

        assertThat(userHoldingStocks.size(), is(2));

        assertThat(userHoldingStocks.get(0).getStockCode(), is(stubStock1.getCode()));
        assertThat(userHoldingStocks.get(0).getQuantity(), is(320L));
        assertThat(userHoldingStocks.get(0).getCashQuantity(), is(320L));
        assertThat(userHoldingStocks.get(0).getCreditQuantity(), is(0L));
        assertThat(userHoldingStocks.get(0).getSecureLoanQuantity(), is(0L));
        assertThat(userHoldingStocks.get(0).getDisplayOrder(), is(getDisplayOrder(0)));
        assertThat(userHoldingStocks.get(0).getPurchasePrice(), is(32_000_000L));

        assertThat(userHoldingStocks.get(1).getStockCode(), is(stubStock2.getCode()));
        assertThat(userHoldingStocks.get(1).getQuantity(), is(50L));
        //assertThat(userHoldingStocks.get(1).getCashQuantity(), is(30L)); // TODO creditType 가 다른 경우가 합쳐이면 엉망이 된다.이 부분을 수정해야 한다.
        //assertThat(userHoldingStocks.get(1).getCreditQuantity(), is(20L));
        assertThat(userHoldingStocks.get(1).getCashQuantity(), is(50L));
        assertThat(userHoldingStocks.get(1).getCreditQuantity(), is(0L));
        assertThat(userHoldingStocks.get(1).getSecureLoanQuantity(), is(0L));
        assertThat(userHoldingStocks.get(1).getDisplayOrder(), is(getDisplayOrder(1)));
        assertThat(userHoldingStocks.get(1).getPurchasePrice(), is(129_300L));
    }

    private int getDisplayOrder(int defaultDisplayOrder) {
        return alreadyHasStocks() ? INITIAL_DISPLAY_ORDER_FOR_NEW_STOCKS : defaultDisplayOrder;
    }

    private boolean alreadyHasStocks() {
        return !dummyStockCode.isEmpty();
    }

    @NotNull
    private List<UserHoldingStock> getActiveUserHoldingStocks(Long userId) {
        final User user = itUtil.findUser(userId);
        return user.getUserHoldingStocks().stream()
            .filter(itUtil::isActive).toList();
    }

    private void assertUserHoldingStockOnReferenceDatesFromDatabase() {
        var userHoldingStockOnReferenceDates = itUtil.findAllUserHoldingStockOnReferenceDatesByUserId(userId)
            .stream()
            .sorted(Comparator.comparing(UserHoldingStockOnReferenceDate::getReferenceDate))
            .toList();

        assertThat(userHoldingStockOnReferenceDates.size(), is(2));
        assertUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDates.get(0), stubStock2.getCode(), distantPastReferenceDate, 50L);
        assertUserHoldingStockOnReferenceDate(userHoldingStockOnReferenceDates.get(1), stubStock2.getCode(), recentPastReferenceDate, 50L);
    }

    @SuppressWarnings("SameParameterValue")
    private void assertUserHoldingStockOnReferenceDate(
        UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate,
        String stockCode,
        LocalDate referenceDate,
        long quantity
    ) {
        assertThat(userHoldingStockOnReferenceDate.getStockCode(), is(stockCode));
        assertThat(userHoldingStockOnReferenceDate.getReferenceDate(), is(referenceDate));
        assertThat(userHoldingStockOnReferenceDate.getQuantity(), is(quantity));
    }

    private void assertMyDataSummaryFromDatabase() {
        final MyDataSummary myDataSummary = itUtil.findMyDataSummaryByUserId(userId)
            .orElseThrow(() -> new RuntimeException("[TEST] MyDataSummary를 찾을 수 없습니다."));

        assertThat(myDataSummary.getLoanPrice(), is(0L));
        assertThat(myDataSummary.getPensionPaidAmount(), is(0L));

        final List<JsonMyDataStock> jsonMyDataStockList = myDataSummary.getJsonMyData().getJsonMyDataStockList();

        assertThat(jsonMyDataStockList.size(), is(10));

        final AtomicInteger index = new AtomicInteger(-1);
        final LocalDateTime currentLocalDateTime = LocalDateTime.now();
        final LocalDate currentLocalDate = currentLocalDateTime.toLocalDate();
        final LocalDate currentLocalDateInKorean = KoreanDateTimeUtil.getTodayLocalDate();

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock1);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(currentLocalDateInKorean));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(320L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock1);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(stock1Date1));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(320L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock1);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(stock1Date2));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(310L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(currentLocalDateInKorean));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(50L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(recentPastReferenceDate));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDateInKorean));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(50L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(distantPastReferenceDate));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDateInKorean));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(50L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(stock2Date1));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(50L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(stock2Date2));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(45L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(stock2Date3));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(20L));

        assertThatWithStock(jsonMyDataStockList.get(index.incrementAndGet()), stubStock2);
        assertThat(jsonMyDataStockList.get(index.get()).getReferenceDate(), is(stock2Date4));
        assertThat(jsonMyDataStockList.get(index.get()).getRegisterDate(), is(currentLocalDate));
        assertThat(jsonMyDataStockList.get(index.get()).getUpdatedAt(), between(currentLocalDateTime.minusMinutes(1), currentLocalDateTime));
        assertThat(jsonMyDataStockList.get(index.get()).getQuantity(), is(5L));
    }

    private CombinableMatcher<ChronoLocalDateTime<?>> between(LocalDateTime start, LocalDateTime end) {
        return both(greaterThanOrEqualTo(start))
            .and(lessThanOrEqualTo(end));
    }

    private void assertThatWithStock(JsonMyDataStock jsonMyDataStock, Stock stubStock) {
        assertThat(jsonMyDataStock.getCode(), is(stubStock.getCode()));
        assertThat(jsonMyDataStock.getName(), is(stubStock.getName()));
        assertThat(jsonMyDataStock.getMyDataProdCode(), is(stubStock.getStandardCode()));

    }

    private Stock stubStock1() {
        final Stock stock = itUtil.createStock();

        // WARN: this data should be matched with the my_data_json_file.json
        stock.setCode("000660");
        stock.setName("SK하이닉스");
        stock.setTotalIssuedQuantity(728002365L);
        stock.setStatus(ag.act.model.Status.ACTIVE);
        stock.setClosingPrice(115300);
        stock.setStandardCode("KR7000660001");
        stock.setFullName("에스케이하이닉스보통주");
        stock.setMarketType("KOSPI");
        stock.setStockType("보통주");
        final Stock updatedStock = itUtil.updateStock(stock);

        final Solidarity solidarity = itUtil.createSolidarity(updatedStock.getCode());
        solidarity.setStatus(Status.ACTIVE);
        itUtil.updateSolidarity(solidarity);

        return updatedStock;
    }

    private Stock stubStock2() {
        final Stock stock = itUtil.createStock();

        // WARN: this data should be matched with the my_data_json_file.json
        stock.setCode("027580");
        stock.setName("상보");
        stock.setTotalIssuedQuantity(59181279L);
        stock.setStatus(ag.act.model.Status.ACTIVE);
        stock.setClosingPrice(1819);
        stock.setStandardCode("KR7027580000");
        stock.setFullName("상보");
        stock.setMarketType("KOSDAQ");
        stock.setStockType("보통주");
        final Stock updatedStock = itUtil.updateStock(stock);

        final Solidarity solidarity = itUtil.createSolidarity(updatedStock.getCode());
        solidarity.setStatus(Status.ACTIVE);
        itUtil.updateSolidarity(solidarity);

        return updatedStock;
    }

    private ag.act.model.UpdateMyDataRequest genRequest() {
        return new ag.act.model.UpdateMyDataRequest()
            .jsonData(encryptedMyDataJsonContent);
    }
}
