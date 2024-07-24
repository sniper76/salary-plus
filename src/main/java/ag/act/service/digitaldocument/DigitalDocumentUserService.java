package ag.act.service.digitaldocument;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.digitaldocument.DigitalDocumentUserDetailsResponseConverter;
import ag.act.converter.post.PostResponseDigitalDocumentConverter;
import ag.act.core.guard.HoldingStockGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.dto.digitaldocument.DigitalDocumentUserSearchDto;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.facade.stock.StockFacade;
import ag.act.model.DigitalDocumentUserDetailsResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.module.digitaldocumentgenerator.dto.DigitalDocumentFilenameDto;
import ag.act.module.mydata.MyDataSummaryService;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.DigitalDocumentUserRepository;
import ag.act.repository.interfaces.DigitalDocumentUserSummary;
import ag.act.service.aws.S3Service;
import ag.act.service.digitaldocument.answer.DigitalDocumentItemUserAnswerService;
import ag.act.service.digitaldocument.certification.DigitalDocumentCertificationService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.DateTimeUtil;
import ag.act.util.DecimalFormatUtil;
import ag.act.util.FilenameUtil;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.validator.document.DigitalDocumentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DigitalDocumentUserService {
    private static final int ONE_HOUR = 1;
    private final DigitalDocumentValidator digitalDocumentValidator;
    private final StockFacade stockFacade;
    private final DigitalDocumentUserProcessService digitalDocumentUserProcessService;
    private final DigitalDocumentItemUserAnswerService digitalDocumentItemUserAnswerService;
    private final PostResponseDigitalDocumentConverter postResponseDigitalDocumentConverter;
    private final DigitalDocumentUserDetailsResponseConverter digitalDocumentUserDetailsResponseConverter;
    private final MyDataSummaryService myDataSummaryService;
    private final DigitalDocumentUserRepository digitalDocumentUserRepository;
    private final DigitalDocumentRepository digitalDocumentRepository;
    private final S3Service s3Service;
    private final DigitalDocumentAcceptUserService digitalDocumentAcceptUserService;
    private final UserVerificationHistoryService userVerificationHistoryService;
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    private final UserHoldingStockService userHoldingStockService;
    private final DigitalDocumentCertificationService digitalDocumentCertificationService;

    @UseGuards({HoldingStockGuard.class})
    public UserDigitalDocumentResponse createUserDigitalDocumentWithImage(
        MultipartFile signImage, MultipartFile idCardImage, List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf, String answerData, Long digitalDocumentId
    ) {
        DigitalDocument digitalDocument = updateDigitalDocumentStockCountAndUserCount(
            findDigitalDocumentByDigitalDocumentId(digitalDocumentId)
        );
        digitalDocumentValidator.validateTargetEndDate(digitalDocument.getTargetEndDate(), "등록");
        digitalDocumentValidator.validateDigitalDocumentUserFiles(
            digitalDocument, signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf, answerData
        );

        User user = ActUserProvider.getNoneNull();
        digitalDocument.getType().validate(user);

        digitalDocumentItemUserAnswerService.saveAnswers(answerData, user.getId(), digitalDocument);

        DigitalDocumentUser digitalDocumentUser = digitalDocumentUserProcessService.createDigitalDocumentUserWithGeneratedPdf(
            digitalDocument,
            user,
            signImage,
            idCardImage,
            bankAccountImages,
            hectoEncryptedBankAccountPdf
        );

        //user_verification_histories
        userVerificationHistoryService.create(
            user.getId(), VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE_SAVE, digitalDocumentUser.getId()
        );

        return postResponseDigitalDocumentConverter.convert(
            digitalDocument,
            digitalDocumentUser,
            getStock(digitalDocument),
            user,
            digitalDocumentAcceptUserService.getAcceptUser(digitalDocument)
        );
    }

    public Optional<DigitalDocumentUser> findByDigitalDocumentIdAndUserId(Long digitalDocumentId, Long userId) {
        return digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId);
    }

    private Stock getStock(DigitalDocument digitalDocument) {
        return stockFacade.findByCode(digitalDocument.getStockCode());
    }

    private double getShareholdingRatio(DigitalDocument digitalDocument, Stock stock) {
        return DecimalFormatUtil.twoDecimalPlaceDouble(digitalDocument.getJoinStockSum() * 100.0 / stock.getTotalIssuedQuantity());
    }

    private DigitalDocument updateDigitalDocumentStockCountAndUserCount(DigitalDocument digitalDocument) {
        DigitalDocumentUserSummary summary = findDigitalDocumentUserSummary(digitalDocument.getId());
        digitalDocument.setJoinUserCount(summary.getCountOfUser());
        digitalDocument.setJoinStockSum(summary.getSumOfStockCount());
        digitalDocument.setShareholdingRatio(getShareholdingRatio(digitalDocument, getStock(digitalDocument)));
        return digitalDocumentRepository.save(digitalDocument);
    }

    private DigitalDocumentUserSummary findDigitalDocumentUserSummary(Long id) {
        return digitalDocumentUserRepository.findDigitalDocumentUserSummary(id);
    }

    public DigitalDocumentUser getDigitalDocumentUserNoneNull(Long digitalDocumentId, Long userId) {
        return digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId)
            .orElseThrow(
                () -> new InternalServerException(
                    String.format("전자 문서 유저를 찾을 수 없습니다. digitalDocumentId: %s, userId: %s", digitalDocumentId, userId))
            );
    }

    @UseGuards({HoldingStockGuard.class})
    public SimpleStringResponse updateUserDigitalDocumentStatus(
        Long digitalDocumentId
    ) {
        DigitalDocument digitalDocument = findDigitalDocumentByDigitalDocumentId(digitalDocumentId);
        digitalDocumentValidator.validateTargetEndDate(digitalDocument.getTargetEndDate(), "제출");

        User user = ActUserProvider.getNoneNull();
        DigitalDocumentUser digitalDocumentUser = getDigitalDocumentUserNoneNull(digitalDocument.getId(), user.getId());
        validate(digitalDocumentUser.getPdfPath(), "전자문서의 PDF 파일 생성전에는 제출하실 수 없습니다.");

        // complete 시에 유저의 주식수도 저장을 한다.
        digitalDocumentUser.setStockCount(findStockCountOnTheReferenceDate(digitalDocument, user));
        digitalDocumentUser.setLoanPrice(findLoanPriceFromMydataSummary(user.getId()));
        digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.COMPLETE);
        digitalDocumentUserRepository.save(digitalDocumentUser);

        updateDigitalDocumentStockCountAndUserCount(digitalDocument);

        userVerificationHistoryService.create(
            user.getId(), VerificationType.SIGNATURE, VerificationOperationType.SIGNATURE, digitalDocumentUser.getId()
        );

        digitalDocumentCertificationService.generate(digitalDocument, digitalDocumentUser);

        return SimpleStringResponseUtil.ok();
    }

    private Long findStockCountOnTheReferenceDate(DigitalDocument digitalDocument, User user) {
        if (digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY) {
            return userHoldingStockOnReferenceDateService.getUserHoldingStockOnReferenceDate(
                    user.getId(), digitalDocument.getStockCode(), digitalDocument.getStockReferenceDate()
                )
                .map(UserHoldingStockOnReferenceDate::getQuantity)
                .orElseThrow(() -> new BadRequestException("기준일 %s에 보유 주식 정보가 없습니다.".formatted(digitalDocument.getStockReferenceDate())));
        }
        return userHoldingStockService.getUserHoldingStock(user.getId(), digitalDocument.getStockCode()).getQuantity();
    }

    private Long findLoanPriceFromMydataSummary(Long userId) {
        return Optional.ofNullable(myDataSummaryService.getByUserId(userId).getLoanPrice()).orElse(0L);
    }

    @UseGuards({HoldingStockGuard.class})
    public SimpleStringResponse deleteUserDigitalDocument(Long digitalDocumentId) {
        DigitalDocument digitalDocument = findDigitalDocumentByDigitalDocumentId(digitalDocumentId);

        digitalDocumentValidator.validateTargetEndDate(digitalDocument.getTargetEndDate(), "삭제");

        deleteUserDigitalDocument(
            digitalDocument,
            getDigitalDocumentUserNoneNull(digitalDocument.getId(), ActUserProvider.getNoneNull().getId())
        );

        return SimpleStringResponseUtil.ok();
    }

    public void deleteUserDigitalDocument(DigitalDocumentUser digitalDocumentUser) {
        DigitalDocument digitalDocument = findDigitalDocumentByDigitalDocumentId(digitalDocumentUser.getDigitalDocumentId());
        deleteUserDigitalDocument(digitalDocument, digitalDocumentUser);
    }

    private void deleteUserDigitalDocument(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        deletePdf(digitalDocument, digitalDocumentUser);

        digitalDocumentItemUserAnswerService.deleteByDigitalDocumentUserId(digitalDocument.getId(), digitalDocumentUser.getUserId());
        digitalDocumentUserRepository.deleteById(digitalDocumentUser.getId());

        updateDigitalDocumentStockCountAndUserCount(digitalDocument);
    }

    private void deletePdf(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        deleteDigitalDocumentPdf(digitalDocumentUser.getPdfPath());
        deleteDigitalDocumentCertificationPdf(digitalDocument, digitalDocumentUser);
    }

    private void deleteDigitalDocumentPdf(String pdfPath) {
        if (StringUtils.isBlank(pdfPath)) {
            return;
        }

        try {
            s3Service.removeObjectInRetry(pdfPath.trim());
        } catch (Exception e) {
            log.error("Failed to delete s3 object: {}", e.getMessage(), e);
        }
    }

    private void deleteDigitalDocumentCertificationPdf(DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {
        final List<String> certificationFullPaths = List.of(
            getDigitalDocumentUploadPath(digitalDocument, digitalDocumentUser, digitalDocumentUser.getUpdatedAt()),
            getDigitalDocumentUploadPath(digitalDocument, digitalDocumentUser, digitalDocumentUser.getUpdatedAt().plusDays(1L))
        );

        final List<Exception> exceptionList = new ArrayList<>();

        boolean isDeleted = certificationFullPaths.stream()
            .anyMatch(certificationFullPath -> deleteDigitalDocumentCertificationPdfInRetry(certificationFullPath, exceptionList));

        if (!isDeleted) {
            exceptionList.forEach(e -> log.error("Failed to delete s3 object: {}", e.getMessage(), e));
        }
    }

    private boolean deleteDigitalDocumentCertificationPdfInRetry(String certificationFullPath, List<Exception> exceptionList) {
        try {
            return s3Service.removeObjectInRetry(certificationFullPath);
        } catch (Exception ex) {
            exceptionList.add(ex);
            return false;
        }
    }

    private String getDigitalDocumentUploadPath(
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser,
        LocalDateTime certificationCreatedAt
    ) {
        String certificationOriginalFilename = FilenameUtil.getDigitalDocumentCertificationFilename(
            DigitalDocumentFilenameDto.builder()
                .stockName(digitalDocumentUser.getStockName())
                .postTitle(digitalDocument.getPost().getTitle())
                .userName(digitalDocumentUser.getName())
                .userBirthDate(digitalDocumentUser.getBirthDate())
                .build(),
            certificationCreatedAt
        );

        return FilenameUtil.getDigitalDocumentUploadPath(digitalDocument.getId(), certificationOriginalFilename).trim();
    }


    @UseGuards({HoldingStockGuard.class})
    public DownloadFile getUserDigitalDocumentPdf(
        Long digitalDocumentId
    ) {
        User user = ActUserProvider.getNoneNull();
        return getUserDigitalDocumentPdf(digitalDocumentId, user.getId());
    }

    @UseGuards({HoldingStockGuard.class})
    public DownloadFile getUserDigitalDocumentPdf(Long digitalDocumentId, Long userId) {
        DigitalDocumentUser digitalDocumentUser = getDigitalDocumentUserNoneNull(digitalDocumentId, userId);
        validate(digitalDocumentUser.getPdfPath(), "전자문서 PDF 파일 정보가 없습니다.");

        //s3 private read
        return DownloadFile
            .builder()
            .fileName(FilenameUtils.getName(digitalDocumentUser.getPdfPath()))
            .resource(new InputStreamResource(s3Service.readObject(digitalDocumentUser.getPdfPath())))
            .build();
    }

    private void validate(String value, String errorMessage) {
        if (StringUtils.isBlank(value)) {
            throw new BadRequestException(errorMessage);
        }
    }

    private DigitalDocument findDigitalDocumentByDigitalDocumentId(Long digitalDocumentId) {
        return digitalDocumentRepository.findById(digitalDocumentId)
            .orElseThrow(() -> new NotFoundException("전자 문서 정보가 존재하지 않습니다."));
    }

    public List<DigitalDocumentUser> findAllByUserId(Long userId) {
        return digitalDocumentUserRepository.findAllByUserId(userId);
    }

    public List<DigitalDocumentUser> getDigitalDocumentUsersInSaveStatus() {
        return digitalDocumentUserRepository.findAllByDigitalDocumentAnswerStatusIn(List.of(DigitalDocumentAnswerStatus.SAVE));
    }

    public List<DigitalDocumentUser> getDigitalDocumentUsersInSaveStatus(Long digitalDocumentId) {
        return digitalDocumentUserRepository.findAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusIn(
            digitalDocumentId,
            List.of(DigitalDocumentAnswerStatus.SAVE)
        );
    }

    public List<DigitalDocumentUser> getUnfinishedDigitalDocumentUsersForCleanup() {
        return getDigitalDocumentUsersInSaveStatus()
            .stream()
            .filter(this::isOneHourPast)
            .toList();
    }

    private boolean isOneHourPast(DigitalDocumentUser digitalDocumentUser) {
        return DateTimeUtil.isBeforeInHours(digitalDocumentUser.getUpdatedAt(), ONE_HOUR);
    }

    public void deleteAllUnfinishedUserDigitalDocuments(Long digitalDocumentId) {
        getDigitalDocumentUsersInSaveStatus(digitalDocumentId).forEach(this::deleteUserDigitalDocument);
    }

    public SimplePageDto<DigitalDocumentUserDetailsResponse> getDigitalDocumentUsers(DigitalDocumentUserSearchDto documentUserSearchDto) {
        Page<DigitalDocumentUser> userPage = documentUserSearchDto.isNameSearch()
            ? getAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusInAndNameContaining(documentUserSearchDto)
            : getAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusIn(documentUserSearchDto);

        return new SimplePageDto<>(userPage.map(digitalDocumentUserDetailsResponseConverter));
    }

    public DigitalDocumentUser save(DigitalDocumentUser digitalDocumentUser) {
        return digitalDocumentUserRepository.save(digitalDocumentUser);
    }

    private Page<DigitalDocumentUser> getAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusInAndNameContaining(
        DigitalDocumentUserSearchDto documentUserSearchDto
    ) {
        return digitalDocumentUserRepository.findAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusInAndNameContaining(
            documentUserSearchDto.getDigitalDocumentId(),
            List.of(DigitalDocumentAnswerStatus.COMPLETE),
            documentUserSearchDto.getKeyword(),
            documentUserSearchDto.getPageable()
        );
    }

    private Page<DigitalDocumentUser> getAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusIn(
        DigitalDocumentUserSearchDto documentUserSearchDto
    ) {
        return digitalDocumentUserRepository.findAllByDigitalDocumentIdAndDigitalDocumentAnswerStatusIn(
            documentUserSearchDto.getDigitalDocumentId(),
            List.of(DigitalDocumentAnswerStatus.COMPLETE),
            documentUserSearchDto.getPageable()
        );
    }
}
