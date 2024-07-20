package ag.act.handler.datamatrix;

import ag.act.api.UserRetentionWeeklyDataMatrixApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.download.datamatrix.UserRetentionWeeklyCsvDownloadService;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@SuppressWarnings("LineLength")
@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class UserRetentionWeeklyDataMatrixApiDelegateImpl implements UserRetentionWeeklyDataMatrixApiDelegate {
    private final UserRetentionWeeklyCsvDownloadService userRetentionWeeklyCsvDownloadService;

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER);
    }

    @Override
    public ResponseEntity<Resource> downloadDigitalDocumentParticipationRateGivenRegisterCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.DIGITAL_DOCUMENT_PARTICIPATION_RATE_GIVEN_REGISTER);
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenFirstDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_FIRST_DIGITAL_DOCUMENT_COMPLETE);
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterAndDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE);
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterAndNoDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE);
    }

    @Override
    public ResponseEntity<Resource> downloadNonRetainedUserGivenRegisterAndDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.NON_RETAINED_USER_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE);
    }

    @Override
    public ResponseEntity<Resource> downloadNonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.NON_RETAINED_USER_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE);
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationForThreeWeeksForThreeWeeksInARowGivenRegisterAndDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_FOR_THREE_WEEKS_IN_A_ROW_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE);
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationForThreeWeeksForThreeWeeksInARowGivenRegisterAndNoDigitalDocumentCompleteCsv() {
        return createAndDownloadCsv(UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_FOR_THREE_WEEKS_IN_A_ROW_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE);
    }

    private ResponseEntity<Resource> createAndDownloadCsv(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        return DownloadFileUtil.ok(
            userRetentionWeeklyCsvDownloadService.createAndDownloadCsv(userRetentionWeeklyCsvDataType)
        );
    }

    @Override
    public ResponseEntity<Resource> downloadUserRetentionWeeklyCsv(String formattedCsvDataType) {
        return DownloadFileUtil.ok(
            userRetentionWeeklyCsvDownloadService.downloadUserRetentionWeeklyCsv(UserRetentionWeeklyCsvDataType.fromPath(formattedCsvDataType))
        );
    }
}
