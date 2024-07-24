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

class GetWeeklyNonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteApiIntegrationTest
    extends AbstractGetUserRetentionWeeklyCsvApiIntegrationTest {
    private static final String TARGET_API =
        "/api/admin/data-matrices/user-retention-weekly/non-retained-user-given-register-and-no-digital-document-complete";

    private String expectedRow1;
    private String expectedRow2;

    @BeforeEach
    void executeScenarios() {
        expectedRow1 = scenarioForWeek1();
        expectedRow2 = scenarioForWeek2();
    }

    private String scenarioForWeek1() {
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

        // 3 saved digital document
        digitalDocumentSaveDuringWeek(user1, week1);
        digitalDocumentSaveDuringWeek(user2, week1.plusWeeks(1));
        digitalDocumentSaveDuringWeek(user3, week1.plusWeeks(2));

        addToList(expectedRow1Cells, 2); // user4 and user5 did not save digital document and registered

        LocalDate week2 = week1.plusWeeks(1);
        verificationDuringWeek(user1, week2);
        verificationDuringWeek(user2, week2);
        verificationDuringWeek(user4, week2);

        addToList(expectedRow1Cells, 1); // user5 did not save digital document and not retained

        LocalDate week3 = week2.plusWeeks(1);
        verificationDuringWeek(user1, week3);
        verificationDuringWeek(user3, week3);

        addToList(expectedRow1Cells, 2); // user4 and user5 did not save digital document and not retained

        LocalDate week4 = week3.plusWeeks(1);
        verificationDuringWeek(user4, week4);
        verificationDuringWeek(user5, week4);

        addToList(expectedRow1Cells, 0); // no user did not save digital document and not retained

        LocalDate week5 = week4.plusWeeks(1);
        verificationDuringWeek(user1, week5);
        verificationDuringWeek(user4, week5);
        verificationDuringWeek(user5, week5);

        addToList(expectedRow1Cells, 0); // no user did not save digital document and not retained

        LocalDate week6 = week5.plusWeeks(1);
        addToList(expectedRow1Cells, 2); // user4 and user5 did not save digital document and not retained

        LocalDate week7 = week6.plusWeeks(1);
        addToList(expectedRow1Cells, 2); // user4 and user5 did not save digital document and not retained

        LocalDate week8 = week7.plusWeeks(1);
        verificationDuringWeek(user1, week8);
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user3, week8);
        verificationDuringWeek(user4, week8);
        verificationDuringWeek(user5, week8);

        addToList(expectedRow1Cells, 0); // no user did not save digital document and not retained

        addToList(expectedRow1Cells, 2); // user4 and user5 did not save digital document and not retained
        addToList(expectedRow1Cells, 2); // user4 and user5 did not save digital document and not retained

        return String.join(",", expectedRow1Cells);
    }

    private String scenarioForWeek2() {
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

        // 3 saved digital document
        digitalDocumentSaveDuringWeek(user1, week2);
        digitalDocumentSaveDuringWeek(user2, week2.plusWeeks(1));
        digitalDocumentSaveDuringWeek(user3, week2.plusWeeks(2));

        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 did not save digital document and registered

        LocalDate week3 = week2.plusWeeks(1);
        verificationDuringWeek(user1, week3);
        verificationDuringWeek(user3, week3);
        verificationDuringWeek(user4, week3);
        verificationDuringWeek(user5, week3);
        verificationDuringWeek(user6, week3);
        verificationDuringWeek(user7, week3);

        addToList(expectedRow2Cells, 0); // no user did not save digital document and not retained

        LocalDate week4 = week3.plusWeeks(1);
        verificationDuringWeek(user4, week4);
        verificationDuringWeek(user5, week4);

        addToList(expectedRow2Cells, 2); // user6, user7 did not save digital document and not retained

        LocalDate week5 = week4.plusWeeks(1);
        verificationDuringWeek(user1, week5);
        verificationDuringWeek(user4, week5);
        verificationDuringWeek(user5, week5);

        addToList(expectedRow2Cells, 2); // user6, user7 did not save digital document and not retained

        LocalDate week6 = week5.plusWeeks(1);
        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 did not save digital document and not retained

        LocalDate week7 = week6.plusWeeks(1);
        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 did not save digital document and not retained

        LocalDate week8 = week7.plusWeeks(1);
        verificationDuringWeek(user1, week8);
        verificationDuringWeek(user2, week8);
        verificationDuringWeek(user3, week8);
        verificationDuringWeek(user4, week8);
        verificationDuringWeek(user5, week8);
        verificationDuringWeek(user6, week8);
        verificationDuringWeek(user7, week8);

        addToList(expectedRow2Cells, 0); // no user did not save digital document and not retained

        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 did not save digital document and not retained
        addToList(expectedRow2Cells, 4); // user4, user5, user6, user7 did not save digital document and not retained

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
