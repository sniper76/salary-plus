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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetWeeklyPinVerificationGivenRegisterApiIntegrationTest extends AbstractGetUserRetentionWeeklyCsvApiIntegrationTest {
    private static final String TARGET_API =
        "/api/admin/data-matrices/user-retention-weekly/pin-verification-given-register";

    private String expectedRow1;
    private String expectedRow2;
    private String expectedRow3;

    @BeforeEach
    void executeScenarios() {
        expectedRow1 = scenarioForWeek1RegisteredUsers();
        expectedRow2 = scenarioForWeek2RegisteredUsers();
        expectedRow3 = scenarioForWeek3RegisteredUsers();
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
        addToList(expectedRow1Cells, 5);

        LocalDate week2 = week1.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user1, week2);
        verificationDuringWeek(user1, week2);
        verificationDuringWeek(user2, week2);
        verificationDuringWeek(user2, week2);
        verificationDuringWeek(user3, week2);
        addToList(expectedRow1Cells, 3);

        LocalDate week3 = week2.plusWeeks(1);
        // two unique verifications
        verificationDuringWeek(user2, week3);
        verificationDuringWeek(user4, week3);
        addToList(expectedRow1Cells, 2);

        LocalDate week4 = week3.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user3, week4);
        verificationDuringWeek(user4, week4);
        verificationDuringWeek(user5, week4);
        addToList(expectedRow1Cells, 3);

        LocalDate week5 = week4.plusWeeks(1);
        // no unique verifications
        addToList(expectedRow1Cells, 0);

        LocalDate week6 = week5.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user3, week6);
        verificationDuringWeek(user4, week6);
        verificationDuringWeek(user5, week6);
        addToList(expectedRow1Cells, 3);

        LocalDate week7 = week6.plusWeeks(1);
        // two unique verifications
        verificationDuringWeek(user2, week7);
        verificationDuringWeek(user4, week7);
        addToList(expectedRow1Cells, 2);

        LocalDate week8 = week7.plusWeeks(1);
        // five unique verifications
        verificationDuringWeek(user1, week8);
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user3, week8);
        verificationDuringWeek(user4, week8);
        verificationDuringWeek(user5, week8);
        addToList(expectedRow1Cells, 5);

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
        addToList(expectedRow2Cells, 7);

        LocalDate week3 = week2.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user2, week3);
        verificationDuringWeek(user4, week3);
        verificationDuringWeek(user5, week3);
        addToList(expectedRow2Cells, 3);

        LocalDate week4 = week3.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user3, week4);
        verificationDuringWeek(user4, week4);
        verificationDuringWeek(user5, week4);
        addToList(expectedRow2Cells, 3);

        LocalDate week5 = week4.plusWeeks(1);
        // no unique verifications
        addToList(expectedRow2Cells, 0);

        LocalDate week6 = week5.plusWeeks(1);
        // four unique verifications
        verificationDuringWeek(user3, week6);
        verificationDuringWeek(user4, week6);
        verificationDuringWeek(user5, week6);
        verificationDuringWeek(user6, week6);
        expectedRow2Cells.add(String.valueOf(4));

        LocalDate week7 = week6.plusWeeks(1);
        // five unique verifications
        verificationDuringWeek(user2, week7);
        verificationDuringWeek(user4, week7);
        verificationDuringWeek(user5, week7);
        verificationDuringWeek(user6, week7);
        verificationDuringWeek(user7, week7);
        addToList(expectedRow2Cells, 5);

        LocalDate week8 = week7.plusWeeks(1);
        // five unique verifications
        verificationDuringWeek(user1, week8);
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user3, week8);
        verificationDuringWeek(user4, week8);
        verificationDuringWeek(user5, week8);
        addToList(expectedRow2Cells, 5);

        addToList(expectedRow2Cells, 0); // no verification at week 9
        addToList(expectedRow2Cells, 0); // no verification at week 10

        return String.join(",", expectedRow2Cells);
    }

    private String scenarioForWeek3RegisteredUsers() {
        List<String> expectedRow3Cells = new ArrayList<>();

        LocalDate week3 = appRenewalDate.plusWeeks(2);
        expectedRow3Cells.add(week3 + TILDE + getLastDateOfWeek(week3));

        // should be empty for week1
        expectedRow3Cells.add(PLACEHOLDER);
        expectedRow3Cells.add(PLACEHOLDER);

        User user1 = itUtil.createUser();
        User user2 = itUtil.createUser();
        User user3 = itUtil.createUser();
        User user4 = itUtil.createUser();
        User user5 = itUtil.createUser();
        User user6 = itUtil.createUser();
        User user7 = itUtil.createUser();

        // 7 registered during week3
        registerDuringWeek(user1, week3);
        registerDuringWeek(user2, week3);
        registerDuringWeek(user3, week3);
        registerDuringWeek(user4, week3);
        registerDuringWeek(user5, week3);
        registerDuringWeek(user6, week3);
        registerDuringWeek(user7, week3);
        addToList(expectedRow3Cells, 7);

        LocalDate week4 = week3.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user2, week4);
        verificationDuringWeek(user4, week4);
        verificationDuringWeek(user5, week4);
        addToList(expectedRow3Cells, 3);

        LocalDate week5 = week4.plusWeeks(1);
        // three unique verifications
        verificationDuringWeek(user3, week5);
        verificationDuringWeek(user4, week5);
        verificationDuringWeek(user5, week5);
        addToList(expectedRow3Cells, 3);

        LocalDate week6 = week5.plusWeeks(1);
        // no unique verifications
        addToList(expectedRow3Cells, 0);

        LocalDate week7 = week6.plusWeeks(1);
        // four unique verifications
        verificationDuringWeek(user3, week7);
        verificationDuringWeek(user4, week7);
        verificationDuringWeek(user5, week7);
        verificationDuringWeek(user6, week7);
        expectedRow3Cells.add(String.valueOf(4));

        LocalDate week8 = week7.plusWeeks(1);
        // five unique verifications
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user4, week8);
        verificationDuringWeek(user5, week8);
        verificationDuringWeek(user6, week8);
        verificationDuringWeek(user7, week8);
        addToList(expectedRow3Cells, 5);

        LocalDate week9 = week8.plusWeeks(1);
        // five unique verifications
        verificationDuringWeek(user1, week9);
        verificationDuringWeek(user2, week9);
        verificationDuringWeek(user3, week9);
        verificationDuringWeek(user4, week9);
        verificationDuringWeek(user5, week9);
        addToList(expectedRow3Cells, 5);

        addToList(expectedRow3Cells, 0); // no verification at week 9

        return String.join(",", expectedRow3Cells);
    }

    private void assertCsv(String actual) {
        String[] rows = actual.split("\n");
        assertThat(rows.length, is(1 + WEEKS_SINCE_RENEWAL)); // header row + weekly retention data rows

        assertThat(rows[1], is(expectedRow1));
        assertThat(rows[2], is(expectedRow2));
        assertThat(rows[3], is(expectedRow3));

        IntStream.range(4, rows.length).forEach(i -> {
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
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }
}
