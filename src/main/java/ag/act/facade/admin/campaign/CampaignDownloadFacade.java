package ag.act.facade.admin.campaign;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.ZipFileDownload;
import ag.act.entity.campaign.CampaignDownload;
import ag.act.enums.FileType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.facade.digitaldocument.DigitalDocumentDownloadFacade;
import ag.act.facade.download.Downloadable;
import ag.act.model.DigitalDocumentZipFileCallbackRequest;
import ag.act.service.admin.CampaignDownloadService;
import ag.act.service.download.zip.ZipFileDownloadService;
import ag.act.util.DownloadFileUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class CampaignDownloadFacade implements Downloadable {

    private final DigitalDocumentDownloadFacade digitalDocumentDownloadFacade;
    private final CampaignDownloadService campaignDownloadService;
    private final ZipFileDownloadService zipFileDownloadService;

    @Override
    public boolean supports(FileType fileType) {
        return FileType.CAMPAIGN_DIGITAL_DOCUMENT == fileType;
    }

    public void createDigitalDocumentZipFile(Long campaignId, List<Long> digitalDocumentIds, boolean isSecured) {
        digitalDocumentIds.forEach(digitalDocumentDownloadFacade::cleanUpIfDigitalDocumentFinished);

        final CampaignDownload campaignDownload = campaignDownloadService.create(campaignId);

        campaignDownloadService.invokeZipFilesLambda(campaignId, digitalDocumentIds, campaignDownload.getId(), isSecured);
        campaignDownloadService.updateDigitalDocumentZipFileInProgress(campaignDownload.getId());
    }

    @Override
    public void updateDownloadZipFile(DigitalDocumentZipFileCallbackRequest digitalDocumentZipFileCallbackRequest) {
        final Long digitalDocumentDownloadId = digitalDocumentZipFileCallbackRequest.getDigitalDocumentDownloadId();
        final String zipFilePath = digitalDocumentZipFileCallbackRequest.getZipFilePath();
        campaignDownloadService.completeDigitalDocumentDownloadZipFile(digitalDocumentDownloadId, zipFilePath);
    }

    @Override
    public DownloadFile downloadZipFile(String zipFileKey) {
        return zipFileDownloadService.downloadZipFile(getCampaignDownload(zipFileKey));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public DownloadFile downloadUserResponseInCsv(Long campaignId) {

        final HttpServletResponse response = RequestContextHolder.getResponse();
        final String csvFilename = campaignDownloadService.getCsvFilename(campaignId);
        try {
            DownloadFileUtil.setFilename(response, csvFilename);
            campaignDownloadService.downloadCampaignUserResponseInCsv(campaignId, response);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("CSV 캠페인 다운로드 중 알 수 없는 오류가 발생했습니다.", e);
        }

        return DownloadFile.builder().fileName(csvFilename).build();
    }

    private CampaignDownload getCampaignDownload(String zipFileKey) {
        return campaignDownloadService.getCampaignDownloadByZipFileKey(zipFileKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Optional<ZipFileDownload> findZipFileDownloadByZipFileKey(String zipFileKey) {
        return (Optional) campaignDownloadService.findByZipFileKey(zipFileKey);
    }
}
