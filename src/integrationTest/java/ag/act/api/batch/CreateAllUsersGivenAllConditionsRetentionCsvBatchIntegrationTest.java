package ag.act.api.batch;

import ag.act.api.admin.datamatrix.AbstractGetUserRetentionWeeklyCsvApiIntegrationTest;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.BatchLog;
import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
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
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType.ALL_USERS_GIVEN_ALL_CONDITIONS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateAllUsersGivenAllConditionsRetentionCsvBatchIntegrationTest extends AbstractGetUserRetentionWeeklyCsvApiIntegrationTest {
    private static final String TARGET_API = "/api/batch/data-matrices/user-retention-weekly/{formattedCsvDataType}";
    private static final int WEEKS_SINCE_RENEWAL = 9;
    private static final int preparedMemberCount = 3;
    private final LocalDate referenceDate = KoreanDateTimeUtil.getYesterdayLocalDate();
    private LocalDate appRenewalDate;
    private Map<String, Integer> request;

    @Captor
    private ArgumentCaptor<InputStream> csvCaptor;
    private String expectedFileFullPath;
    private List<User> users;

    @BeforeEach
    void executeScenarios() {

        final int batchPeriod = 0;
        request = Map.of("batchPeriod", batchPeriod);

        appRenewalDate = referenceDate.minusWeeks(WEEKS_SINCE_RENEWAL);
        given(appRenewalDateProvider.get()).willReturn(appRenewalDate);

        users = getSortedUsers(setUsers(itUtil.findAllUsers()));
        expectedFileFullPath = getExpectedFileFullPath();
    }

    private String getExpectedFileFullPath() {
        return String.format(
            "%s/%s/%s",
            MATRIX.getPathPrefix(),
            DateTimeUtil.formatLocalDate(referenceDate, "yyyy/MM/dd"),
            FilenameUtil.getFilenameWithDate(ALL_USERS_GIVEN_ALL_CONDITIONS.getFileName(), "csv", referenceDate));
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
        if (users.size() < preparedMemberCount) {
            for (int i = 0; i < preparedMemberCount - users.size(); i++) {
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

        LocalDate week1 = appRenewalDate;

        User user1 = users.get(0);
        updateUserCreatedAt(user1, week1.atStartOfDay());

        expectedRow1Cells.add(getFirstColumnOfRecord(user1));

        createMyDataSummary(user1, week1.atStartOfDay());
        expectedRow1Cells.add("1"); // user1 create MyData at week1

        LocalDate week2 = week1.plusWeeks(1);
        verificationDuringWeek(user1, week2);
        expectedRow1Cells.add("O"); // user1 verification history at week2

        LocalDate week3 = week2.plusWeeks(1);
        expectedRow1Cells.add("X"); // user1 not access at week3

        LocalDate week4 = week3.plusWeeks(1);
        verificationDuringWeek(user1, week4);
        expectedRow1Cells.add("O"); // user1 verification history at week4

        LocalDate week5 = week4.plusWeeks(1);
        verificationDuringWeek(user1, week5);
        signDigitalDocumentDuringWeek(user1, week5, DigitalDocumentType.DIGITAL_PROXY);
        signDigitalDocumentDuringWeek(user1, week5, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
        signDigitalDocumentDuringWeek(user1, week5, DigitalDocumentType.ETC_DOCUMENT);
        expectedRow1Cells.add("a"); // user1 signed digital proxy document at week5

        LocalDate week6 = week5.plusWeeks(1);
        expectedRow1Cells.add("X"); // user1 not access at week6

        LocalDate week7 = week6.plusWeeks(1);
        verificationDuringWeek(user1, week7);
        signDigitalDocumentDuringWeek(user1, week7, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
        signDigitalDocumentDuringWeek(user1, week7, DigitalDocumentType.ETC_DOCUMENT);
        expectedRow1Cells.add("b"); // user1 signed joint ownership document at week7

        LocalDate week8 = week7.plusWeeks(1);
        verificationDuringWeek(user1, week8);
        signDigitalDocumentDuringWeek(user1, week8, DigitalDocumentType.ETC_DOCUMENT);
        expectedRow1Cells.add("c"); // user1 signed etc document at week8

        LocalDate week9 = week8.plusWeeks(1);
        expectedRow1Cells.add("X"); // user1 not access at week9

        LocalDate week10 = week9.plusWeeks(1);
        expectedRow1Cells.add("X"); // user1 not access at week10

        return expectedRow1Cells;
    }

    private List<String> scenarioForUser2() {
        List<String> expectedRow2Cells = new ArrayList<>();

        LocalDate week1 = appRenewalDate;

        User user2 = users.get(1);
        updateUserCreatedAt(user2, week1.atStartOfDay());

        expectedRow2Cells.add(getFirstColumnOfRecord(user2));

        verificationDuringWeek(user2, week1);
        expectedRow2Cells.add("O"); // user2 register and not verification history at week1

        LocalDate week2 = week1.plusWeeks(1);
        verificationDuringWeek(user2, week2);
        signDigitalDocumentDuringWeek(user2, week2, DigitalDocumentType.DIGITAL_PROXY);
        signDigitalDocumentDuringWeek(user2, week2, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
        signDigitalDocumentDuringWeek(user2, week2, DigitalDocumentType.ETC_DOCUMENT);
        expectedRow2Cells.add("a"); // user2 signed digital proxy document at week2

        LocalDate week3 = week2.plusWeeks(1);
        expectedRow2Cells.add("X"); // user2 not access at week3

        LocalDate week4 = week3.plusWeeks(1);
        verificationDuringWeek(user2, week4);
        signDigitalDocumentDuringWeek(user2, week4, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
        expectedRow2Cells.add("b"); // user2 has verified joint ownership document at week4

        LocalDate week5 = week4.plusWeeks(1);
        expectedRow2Cells.add("X"); // user2 not access at week5

        LocalDate week6 = week5.plusWeeks(1);
        verificationDuringWeek(user2, week6);
        expectedRow2Cells.add("O"); // user2 access at week6

        LocalDate week7 = week6.plusWeeks(1);
        verificationDuringWeek(user2, week7);
        signDigitalDocumentDuringWeek(user2, week7, DigitalDocumentType.ETC_DOCUMENT);
        expectedRow2Cells.add("c"); // user2 has verified etc document at week7

        LocalDate week8 = week7.plusWeeks(1);
        expectedRow2Cells.add("X"); // user2 not access at week8

        LocalDate week9 = week8.plusWeeks(1);
        createMyDataSummary(user2, week9.atStartOfDay());
        expectedRow2Cells.add("1"); // user2 create MyData at week9

        LocalDate week10 = week9.plusWeeks(1);
        expectedRow2Cells.add("X"); // user2 not access at week10

        return expectedRow2Cells;
    }

    private List<String> scenarioForUser3() {
        LocalDate week10 = appRenewalDate.plusWeeks(WEEKS_SINCE_RENEWAL);
        User user3 = users.get(2);
        updateUserCreatedAt(user3, week10.atStartOfDay());

        List<String> expectedRow3Cells = new ArrayList<>();
        expectedRow3Cells.addAll(List.of(getFirstColumnOfRecord(user3), "", "", "", "", "", "", "", "", "", "X"));
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

                // expected csv , 10주차 제외
                expectedHeaders = getExpectedHeaders();
                expectedHeader = String.join("", expectedHeaders);
                expectedRow1Cells = scenarioForUser1().subList(0, 10);
                expectedRow2Cells = scenarioForUser2().subList(0, 10);
                expectedRow3Cells = scenarioForUser3().subList(0, 10);
                expectedRow1 = String.join(",", expectedRow1Cells);
                expectedRow2 = String.join(",", expectedRow2Cells);
                expectedRow3 = String.join(",", expectedRow3Cells);
                expectedCsvString = getExpectedCsvString(expectedRow1, expectedRow2, expectedRow3, expectedHeader);

                // 기존의 더미 csv
                int size = expectedRow1Cells.size();

                // 미리 만들어둔 csv 더미 데이터에서 세번째 유저의 레코드 제외, 10주차 제외, 9주차 데이터 변경
                List<String> headers = expectedHeaders.subList(0, expectedHeaders.size());
                List<String> row1Cells = expectedRow1Cells.subList(0, size);
                row1Cells.set(9, "X");
                List<String> row2Cells = expectedRow2Cells.subList(0, size);
                row2Cells.set(9, "X");

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
                post(TARGET_API, getPathFrom(ALL_USERS_GIVEN_ALL_CONDITIONS.name()))
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

        final String batchName = ALL_USERS_GIVEN_ALL_CONDITIONS.name();
        final String expectedResult = "[Batch] %s batch successfully finished. [upload path: %s]".formatted(batchName, expectedFileFullPath);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(batchName).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }
}
