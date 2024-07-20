package ag.act.handler.datamatrix;

import ag.act.api.UserRetentionWeeklyMatrixByDigitalDocumentApiDelegate;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.facade.admin.UserRetentionByDigitalDocumentDownloadService;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class UserRetentionMatrixByDigitalDocumentApiDelegateImpl implements UserRetentionWeeklyMatrixByDigitalDocumentApiDelegate {

    private final UserRetentionByDigitalDocumentDownloadService userRetentionByDigitalDocumentDownloadService;

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignCompleteRetentionDownload(Long campaignId) {
        return DownloadFileUtil.ok(
            userRetentionByDigitalDocumentDownloadService.downloadByCampaignId(
                UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_COMPLETE, campaignId)
        );
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterDigitalDocumentCompleteRetentionDownload(Long digitalDocumentId) {
        return DownloadFileUtil.ok(
            userRetentionByDigitalDocumentDownloadService.downloadByDigitalDocumentId(
                UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_COMPLETE, digitalDocumentId)
        );
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignNotCompleteRetentionDownload(Long campaignId) {
        return DownloadFileUtil.ok(
            userRetentionByDigitalDocumentDownloadService.downloadByCampaignId(
                UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_NOT_COMPLETE, campaignId
            )
        );
    }

    @Override
    public ResponseEntity<Resource> downloadPinVerificationGivenRegisterAndDigitalDocumentNotCompleteRetentionDownload(Long digitalDocumentId) {
        return DownloadFileUtil.ok(
            userRetentionByDigitalDocumentDownloadService.downloadByDigitalDocumentId(
                UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_NOT_COMPLETE, digitalDocumentId
            )
        );
    }
}
