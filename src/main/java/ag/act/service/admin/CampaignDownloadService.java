package ag.act.service.admin;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.digitaldocument.DigitalDocumentZipFileRequestConverter;
import ag.act.core.infra.LambdaEnvironment;
import ag.act.dto.DigitalDocumentZipFileRequest;
import ag.act.entity.Campaign;
import ag.act.entity.campaign.CampaignDownload;
import ag.act.enums.FileType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.repository.campaign.CampaignDownloadRepository;
import ag.act.service.aws.LambdaService;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.campaign.CampaignService;
import ag.act.service.download.csv.CsvDownloadServiceResolver;
import ag.act.service.download.csv.CsvDownloadSourceProvider;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.FilenameUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ObjectMapperUtil;
import ag.act.util.UUIDUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CampaignDownloadService {

    private final ObjectMapperUtil objectMapperUtil;
    private final LambdaEnvironment lambdaEnvironment;
    private final LambdaService lambdaService;
    private final CampaignService campaignService;
    private final CampaignDownloadRepository campaignDownloadRepository;
    private final DigitalDocumentService digitalDocumentService;
    private final CsvDownloadServiceResolver csvDownloadServiceResolver;
    private final DigitalDocumentZipFileRequestConverter digitalDocumentZipFileRequestConverter;

    public CampaignDownload create(Long campaignId) {

        updateIsLatestFalse(campaignId);

        CampaignDownload digitalDocumentDownload = new CampaignDownload();
        digitalDocumentDownload.setCampaignId(campaignId);
        digitalDocumentDownload.setRequestUserId(ActUserProvider.getNoneNull().getId());
        digitalDocumentDownload.setIsLatest(true);
        digitalDocumentDownload.setZipFileStatus(ZipFileStatus.REQUEST);

        return campaignDownloadRepository.save(digitalDocumentDownload);
    }

    private void updateIsLatestFalse(Long campaignId) {
        final List<CampaignDownload> campaignDownloads = campaignDownloadRepository.findAllByCampaignId(campaignId);

        if (campaignDownloads.isEmpty()) {
            return;
        }

        campaignDownloads.forEach(this::setIsLatestFalseAndReturn);

        campaignDownloadRepository.saveAll(campaignDownloads);
    }

    private void setIsLatestFalseAndReturn(CampaignDownload campaignDownload) {
        campaignDownload.setIsLatest(false);
    }

    public void invokeZipFilesLambda(
        Long campaignId,
        List<Long> digitalDocumentIds,
        Long downloadId,
        Boolean isSecured
    ) {
        final Campaign campaign = getCampaign(campaignId);
        final String password = isSecured ? getPassword(campaign) : null;
        final boolean isFinished = isFinished(digitalDocumentIds.get(0));

        final DigitalDocumentZipFileRequest request = digitalDocumentZipFileRequestConverter.convert(
            campaignId,
            digitalDocumentIds,
            downloadId,
            password,
            getZipFilename(campaign, isFinished),
            FileType.CAMPAIGN_DIGITAL_DOCUMENT
        );

        lambdaService.invokeLambdaAsync(lambdaEnvironment.getZipFilesLambdaName(), objectMapperUtil.toRequestBody(request));
    }

    private boolean isFinished(Long digitalDocumentId) {
        return digitalDocumentService.isFinished(digitalDocumentId);
    }

    private String getPassword(Campaign campaign) {
        return KoreanDateTimeUtil.toKoreanTime(campaign.getCreatedAt()).format(DateTimeFormatUtil.yyMMdd());
    }

    private String getZipFilename(Campaign campaign, boolean isFinished) {
        return FilenameUtil.getFilenameWithDate(campaign.getTitle() + (isFinished ? "" : "_temp"), "zip");
    }

    private String getCsvFilename(Campaign campaign) {
        return FilenameUtil.getFilenameWithDate(campaign.getTitle(), "csv");
    }

    public String getCsvFilename(Long campaignId) {
        return getCsvFilename(getCampaign(campaignId));
    }

    private Campaign getCampaign(Long campaignId) {
        return campaignService.getCampaign(campaignId);
    }

    public CampaignDownload updateDigitalDocumentZipFileInProgress(Long downloadId) {
        final CampaignDownload campaignDownload = getCampaignDownload(downloadId);
        campaignDownload.setZipFileStatus(ZipFileStatus.IN_PROGRESS);

        return campaignDownloadRepository.save(campaignDownload);
    }

    private CampaignDownload getCampaignDownload(Long downloadId) {
        return campaignDownloadRepository.findById(downloadId)
            .orElseThrow(() -> new NotFoundException("해당 캠페인 다운로드 요청 정보를 찾을 수가 없습니다."));
    }

    public CampaignDownload completeDigitalDocumentDownloadZipFile(Long digitalDocumentDownloadId, String zipFilePath) {
        final CampaignDownload campaignDownload = getCampaignDownload(digitalDocumentDownloadId);
        campaignDownload.setZipFileStatus(ZipFileStatus.COMPLETE);
        campaignDownload.setZipFilePath(zipFilePath);
        campaignDownload.setZipFileKey(UUIDUtil.randomUUID().toString());

        return campaignDownloadRepository.save(campaignDownload);
    }

    @SuppressWarnings("unchecked")
    public void downloadCampaignUserResponseInCsv(Long campaignId, HttpServletResponse response) {
        final var campaign = getCampaign(campaignId);
        final var category = campaignService.getSourcePostOf(campaign).getBoard().getCategory();
        final var sourceProvider = new CsvDownloadSourceProvider<>(category, campaign);

        csvDownloadServiceResolver.resolve(sourceProvider)
            .download(response, sourceProvider);
    }

    public CampaignDownload getCampaignDownloadByZipFileKey(String zipFileKey) {
        return findByZipFileKey(zipFileKey)
            .orElseThrow(() -> new BadRequestException("캠페인 다운로드 정보가 없습니다."));
    }

    public Optional<CampaignDownload> findByZipFileKey(String zipFileKey) {
        return campaignDownloadRepository.findFirstByZipFileKey(zipFileKey);
    }
}
