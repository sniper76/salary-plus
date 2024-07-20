package ag.act.api.admin.datamatrix;

import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetWeeklyPinVerificationGivenRegisterAndSpecificDigitalDocumentNotCompleteOfCampaignApiIntegrationTest
    extends AbstractGetUserRetentionWeeklyCsvApiIntegrationTest {

    private static final String TARGET_API =
        "/api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-not-complete-retention-download";
    private static final int CAMPAIGN_PROGRESS_PERIOD = 2;
    private static final int HEADER_ROW_COUNT = 1;
    private static final int EXPECTED_ROW_COUNT = HEADER_ROW_COUNT + CAMPAIGN_PROGRESS_PERIOD + 1;
    // header + campaign progress period + extra row because digital document start before friday

    private String expectedRow1;
    private String expectedRow2;

    private Stock stock;
    private DigitalDocument digitalDocument;
    private Campaign campaign;
    private final LocalDateTime digitalDocumentTargetStartDateTime = generateRandomDateFromAppRenewalDate().atStartOfDay();
    private final LocalDateTime digitalDocumentTargetEndDateTime = digitalDocumentTargetStartDateTime.plusWeeks(CAMPAIGN_PROGRESS_PERIOD);
    private final LocalDateTime referenceStartDateTime = appRenewalDate.atStartOfDay();

    @BeforeEach
    void executeScenarios() {
        stock = itUtil.createStock();
        final Post post = createPost(stock);
        digitalDocument = createDigitalDocument(digitalDocumentTargetStartDateTime, digitalDocumentTargetEndDateTime, post, stock);
        campaign = createCampaign(stock, post);

        expectedRow1 = scenarioForWeek1();
        expectedRow2 = scenarioForWeek2();
    }

    private String scenarioForWeek1() {
        List<String> expectedRow1Cells = new ArrayList<>();

        User user1 = itUtil.createUser();
        User user2 = itUtil.createUser();
        User user3 = itUtil.createUser();
        User user5 = itUtil.createUser();
        User user6 = itUtil.createUser();
        User user7 = itUtil.createUser();

        LocalDate week1 = referenceStartDateTime.toLocalDate();
        expectedRow1Cells.add(week1 + TILDE + getLastDateOfWeek(week1));

        // registered before digital document progress period and reference week
        registerAt(user5, appRenewalDate);

        // registered after digital document progress and reference week
        registerAt(user7, digitalDocumentTargetEndDateTime.plusWeeks(1).toLocalDate());

        // 4 registered during week1
        final LocalDate digitalDocumentStartDate = digitalDocumentTargetStartDateTime.toLocalDate();
        LocalDate yesterdayOfDigitalDocumentStartDate = digitalDocumentStartDate.minusDays(1);

        // register in digital document progress period - should be count if not save digital document
        registerAt(user1, digitalDocumentStartDate.plusDays(1)); // count
        registerAt(user2, digitalDocumentStartDate); // count
        registerAt(user6, digitalDocumentStartDate); // not count

        // register before digital document progress period but in reference week - should be not count
        registerAt(user3, yesterdayOfDigitalDocumentStartDate); // not count

        // 2 saved digital document
        saveDigitalDocument(user5, digitalDocumentStartDate, digitalDocument, stock);
        saveDigitalDocument(user6, digitalDocumentStartDate, digitalDocument, stock);

        addToList(expectedRow1Cells, 2); // user1 and user2 not saved digital document and new register at week 1

        LocalDate week2 = week1.plusWeeks(1);
        verificationDuringWeek(user1, week2);
        verificationDuringWeek(user2, week2);
        verificationDuringWeek(user3, week2);
        verificationDuringWeek(user5, week2);
        verificationDuringWeek(user6, week2);
        verificationDuringWeek(user7, week2);

        addToList(expectedRow1Cells, 2); // user1 and user2 not saved digital document and retained

        LocalDate week3 = week2.plusWeeks(1);
        verificationDuringWeek(user1, week3);
        verificationDuringWeek(user3, week3);
        verificationDuringWeek(user5, week3);
        verificationDuringWeek(user6, week3);
        verificationDuringWeek(user7, week3);

        addToList(expectedRow1Cells, 1); // user1 not saved digital document and retained

        LocalDate week4 = week3.plusWeeks(1);
        verificationDuringWeek(user3, week4);
        verificationDuringWeek(user5, week4);
        verificationDuringWeek(user6, week4);
        verificationDuringWeek(user7, week4);

        addToList(expectedRow1Cells, 0); // no one not saved digital document

        LocalDate week5 = week4.plusWeeks(1);
        verificationDuringWeek(user1, week5);
        verificationDuringWeek(user3, week5);
        verificationDuringWeek(user5, week5);
        verificationDuringWeek(user6, week5);
        verificationDuringWeek(user7, week5);

        addToList(expectedRow1Cells, 1); // user1 saved digital document and retained

        LocalDate week6 = week5.plusWeeks(1);
        addToList(expectedRow1Cells, 0); // no active users.

        LocalDate week7 = week6.plusWeeks(1);
        addToList(expectedRow1Cells, 0); // no active users.

        LocalDate week8 = week7.plusWeeks(1);
        verificationDuringWeek(user1, week8);
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user3, week8);

        addToList(expectedRow1Cells, 2); // user1, user2 not saved digital document and retained

        addToList(expectedRow1Cells, 0); // no verification at week 9
        addToList(expectedRow1Cells, 0); // no verification at week 10

        return String.join(",", expectedRow1Cells);
    }

    private String scenarioForWeek2() {
        List<String> expectedRow2Cells = new ArrayList<>();

        LocalDate week2 = referenceStartDateTime.toLocalDate().plusWeeks(1);
        expectedRow2Cells.add(week2 + TILDE + getLastDateOfWeek(week2));

        // should be empty for week1
        expectedRow2Cells.add(PLACEHOLDER);

        User user1 = itUtil.createUser();
        User user2 = itUtil.createUser();
        User user3 = itUtil.createUser();
        User user4 = itUtil.createUser();
        User user5 = itUtil.createUser();
        User user6 = itUtil.createUser();
        User user7 = itUtil.createUser();

        final LocalDate digitalDocumentStartDateNextWeekDate = digitalDocumentTargetStartDateTime.toLocalDate().plusWeeks(1);

        // 7 registered during week2
        registerAt(user1, digitalDocumentStartDateNextWeekDate);
        registerAt(user2, digitalDocumentStartDateNextWeekDate);
        registerAt(user3, digitalDocumentStartDateNextWeekDate);
        registerAt(user4, digitalDocumentStartDateNextWeekDate);
        registerAt(user5, digitalDocumentStartDateNextWeekDate);
        registerAt(user6, digitalDocumentStartDateNextWeekDate);
        registerAt(user7, digitalDocumentStartDateNextWeekDate);

        // 3 saved digital document
        saveDigitalDocument(user1, digitalDocumentStartDateNextWeekDate, digitalDocument, stock);
        saveDigitalDocument(user2, digitalDocumentStartDateNextWeekDate, digitalDocument, stock);
        saveDigitalDocument(user3, digitalDocumentStartDateNextWeekDate, digitalDocument, stock);

        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 not saved digital document

        LocalDate week3 = week2.plusWeeks(1);
        verificationDuringWeek(user1, week3);
        verificationDuringWeek(user3, week3);
        verificationDuringWeek(user4, week3);
        verificationDuringWeek(user5, week3);
        verificationDuringWeek(user6, week3);
        verificationDuringWeek(user7, week3);

        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 not saved digital document and retained

        LocalDate week4 = week3.plusWeeks(1);
        verificationDuringWeek(user4, week4);
        verificationDuringWeek(user5, week4);

        addToList(expectedRow2Cells, 2); // user4, user5 not saved digital document and retained

        LocalDate week5 = week4.plusWeeks(1);
        verificationDuringWeek(user1, week5);

        addToList(expectedRow2Cells, 0); // no one not saved digital document and retained

        LocalDate week6 = week5.plusWeeks(1);
        verificationDuringWeek(user1, week6);
        verificationDuringWeek(user3, week6);
        verificationDuringWeek(user5, week6);
        verificationDuringWeek(user7, week6);

        addToList(expectedRow2Cells, 2); // user5, user7 not saved digital document and retained

        LocalDate week7 = week6.plusWeeks(1);
        addToList(expectedRow2Cells, 0); // no active users.

        LocalDate week8 = week7.plusWeeks(1);
        verificationDuringWeek(user1, week8);
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user3, week8);
        verificationDuringWeek(user4, week8);
        verificationDuringWeek(user5, week8);
        verificationDuringWeek(user6, week8);
        verificationDuringWeek(user7, week8);

        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 not saved digital document and retained

        addToList(expectedRow2Cells, 0); // no verification at week 9
        addToList(expectedRow2Cells, 0); // no verification at week 10

        return String.join(",", expectedRow2Cells);
    }

    private void assertCsv(String actual) {
        String[] rows = actual.split("\n");
        assertThat(rows.length, is(EXPECTED_ROW_COUNT)); // header row + weekly retention data rows

        assertThat(rows[1], is(expectedRow1));
        assertThat(rows[2], is(expectedRow2));

        IntStream.range(3, rows.length).forEach(i -> {
            String expectedEnd = generateExpectedRowForWeekWithoutRegister(i);
            assertThat(rows[i], endsWith(expectedEnd));
        });
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldDownloadAllData() throws Exception {
        assertCsv(callApiAndGetResult());
    }

    private String callApiAndGetResult() throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(
                get(TARGET_API, campaign.getId())
                    .contentType(MediaType.ALL)
                    .accept(MediaType.ALL)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }
}
