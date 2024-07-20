package ag.act.service.datamatrix.download.csv;

import ag.act.dto.datamatrix.UserRetentionWeeklyCsvGenerateRequestInput;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.download.datamatrix.UserRetentionWeeklyCsvDownloadProcessor;
import ag.act.service.download.datamatrix.csv.generator.UserRetentionWeeklyCsvByWeekGenerator;
import ag.act.service.download.datamatrix.csv.generator.UserRetentionWeeklyCsvGeneratorResolver;
import ag.act.service.download.datamatrix.data.provider.PinVerificationGivenRegisterByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.UserRetentionDataByWeekProviderResolver;
import ag.act.service.download.datamatrix.headerwriter.UserRetentionWeeklyCsvHeaderWriterResolver;
import ag.act.service.download.datamatrix.headerwriter.WeeklyHeaderWriter;
import ag.act.util.AppRenewalDateProvider;
import ag.act.util.CSVWriterFactory;
import ag.act.util.DateTimeUtil;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("LineLength")
public class UserRetentionWeeklyCsvDownloadProcessorTest {
    @InjectMocks
    private UserRetentionWeeklyCsvDownloadProcessor processor;
    @Mock
    private CSVWriterFactory csvWriterFactory;
    @Mock
    private AppRenewalDateProvider appRenewalDateProvider;
    @Mock
    private UserRetentionDataByWeekProviderResolver userRetentionDataByWeekProviderResolver;
    @Mock
    private UserRetentionWeeklyCsvHeaderWriterResolver userRetentionWeeklyCsvHeaderWriterResolver;
    @Mock
    private UserRetentionWeeklyCsvGeneratorResolver userRetentionWeeklyCsvGeneratorResolver;
    @Mock
    private UserRetentionWeeklyCsvByWeekGenerator userRetentionWeeklyCsvByWeekGenerator;

    @Nested
    class WhenDownload {
        private static final int EXPECTED_CSV_ROWS = 10;

        @Mock
        private HttpServletResponse response;
        @Mock
        private ServletOutputStream servletOutputStream;
        @Mock
        private CSVWriter csvWriter;
        @Mock
        private PinVerificationGivenRegisterByWeekProvider pinVerificationGivenRegisterByWeekGenerator;
        @Mock
        private WeeklyHeaderWriter weeklyHeaderWriter;

        @SuppressWarnings("unchecked")
        @Test
        void shouldWrite() throws Exception {
            // Given
            LocalDate appRenewalDate = DateTimeUtil.getTodayLocalDate().minusWeeks(EXPECTED_CSV_ROWS);
            given(appRenewalDateProvider.get()).willReturn(appRenewalDate);

            given(response.getOutputStream()).willReturn(servletOutputStream);
            given(csvWriterFactory.create(servletOutputStream)).willReturn(csvWriter);

            given(userRetentionWeeklyCsvGeneratorResolver.resolve(any(UserRetentionWeeklyCsvDataType.class)))
                .willReturn(userRetentionWeeklyCsvByWeekGenerator);

            given(
                userRetentionDataByWeekProviderResolver.resolve(
                    UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER
                )
            ).willReturn(pinVerificationGivenRegisterByWeekGenerator);
            given(
                userRetentionWeeklyCsvHeaderWriterResolver.resolve(
                    UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER
                )
            ).willReturn(weeklyHeaderWriter);

            willDoNothing().given(weeklyHeaderWriter).setHeaders(eq(csvWriter), eq(appRenewalDate), any(LocalDate.class));

            // When
            processor.download(response, UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER);

            then(userRetentionWeeklyCsvByWeekGenerator)
                .should()
                .generate(any(UserRetentionWeeklyCsvGenerateRequestInput.class));
        }
    }

}
