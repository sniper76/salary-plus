package ag.act.api.admin.datamatrix;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.util.DateTimeUtil;
import ag.act.util.FilenameUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import static ag.act.TestUtil.getPathFrom;
import static ag.act.enums.FileType.MATRIX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class DownloadAllUsersGivenAllConditionsRetentionCsvApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API =
        "/api/admin/data-matrices/user-retention-weekly/{formattedCsvDataType}";

    private static final String CSV_EXTENSION = "csv";
    private String jwt;
    private String latestCsvFileContent;

    @BeforeEach
    void setUp() {
        itUtil.init();
        jwt = itUtil.createJwt(itUtil.createAdminUser().getId());

        final String fileName = UserRetentionWeeklyCsvDataType.ALL_USERS_GIVEN_ALL_CONDITIONS.getFileName();
        final LocalDate date = KoreanDateTimeUtil.getYesterdayLocalDate();
        final String latestCsvFilePath = String.format(
            "%s/%s/%s",
            MATRIX.getPathPrefix(),
            DateTimeUtil.formatLocalDate(date, "yyyy/MM/dd"),
            FilenameUtil.getFilenameWithDate(fileName, CSV_EXTENSION, date)
        );

        latestCsvFileContent = someAlphanumericString(50);
        given(itUtil.getS3ServiceMockBean().readObject(latestCsvFilePath)).willReturn(new ByteArrayInputStream(latestCsvFileContent.getBytes()));
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldDownloadAllData() throws Exception {
        final String actual = callApiAndGetResult(status().isOk()).getResponse().getContentAsString();

        assertThat(actual, is(latestCsvFileContent));
    }

    private MvcResult callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, getPathFrom(UserRetentionWeeklyCsvDataType.ALL_USERS_GIVEN_ALL_CONDITIONS.name()))
                    .contentType(MediaType.ALL)
                    .accept(MediaType.ALL)
                    .header(AUTHORIZATION, "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}