package ag.act.facade.digitaldocument;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.ZipFileDownload;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.FileType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.facade.download.Downloadable;
import ag.act.model.DigitalDocumentZipFileCallbackRequest;
import ag.act.service.digitaldocument.DigitalDocumentDownloadService;
import ag.act.service.download.zip.ZipFileDownloadService;
import ag.act.util.DownloadFileUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DigitalDocumentDownloadFacade implements Downloadable {

    private final DigitalDocumentDownloadService digitalDocumentDownloadService;
    private final ZipFileDownloadService zipFileDownloadService;

    @Override
    public boolean supports(FileType fileType) {
        return FileType.DIGITAL_DOCUMENT == fileType;
    }

    public void createDigitalDocumentZipFile(Long digitalDocumentId, Boolean isSecured) {
        cleanUpIfDigitalDocumentFinished(digitalDocumentId);

        final DigitalDocumentDownload digitalDocumentDownload = digitalDocumentDownloadService.createDigitalDocumentDownload(digitalDocumentId);
        digitalDocumentDownloadService.invokeZipFilesLambda(digitalDocumentId, digitalDocumentDownload.getId(), isSecured);
        digitalDocumentDownloadService.updateDigitalDocumentZipFileInProgress(digitalDocumentDownload.getId());
    }

    public void cleanUpIfDigitalDocumentFinished(Long digitalDocumentId) {
        if (!digitalDocumentDownloadService.isDigitalDocumentFinished(digitalDocumentId)) {
            return;
        }

        cleanupAllUnfinishedUserDigitalDocuments(digitalDocumentId);
    }

    private void cleanupAllUnfinishedUserDigitalDocuments(Long digitalDocumentId) {
        digitalDocumentDownloadService.cleanupAllUnfinishedUserDigitalDocuments(digitalDocumentId);
    }

    @Override
    public void updateDownloadZipFile(DigitalDocumentZipFileCallbackRequest digitalDocumentZipFileCallbackRequest) {
        final Long digitalDocumentDownloadId = digitalDocumentZipFileCallbackRequest.getDigitalDocumentDownloadId();
        final String zipFilePath = digitalDocumentZipFileCallbackRequest.getZipFilePath();
        digitalDocumentDownloadService.completeDigitalDocumentDownloadZipFile(digitalDocumentDownloadId, zipFilePath);
    }

    @Override
    public DownloadFile downloadZipFile(String zipFileKey) {
        return zipFileDownloadService.downloadZipFile(getDigitalDocumentDownload(zipFileKey));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public DownloadFile downloadUserResponseInCsv(Long digitalDocumentId) {
        final HttpServletResponse response = RequestContextHolder.getResponse();
        final String csvFilename = digitalDocumentDownloadService.getCsvFilename(digitalDocumentId);
        try {
            DownloadFileUtil.setFilename(response, csvFilename);
            digitalDocumentDownloadService.downloadDigitalDocumentUserResponseInCsv(digitalDocumentId, response);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("CSV 전자문서 다운로드 중 알 수 없는 오류가 발생했습니다.", e);
        }

        return DownloadFile.builder().fileName(csvFilename).build();
    }

    private DigitalDocumentDownload getDigitalDocumentDownload(String zipFileKey) {
        return digitalDocumentDownloadService.findByZipFileKeyNoneNull(zipFileKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Optional<ZipFileDownload> findZipFileDownloadByZipFileKey(String zipFileKey) {
        return (Optional) digitalDocumentDownloadService.findByZipFileKey(zipFileKey);
    }
}
