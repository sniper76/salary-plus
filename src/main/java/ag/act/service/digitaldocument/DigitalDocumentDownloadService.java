package ag.act.service.digitaldocument;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.digitaldocument.DigitalDocumentZipFileRequestConverter;
import ag.act.core.infra.LambdaEnvironment;
import ag.act.dto.DigitalDocumentZipFileRequest;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.repository.DigitalDocumentDownloadRepository;
import ag.act.service.aws.LambdaService;
import ag.act.service.download.csv.CsvDownloadSourceProvider;
import ag.act.service.download.csv.DigitalDocumentCsvDownloadService;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockService;
import ag.act.util.FilenameUtil;
import ag.act.util.ObjectMapperUtil;
import ag.act.util.UUIDUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DigitalDocumentDownloadService {
    private final LambdaEnvironment lambdaEnvironment;
    private final DigitalDocumentDownloadRepository digitalDocumentDownloadRepository;
    private final LambdaService lambdaService;
    private final ObjectMapperUtil objectMapperUtil;
    private final DigitalDocumentZipFileRequestConverter digitalDocumentZipFileRequestConverter;
    private final DigitalDocumentService digitalDocumentService;
    private final StockService stockService;
    private final PostService postService;
    private final DigitalDocumentCsvDownloadService digitalDocumentCsvDownloadService;
    private final DigitalDocumentUserService digitalDocumentUserService;

    public DigitalDocumentDownload createDigitalDocumentDownload(Long digitalDocumentId) {

        updateIsLatestFalse(digitalDocumentId);

        DigitalDocumentDownload digitalDocumentDownload = new DigitalDocumentDownload();
        digitalDocumentDownload.setDigitalDocumentId(digitalDocumentId);
        digitalDocumentDownload.setRequestUserId(ActUserProvider.get().map(User::getId).orElse(ActUserProvider.getSystemUserId()));
        digitalDocumentDownload.setIsLatest(true);
        digitalDocumentDownload.setZipFileStatus(ZipFileStatus.REQUEST);

        return digitalDocumentDownloadRepository.save(digitalDocumentDownload);
    }

    private void updateIsLatestFalse(Long digitalDocumentId) {
        final List<DigitalDocumentDownload> digitalDocumentDownloads
            = digitalDocumentDownloadRepository.findAllByDigitalDocumentId(digitalDocumentId)
            .stream()
            .map(this::setIsLatestFalseAndReturn)
            .toList();

        if (digitalDocumentDownloads.isEmpty()) {
            return;
        }

        digitalDocumentDownloadRepository.saveAll(digitalDocumentDownloads);
    }

    private DigitalDocumentDownload setIsLatestFalseAndReturn(DigitalDocumentDownload digitalDocumentDownload) {
        digitalDocumentDownload.setIsLatest(false);
        return digitalDocumentDownload;
    }

    public void invokeZipFilesLambda(Long digitalDocumentId, Long digitalDocumentDownloadId, Boolean isSecured) {
        final DigitalDocument digitalDocument = getDigitalDocument(digitalDocumentId);
        final String password = isSecured ? digitalDocument.getStockCode() : null;

        final DigitalDocumentZipFileRequest request = digitalDocumentZipFileRequestConverter.convert(
            digitalDocumentId,
            digitalDocumentDownloadId,
            password,
            getZipFilename(digitalDocument)
        );

        lambdaService.invokeLambdaAsync(lambdaEnvironment.getZipFilesLambdaName(), objectMapperUtil.toRequestBody(request));
    }

    public String getCsvFilename(Long digitalDocumentId) {
        return getCsvFilename(getDigitalDocument(digitalDocumentId));
    }

    private String getCsvFilename(DigitalDocument digitalDocument) {
        return getDigitalDocumentFilename(digitalDocument, "csv");
    }

    private String getZipFilename(DigitalDocument digitalDocument) {
        return getDigitalDocumentFilename(digitalDocument, "zip");
    }

    private String getDigitalDocumentFilename(DigitalDocument digitalDocument, String extension) {
        final Stock stock = stockService.findByCode(digitalDocument.getStockCode())
            .orElseThrow(() -> new NotFoundException("전자문서의 종목을 찾을 수 없습니다."));

        final Post post = postService.findById(digitalDocument.getPostId())
            .orElseThrow(() -> new NotFoundException("전자문서의 게시글을 찾을 수 없습니다."));

        final String postTitle = post.getTitle()
            + (isDigitalDocumentFinished(digitalDocument.getId()) ? "" : "_temp");

        return FilenameUtil.getFilename(stock.getName(), postTitle, extension);
    }

    public DigitalDocumentDownload completeDigitalDocumentDownloadZipFile(Long digitalDocumentDownloadId, String zipFilePath) {
        final DigitalDocumentDownload digitalDocumentDownload = findNoneNull(digitalDocumentDownloadId);
        digitalDocumentDownload.setZipFileStatus(ZipFileStatus.COMPLETE);
        digitalDocumentDownload.setZipFilePath(zipFilePath);
        digitalDocumentDownload.setZipFileKey(UUIDUtil.randomUUID().toString());

        return digitalDocumentDownloadRepository.save(digitalDocumentDownload);
    }

    public DigitalDocumentDownload updateDigitalDocumentZipFileInProgress(Long digitalDocumentDownloadId) {
        final DigitalDocumentDownload digitalDocumentDownload = findNoneNull(digitalDocumentDownloadId);
        digitalDocumentDownload.setZipFileStatus(ZipFileStatus.IN_PROGRESS);

        return digitalDocumentDownloadRepository.save(digitalDocumentDownload);
    }

    private DigitalDocumentDownload findNoneNull(Long digitalDocumentDownloadId) {
        return digitalDocumentDownloadRepository.findById(digitalDocumentDownloadId)
            .orElseThrow(() -> new NotFoundException("해당 전자문서 다운로드 요청 정보를 찾을 수가 없습니다."));
    }

    private DigitalDocument getDigitalDocument(Long digitalDocumentId) {
        return digitalDocumentService.getDigitalDocument(digitalDocumentId);
    }

    public Optional<DigitalDocumentDownload> findByZipFileKey(String zipFileKey) {
        return digitalDocumentDownloadRepository.findFirstByZipFileKey(zipFileKey);
    }

    public DigitalDocumentDownload findByZipFileKeyNoneNull(String zipFileKey) {
        return findByZipFileKey(zipFileKey)
            .orElseThrow(() -> new BadRequestException("전자문서 다운로드 정보가 없습니다."));
    }

    public void downloadDigitalDocumentUserResponseInCsv(Long digitalDocumentId, HttpServletResponse response) {
        final DigitalDocument digitalDocument = getDigitalDocument(digitalDocumentId);
        digitalDocumentCsvDownloadService.download(response, new CsvDownloadSourceProvider<>(List.of(digitalDocument.getId())));
    }

    public boolean isDigitalDocumentFinished(Long digitalDocumentId) {
        return digitalDocumentService.isFinished(digitalDocumentId);
    }

    public void cleanupAllUnfinishedUserDigitalDocuments(Long digitalDocumentId) {
        digitalDocumentUserService.deleteAllUnfinishedUserDigitalDocuments(digitalDocumentId);
    }
}
