package ag.act.api.admin.datamatrix;

import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetForThreeWeeksInARowPinVerificationGivenRegisterAndDigitalDocumentCompleteApiIntegrationTest 
    extends  AbstractGetUserRetentionWeeklyCsvApiIntegrationTest {
    private static final String TARGET_API =
        "/api/admin/data-matrices/user-retention-weekly/pin-verification-for-three-weeks-in-a-row-given-register-and-digital-document-complete";

    private String expectedRow1;
    private String expectedRow2;

    @BeforeEach
    void executeScenarios() {
        expectedRow1 = scenarioForWeek1RegisteredUsers();
        expectedRow2 = scenarioForWeek2RegisteredUsers();
    }

    private String scenarioForWeek1RegisteredUsers() {
        List<String> expectedRow1Cells = new ArrayList<>();

        LocalDate week1 = appRenewalDate;
        expectedRow1Cells.add(week1 + TILDE + getLastDateOfWeek(week1));

        User user1 = itUtil.createUser();
        User user2 = itUtil.createUser();
        User user3 = itUtil.createUser();
        User user4 = itUtil.createUser();
        User user5 = itUtil.createUser();

        // 5 registered during week1
        registerDuringWeek(user1, week1);
        registerDuringWeek(user2, week1);
        registerDuringWeek(user3, week1);
        registerDuringWeek(user4, week1);
        registerDuringWeek(user5, week1);

        // 5 verified during week1
        verificationForThreeWeeksInARow(user1, week1);
        verificationForThreeWeeksInARow(user2, week1);
        verificationForThreeWeeksInARow(user3, week1);
        verificationForThreeWeeksInARow(user4, week1);
        verificationForThreeWeeksInARow(user5, week1);

        // 3 saved digital document
        digitalDocumentSaveDuringWeek(user1, week1);
        digitalDocumentSaveDuringWeek(user2, week1);
        digitalDocumentSaveDuringWeek(user3, week1);

        addToList(expectedRow1Cells, 3); // user1, user2, user3

        LocalDate week2 = week1.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week2);
        verificationForThreeWeeksInARow(user2, week2);
        verificationForThreeWeeksInARow(user4, week2);

        addToList(expectedRow1Cells, 3); // user1, user2, user3

        LocalDate week3 = week2.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week3);
        verificationForThreeWeeksInARow(user3, week3);

        addToList(expectedRow1Cells, 2); // user1, user3

        LocalDate week4 = week3.plusWeeks(1);
        verificationForThreeWeeksInARow(user4, week4);
        verificationForThreeWeeksInARow(user5, week4);

        addToList(expectedRow1Cells, 1); // user1

        LocalDate week5 = week4.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week5);
        verificationForThreeWeeksInARow(user4, week5);
        verificationForThreeWeeksInARow(user5, week5);

        addToList(expectedRow1Cells, 1); // user1

        LocalDate week6 = week5.plusWeeks(1);
        addToList(expectedRow1Cells, 1); // user1

        LocalDate week7 = week6.plusWeeks(1);
        addToList(expectedRow1Cells, 1); // user1

        LocalDate week8 = week7.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week8);
        verificationForThreeWeeksInARow(user2, week8);
        verificationForThreeWeeksInARow(user3, week8);
        verificationForThreeWeeksInARow(user4, week8);
        verificationForThreeWeeksInARow(user5, week8);

        addToList(expectedRow1Cells, 3); // user1, user2, user3

        addToList(expectedRow1Cells, 0); // no verification at week 9
        addToList(expectedRow1Cells, 0); // no verification at week 10

        return String.join(",", expectedRow1Cells);
    }

    private String scenarioForWeek2RegisteredUsers() {
        List<String> expectedRow2Cells = new ArrayList<>();

        LocalDate week2 = appRenewalDate.plusWeeks(1);
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

        // 7 registered during week2
        registerDuringWeek(user1, week2);
        registerDuringWeek(user2, week2);
        registerDuringWeek(user3, week2);
        registerDuringWeek(user4, week2);
        registerDuringWeek(user5, week2);
        registerDuringWeek(user6, week2);
        registerDuringWeek(user7, week2);

        // 7 verified during week2
        verificationForThreeWeeksInARow(user1, week2);
        verificationForThreeWeeksInARow(user2, week2);
        verificationForThreeWeeksInARow(user3, week2);
        verificationForThreeWeeksInARow(user4, week2);
        verificationForThreeWeeksInARow(user5, week2);
        verificationForThreeWeeksInARow(user6, week2);
        verificationForThreeWeeksInARow(user7, week2);

        // 3 saved digital document
        digitalDocumentSaveDuringWeek(user1, week2);
        digitalDocumentSaveDuringWeek(user2, week2);
        digitalDocumentSaveDuringWeek(user3, week2);

        addToList(expectedRow2Cells, 3); // user1, user2, user3

        LocalDate week3 = week2.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week3);
        verificationForThreeWeeksInARow(user3, week3);

        addToList(expectedRow2Cells, 2); // user1, user3

        LocalDate week4 = week3.plusWeeks(1);
        verificationForThreeWeeksInARow(user4, week4);
        verificationForThreeWeeksInARow(user5, week4);

        addToList(expectedRow2Cells, 1); // user1

        LocalDate week5 = week4.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week5);
        verificationForThreeWeeksInARow(user4, week5);
        verificationForThreeWeeksInARow(user5, week5);

        addToList(expectedRow2Cells, 1); // user1

        LocalDate week6 = week5.plusWeeks(1);
        addToList(expectedRow2Cells, 1); // user1

        LocalDate week7 = week6.plusWeeks(1);
        addToList(expectedRow2Cells, 1); // user1

        LocalDate week8 = week7.plusWeeks(1);
        verificationForThreeWeeksInARow(user1, week8);
        verificationForThreeWeeksInARow(user2, week8);
        verificationForThreeWeeksInARow(user3, week8);
        verificationForThreeWeeksInARow(user4, week8);
        verificationForThreeWeeksInARow(user5, week8);
        verificationForThreeWeeksInARow(user6, week8);
        verificationForThreeWeeksInARow(user7, week8);

        addToList(expectedRow2Cells, 3); // user1, user2, user3

        addToList(expectedRow2Cells, 0); // no verification at week 9
        addToList(expectedRow2Cells, 0); // no verification at week 10

        return String.join(",", expectedRow2Cells);
    }

    private void assertCsv(String actual) {
        String[] rows = actual.split("\n");
        assertThat(rows.length, is(1 + WEEKS_SINCE_RENEWAL)); // header row + weekly retention data rows

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
                get(TARGET_API)
                    .contentType(MediaType.ALL)
                    .accept(MediaType.ALL)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }
}
