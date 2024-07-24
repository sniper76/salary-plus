package ag.act.api.batch;

import ag.act.api.admin.datamatrix.AbstractGetUserRetentionWeeklyCsvApiIntegrationTest;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.BatchLog;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DateTimeUtil;
import ag.act.util.FilenameUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ag.act.TestUtil.getPathFrom;
import static ag.act.enums.FileType.MATRIX;
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateAllUsersSignatureOpportunityOfAllDigitalDocumentsCountCsvBatchIntegrationTest
    extends AbstractGetUserRetentionWeeklyCsvApiIntegrationTest {
    private static final String TARGET_API = "/api/batch/data-matrices/user-retention-weekly/{csvDataBatchType}";
    private static final int WEEKS_SINCE_RENEWAL = 3;
    private static final int PREPARED_MEMBER_COUNT = 3;
    private static final int STANDARD_INCREMENT = 3; // 임의 숫자

    private final LocalDate referenceDate = KoreanDateTimeUtil.getYesterdayLocalDate();

    private LocalDate appRenewalDate;
    private Map<String, Integer> request;

    @Captor
    private ArgumentCaptor<InputStream> csvCaptor;
    private String expectedFileFullPath;

    private List<User> users;
    private LocalDate week1;
    private LocalDate week2;
    private LocalDate week3;
    private LocalDate week4;
    private Stock stock1;
    private Stock stock2;
    private Stock stock3;
    private LocalDate digitalDocumentStartDate;
    private LocalDate digitalDocumentEndDate;

    @BeforeEach
    void executeScenarios() {

        final int batchPeriod = 0;
        request = Map.of("batchPeriod", batchPeriod);

        appRenewalDate = referenceDate.minusWeeks(WEEKS_SINCE_RENEWAL);
        given(appRenewalDateProvider.get()).willReturn(appRenewalDate);

        users = getSortedUsers(setUsers(itUtil.findAllUsers()));
        expectedFileFullPath = getExpectedFileFullPath();

        commonScenario();
    }

    private void commonScenario() {
        final User user = users.get(0);
        final Long userId = user.getId(); // 어떤 유저여도 무관.
        stock1 = itUtil.createStock();
        stock2 = itUtil.createStock();
        final Board board1 = itUtil.createBoard(stock1);
        final Post post1 = itUtil.createPost(board1, userId);
        final Post post2 = itUtil.createPost(itUtil.createBoard(stock2), userId);

        week1 = appRenewalDate;
        week2 = week1.plusWeeks(1);
        week3 = week2.plusWeeks(1);
        week4 = week3.plusWeeks(1);

        // [week3 + 3일] ~ [week4 + 3일] 총 7일 동안 진행한 전자문서1, 2 생성
        digitalDocumentStartDate = week3.plusDays(STANDARD_INCREMENT);
        digitalDocumentEndDate = week4.plusDays(STANDARD_INCREMENT);
        createDigitalDocument(
            user, digitalDocumentStartDate, digitalDocumentEndDate, post1, stock1
        );

        createDigitalDocument(
            user, digitalDocumentStartDate, digitalDocumentEndDate, post2, stock2
        );

        // 다양한 케이스를 고려하여 잉여 데이터 추가
        // 관련없는 전자문서, 종목 추가
        stock3 = itUtil.createStock();
        final Post post3 = itUtil.createPost(itUtil.createBoard(stock3), userId);
        createDigitalDocument(user, digitalDocumentStartDate, digitalDocumentEndDate, post3, stock3);

        // stock1 에 해당하는 전자문서 추가 (중복 카운트를 허용하지 않는지 확인을 위함)
        final Post post4 = itUtil.createPost(board1, userId);
        createDigitalDocument(user, digitalDocumentStartDate, digitalDocumentEndDate, post4, stock1);
    }

    private String getExpectedFileFullPath() {
        return String.format(
            "%s/%s/%s",
            MATRIX.getPathPrefix(),
            DateTimeUtil.formatLocalDate(referenceDate, "yyyy/MM/dd"),
            FilenameUtil.getFilenameWithDate(
                ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD.getFileName(),
                "csv",
                referenceDate
            )
        );
    }

    private List<String> getExpectedHeaders() {
        return Stream.iterate(
                appRenewalDate,
                week -> week.isBefore(referenceDate.plusDays(1)),
                week -> week.plusWeeks(1)
            )
            .map(week -> "," + week + TILDE + DateTimeUtil.getDateBeforeNextWeek(week))
            .toList();
    }

    @NotNull
    private String getExpectedCsvString(String expectedRow1, String expectedRow2, String expectedRow3, String expectedHeader) {
        final String expectedDataRows = String.join("\n", expectedRow1, expectedRow2, expectedRow3);
        return String.join("\n", expectedHeader, expectedDataRows) + "\n";
    }

    private List<User> setUsers(List<User> users) {
        if (users.size() < PREPARED_MEMBER_COUNT) {
            for (int i = 0; i < PREPARED_MEMBER_COUNT - users.size(); i++) {
                User user = itUtil.createUser();
                users.add(user);
            }
        }
        return users;
    }

    private List<User> getSortedUsers(List<User> users) {
        ArrayList<User> sorted = new ArrayList<>(users);
        sorted.sort(Comparator.comparing(User::getId));
        return sorted;
    }

    private List<String> scenarioForUser1() {
        List<String> expectedRow1Cells = new ArrayList<>();
        User user1 = users.get(0);

        expectedRow1Cells.add(getFirstColumnOfRecord(user1));

        expectedRow1Cells.add("0"); // user1 at week1

        expectedRow1Cells.add("0"); // user1 at week2

        // week3 : 가지고 있는 종목 2개에 대한 전자문서 서명 기회 2회
        itUtil.createUserHoldingStockHistoryOnDate(
            user1.getId(), stock1.getCode(), 500L, generateRandomDateSameOrAfterReferenceDateWithinWeek(week3, digitalDocumentStartDate));
        itUtil.createUserHoldingStockHistoryOnDate(
            user1.getId(), stock2.getCode(), generateRandomDateSameOrAfterReferenceDateWithinWeek(week3, digitalDocumentStartDate));
        LocalDate week3 = week2.plusWeeks(1);
        expectedRow1Cells.add("2"); // user1 holding stock1 and stock2 history during digital document process period at week3

        // week4 : 가지고 있는 종목 2개에 대한 전자문서 서명 기회 1회
        itUtil.createUserHoldingStockHistoryOnDate(
            user1.getId(), stock1.getCode(), generateRandomDateBeforeReferenceDateWithinWeek(week4, digitalDocumentEndDate));
        itUtil.createUserHoldingStockHistoryOnDate( // stock2 를 가지고 있는 기간에 해당 종목의 전자문서가 열리지 않음
            user1.getId(), stock2.getCode(), generateRandomDateSameOrAfterReferenceDateWithinWeek(week4, digitalDocumentEndDate));
        LocalDate week4 = week3.plusWeeks(1);
        expectedRow1Cells.add("1"); // user1 holding stock1 during digital document process period at week4

        return expectedRow1Cells;
    }

    private List<String> scenarioForUser2() {
        List<String> expectedRow2Cells = new ArrayList<>();
        User user2 = users.get(1);

        expectedRow2Cells.add(getFirstColumnOfRecord(user2));

        expectedRow2Cells.add("0"); // user2 at week1

        expectedRow2Cells.add("0"); // user2 at week2

        // week3 : 가지고 있는 종목 2개에 대한 전자문서 서명 기회 1회
        itUtil.createUserHoldingStockHistoryOnDate( // stock1 를 가지고 있는 기간에 해당 종목의 전자문서가 열리지 않음
            user2.getId(), stock1.getCode(), generateRandomDateBeforeReferenceDateWithinWeek(week3, digitalDocumentStartDate));
        itUtil.createUserHoldingStockHistoryOnDate(
            user2.getId(), stock2.getCode(), generateRandomDateSameOrAfterReferenceDateWithinWeek(week3, digitalDocumentStartDate));
        expectedRow2Cells.add("1"); // user2 holding stock2 history during digital document process period at week3

        // week4 : 가지고 있는 종목 2개에 대한 전자문서 서명 기회 2회
        itUtil.createUserHoldingStockHistoryOnDate(
            user2.getId(), stock1.getCode(), generateRandomDateBeforeReferenceDateWithinWeek(week4, digitalDocumentEndDate));
        itUtil.createUserHoldingStockHistoryOnDate(
            user2.getId(), stock2.getCode(), generateRandomDateBeforeReferenceDateWithinWeek(week4, digitalDocumentEndDate));
        expectedRow2Cells.add("2"); // user2 holding stock1 and stock2 history during digital document process period at week4

        return expectedRow2Cells;
    }

    private List<String> scenarioForUser3() {
        User user3 = users.get(2);

        List<String> expectedRow3Cells = new ArrayList<>();
        expectedRow3Cells.addAll(List.of(getFirstColumnOfRecord(user3), "0", "0", "0", "0"));
        return expectedRow3Cells;
    }

    private String getFirstColumnOfRecord(User user) {
        return user.getId() + SLASH + ((LocalDate.now().getYear() - user.getBirthDate().getYear())) / 10 * 10 + SLASH + user.getGender();
    }

    @Nested
    class WhenNonExistingCsvFile {
        private List<String> expectedHeaders;
        private List<String> expectedRow1Cells;
        private List<String> expectedRow2Cells;
        private List<String> expectedRow3Cells;
        private String expectedRow1;
        private String expectedRow2;
        private String expectedRow3;
        private String expectedHeader;
        private String expectedCsvString;

        @BeforeEach
        void setUp() {
            expectedHeaders = getExpectedHeaders();
            expectedHeader = String.join("", expectedHeaders);
            expectedRow1Cells = scenarioForUser1();
            expectedRow2Cells = scenarioForUser2();
            expectedRow3Cells = scenarioForUser3();
            expectedRow1 = String.join(",", expectedRow1Cells);
            expectedRow2 = String.join(",", expectedRow2Cells);
            expectedRow3 = String.join(",", expectedRow3Cells);
            expectedCsvString = getExpectedCsvString(expectedRow1, expectedRow2, expectedRow3, expectedHeader);

            given(itUtil.getS3ServiceMockBean().findObjectFromPrivateBucket(anyString())).willReturn(Optional.empty());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldCreateAllUsersGivenAllConditionsRetentionCsvBatch() throws Exception {
            final MvcResult response = callApi();

            then(itUtil.getS3ServiceMockBean()).should().putObject(any(UploadFilePathDto.class), csvCaptor.capture());
            assertResponse(response);
            assertCsv(csvCaptor.getValue(), expectedCsvString);
        }
    }

    @Nested
    class WhenExistingCsvFile {

        @Nested
        @DisplayName("기준일이 새로운 주차에 해당하는 경우")
        class AndNewWeek {
            private List<String> expectedHeaders;
            private List<String> expectedRow1Cells;
            private List<String> expectedRow2Cells;
            private List<String> expectedRow3Cells;
            private String expectedRow1;
            private String expectedRow2;
            private String expectedRow3;
            private String expectedHeader;
            private String expectedCsvString;


            @BeforeEach
            void setUp() {
                expectedHeaders = getExpectedHeaders();
                expectedHeader = String.join("", expectedHeaders);
                expectedRow1Cells = scenarioForUser1();
                expectedRow2Cells = scenarioForUser2();
                expectedRow3Cells = scenarioForUser3();
                expectedRow1 = String.join(",", expectedRow1Cells);
                expectedRow2 = String.join(",", expectedRow2Cells);
                expectedRow3 = String.join(",", expectedRow3Cells);
                expectedCsvString = getExpectedCsvString(expectedRow1, expectedRow2, expectedRow3, expectedHeader);

                // 미리 만들어둔 csv 더미 데이터에서 세번째 유저의 레코드 제외, 기준일을 포함한 최신 1주의 컬럼을 제외
                int size = expectedRow1Cells.size();
                List<String> headers = expectedHeaders.subList(0, expectedHeaders.size() - 1);
                List<String> row1Cells = expectedRow1Cells.subList(0, size - 1);
                List<String> row2Cells = expectedRow2Cells.subList(0, size - 1);

                String header = String.join("", headers);
                String row1 = String.join(",", row1Cells);
                String row2 = String.join(",", row2Cells);

                String csvString = String.join("\n", header, row1, row2);
                InputStream inputStream = new ByteArrayInputStream(csvString.getBytes(StandardCharsets.UTF_8));

                given(itUtil.getS3ServiceMockBean().findObjectFromPublicBucket(anyString())).willReturn(Optional.of(inputStream));
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldCreateAllUsersGivenAllConditionsRetentionCsvBatch() throws Exception {
                final MvcResult response = callApi();

                then(itUtil.getS3ServiceMockBean()).should().putObject(any(UploadFilePathDto.class), csvCaptor.capture());

                assertResponse(response);
                assertCsv(csvCaptor.getValue(), expectedCsvString);
            }
        }

        @Nested
        @DisplayName("기준일이 기존 파일의 최신 주차에 해당하는 경우")
        class AndLatestWeekFromCsv {
            private List<String> expectedHeaders;
            private List<String> expectedRow1Cells;
            private List<String> expectedRow2Cells;
            private List<String> expectedRow3Cells;
            private String expectedRow1;
            private String expectedRow2;
            private String expectedRow3;
            private String expectedHeader;
            private String expectedCsvString;


            @BeforeEach
            void setUp() {
                appRenewalDate = appRenewalDate.plusDays(1);
                given(appRenewalDateProvider.get()).willReturn(appRenewalDate);

                // expected csv , 4주차 제외
                expectedHeaders = getExpectedHeaders();
                expectedHeader = String.join("", expectedHeaders);
                expectedRow1Cells = scenarioForUser1().subList(0, 4);
                expectedRow2Cells = scenarioForUser2().subList(0, 4);
                expectedRow3Cells = scenarioForUser3().subList(0, 4);
                expectedRow1 = String.join(",", expectedRow1Cells);
                expectedRow2 = String.join(",", expectedRow2Cells);
                expectedRow3 = String.join(",", expectedRow3Cells);
                expectedCsvString = getExpectedCsvString(expectedRow1, expectedRow2, expectedRow3, expectedHeader);

                // 기존의 더미 csv
                int size = expectedRow1Cells.size();

                // 미리 만들어둔 csv 더미 데이터에서 세번째 유저의 레코드 제외, 4주차 데이터 변경
                List<String> headers = expectedHeaders.subList(0, expectedHeaders.size());
                List<String> row1Cells = expectedRow1Cells.subList(0, size);
                row1Cells.set(3, "0");
                List<String> row2Cells = expectedRow2Cells.subList(0, size);
                row2Cells.set(3, "0");

                String header = String.join("", headers);
                String row1 = String.join(",", row1Cells);
                String row2 = String.join(",", row2Cells);

                String csvString = String.join("\n", header, row1, row2);
                InputStream inputStream = new ByteArrayInputStream(csvString.getBytes(StandardCharsets.UTF_8));

                given(itUtil.getS3ServiceMockBean().findObjectFromPublicBucket(anyString())).willReturn(Optional.of(inputStream));
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldCreateCsv() throws Exception {
                final MvcResult response = callApi();

                then(itUtil.getS3ServiceMockBean()).should().putObject(any(UploadFilePathDto.class), csvCaptor.capture());

                assertResponse(response);
                assertCsv(csvCaptor.getValue(), expectedCsvString);
            }

        }

    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                post(
                    TARGET_API,
                    getPathFrom(ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD.name())
                )
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(batchXApiKey())
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertCsv(InputStream actual, String expectedCsvString) throws IOException {
        String actualCsvString = new String(actual.readAllBytes(), StandardCharsets.UTF_8);
        assertThat(actualCsvString, is(expectedCsvString));
    }

    private void assertResponse(MvcResult response) throws Exception {
        final SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );

        final String batchName =
            UserRetentionWeeklyCsvDataType.ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD.name();
        final String expectedResult = "[Batch] %s batch successfully finished. [upload path: %s]".formatted(batchName, expectedFileFullPath);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(batchName).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }
}
