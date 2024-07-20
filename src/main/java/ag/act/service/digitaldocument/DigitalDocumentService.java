package ag.act.service.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.dto.DigitalDocumentMatchingUserDto;
import ag.act.dto.digitaldocument.DigitalDocumentProgressPeriodDto;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.model.Status;
import ag.act.model.UpdatePostRequestDigitalDocument;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.interfaces.JoinCount;
import ag.act.service.digitaldocument.documenttype.DigitalDocumentTypeService;
import ag.act.service.digitaldocument.documenttype.DigitalDocumentTypeServiceResolver;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.validator.document.DigitalDocumentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes"})
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DigitalDocumentService {
    private final DigitalDocumentTypeServiceResolver digitalDocumentTypeServiceResolver;
    private final DigitalDocumentValidator digitalDocumentValidator;
    private final DigitalDocumentRepository digitalDocumentRepository;
    private final StockReferenceDateService stockReferenceDateService;
    private final UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;
    private final UserHoldingStockService userHoldingStockService;

    public IDigitalDocument createDigitalDocumentAndGet(Post post, CreateDigitalDocumentRequest createRequest) {
        digitalDocumentValidator.validateCommon(createRequest);
        DigitalDocumentTypeService service = getDigitalDocumentTypeService(createRequest.getType());

        return service.create(post, createRequest);
    }

    public DownloadFile previewDigitalDocument(PreviewDigitalDocumentRequest previewRequest) {
        DigitalDocumentTypeService service = getDigitalDocumentTypeService(previewRequest.getType());

        return service.preview(previewRequest);
    }

    public UserDigitalDocumentResponse getPostResponseDigitalDocument(
        Post post, User currentUser
    ) {
        if (!isDigitalDocument(post)) {
            return null;
        }

        DigitalDocument digitalDocument = post.getDigitalDocument();
        if (DigitalDocumentType.DIGITAL_PROXY != digitalDocument.getType()) {
            digitalDocument.setStockReferenceDate(KoreanDateTimeUtil.getTodayLocalDate());
        }

        Long stockCount = getUserStockCount(currentUser, digitalDocument);

        DigitalDocumentTypeService service = digitalDocumentTypeServiceResolver.getService(digitalDocument.getType());
        return service.getPostResponseDigitalDocument(post, currentUser, stockCount);
    }

    private boolean isDigitalDocument(Post post) {
        return post.getDigitalDocument() != null
            && post.getBoard().getCategory().isDigitalDocumentActionGroup();
    }

    private Long getUserStockCount(User currentUser, DigitalDocument digitalDocument) {
        if (digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY) {
            return userHoldingStockOnReferenceDateService.getQuantityUserHoldingStockOnReferenceDate(
                currentUser.getId(), digitalDocument.getStockCode(), digitalDocument.getStockReferenceDate()
            );
        }
        return userHoldingStockService.getUserHoldingStock(currentUser.getId(), digitalDocument.getStockCode())
            .getQuantity();
    }

    public Optional<DigitalDocument> findDigitalDocument(Long digitalDocumentId) {
        return digitalDocumentRepository.findById(digitalDocumentId);
    }

    public Optional<DigitalDocument> findDigitalDocument(String stockCode, LocalDate referenceDate) {
        return digitalDocumentRepository.findByStockCodeAndStockReferenceDate(stockCode, referenceDate);
    }

    public DigitalDocument getDigitalDocument(Long digitalDocumentId) {
        return findDigitalDocument(digitalDocumentId)
            .orElseThrow(() -> new BadRequestException("전자문서 정보가 없습니다."));
    }

    public Map<String, List<DigitalDocumentUser>> getInProgressStockCodeDigitalDocumentsUsersMap(Long userId, List<String> stockCodes) {
        final List<DigitalDocumentMatchingUserDto> digitalDocumentMatchingUsers =
            digitalDocumentRepository.findAllInProgressDigitalDocumentWithMatchingUser(userId, stockCodes);

        return digitalDocumentMatchingUsers.stream()
            .collect(Collectors.groupingBy(
                dto -> dto.getDigitalDocument().getStockCode(),
                Collectors.mapping(DigitalDocumentMatchingUserDto::getDigitalDocumentUser, Collectors.toList())
            ));
    }

    public DigitalDocument updateDigitalDocument(
        DigitalDocument digitalDocument, UpdatePostRequestDigitalDocument updatePostRequestDigitalDocument
    ) {
        validateAndSetStockReferenceDate(digitalDocument, updatePostRequestDigitalDocument.getStockReferenceDateId());

        String title = updatePostRequestDigitalDocument.getTitle();
        String content = updatePostRequestDigitalDocument.getContent();
        if (StringUtils.isNotBlank(title)) {
            digitalDocument.setTitle(title.trim());
        }
        if (StringUtils.isNotBlank(content)) {
            digitalDocument.setContent(content.trim());
        }
        if (updatePostRequestDigitalDocument.getTargetStartDate() != null) {
            digitalDocument.setTargetStartDate(
                DateTimeConverter.convert(updatePostRequestDigitalDocument.getTargetStartDate())
            );
        }
        if (updatePostRequestDigitalDocument.getTargetEndDate() != null) {
            digitalDocument.setTargetEndDate(
                DateTimeConverter.convert(updatePostRequestDigitalDocument.getTargetEndDate())
            );
        }

        return digitalDocument;
    }

    public Optional<JoinCount> findDigitalDocumentCountByPostId(Long postId) {
        return digitalDocumentRepository.findDigitalDocumentCountByPostId(postId);
    }

    public Optional<DigitalDocument> findDigitalDocumentByPostId(Long postId) {
        return digitalDocumentRepository.findByPostId(postId);
    }

    private DigitalDocumentTypeService getDigitalDocumentTypeService(String type) {
        DigitalDocumentType digitalDocumentType = DigitalDocumentType.fromValue(type);

        return digitalDocumentTypeServiceResolver.getService(digitalDocumentType);
    }

    private void validateAndSetStockReferenceDate(
        DigitalDocument digitalDocument, Long stockReferenceDateId
    ) {
        if (stockReferenceDateId == null) {
            return;
        }
        digitalDocumentValidator.validateTargetStartDateIsBeforeToday(digitalDocument.getTargetStartDate());
        LocalDate stockReferenceDate = stockReferenceDateService.findReferenceDate(
            digitalDocument.getType().name(), digitalDocument.getStockCode(), stockReferenceDateId
        );
        digitalDocument.setStockReferenceDate(stockReferenceDate);
    }

    public boolean isFinished(Long digitalDocumentId) {
        return DateTimeUtil.isNowAfter(getDigitalDocument(digitalDocumentId).getTargetEndDate());
    }

    public void deleteDigitalDocument(DigitalDocument digitalDocument, Status status, LocalDateTime deleteTime) {
        if (digitalDocument == null) {
            return;
        }

        digitalDocument.setStatus(status);
        digitalDocument.setDeletedAt(deleteTime);
        digitalDocumentRepository.save(digitalDocument);
    }

    public List<DigitalDocument> getDigitalDocuments(
        DigitalDocumentType type, Status status, LocalDateTime startDate, LocalDateTime endDate
    ) {
        return digitalDocumentRepository.findAllByTypeAndStatusAndTargetStartDateIsLessThanAndTargetEndDateIsGreaterThanEqual(
            type, status, startDate, endDate
        );
    }

    public List<DigitalDocument> getDigitalDocuments(
        Status status, LocalDateTime endDate
    ) {
        return digitalDocumentRepository.findAllByStatusAndTargetEndDateIsLessThanEqual(
            status, endDate
        );
    }

    public boolean existsProcessingByStockCodeAndAcceptor(String stockCode, Long userId) {
        return digitalDocumentRepository.existsByStockCodeAndAcceptUserIdAndTargetEndDateGreaterThanEqual(
            stockCode, userId, DateTimeUtil.getTodayLocalDateTime()
        );
    }

    public boolean existsProcessingByAcceptor(Long userId) {
        return digitalDocumentRepository.existsByAcceptUserIdAndTargetEndDateGreaterThanEqual(
            userId, DateTimeUtil.getTodayLocalDateTime()
        );
    }

    public DigitalDocumentProgressPeriodDto getDigitalDocumentProgressPeriod(Long postId) {
        return digitalDocumentRepository.findDigitalDocumentProgressPeriodByDigitalDocumentId(postId)
            .orElseThrow(() -> new BadRequestException("Digital Document 를 찾을 수 없습니다."));
    }

    public Long getDigitalDocumentPostId(Long digitalDocumentId) {
        return digitalDocumentRepository.findDigitalDocumentPostIdByDigitalDocumentId(digitalDocumentId)
            .orElseThrow(() -> new BadRequestException("Digital Document 를 찾을 수 없습니다."));
    }
}
