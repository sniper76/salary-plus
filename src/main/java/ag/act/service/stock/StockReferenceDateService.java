package ag.act.service.stock;

import ag.act.configuration.initial.caching.CachingType;
import ag.act.converter.stock.StockReferenceDateRequestConverter;
import ag.act.dto.StockReferenceDateDto;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.StockReferenceDateRepository;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.validator.StockReferenceDateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockReferenceDateService {
    private static final int THREE_MONTHS = 3;
    private final StockReferenceDateRepository stockReferenceDateRepository;
    private final StockReferenceDateValidator stockReferenceDateValidator;
    private final StockReferenceDateRequestConverter stockReferenceDateRequestConverter;
    private final DigitalDocumentRepository digitalDocumentRepository;

    @Cacheable(CachingType.Fields.USER_HOLDING_STOCK_ON_REFERENCE_WITHIN_THREE_MONTHS)
    public List<StockReferenceDate> getStockReferenceDatesWithinThreeMonths(String stockCode) {
        final LocalDate startDate = getLocalDateInPastThreeMonths();
        final LocalDate endDate = getTodayLocalDate();

        return stockReferenceDateRepository.findAllByStockCodeAndReferenceDateBetween(stockCode, startDate, endDate);
    }

    public List<StockReferenceDate> getAllStockReferenceDatesWithinThreeMonths(List<String> stockCodes) {
        final LocalDate startDate = getLocalDateInPastThreeMonths();
        final LocalDate endDate = getTodayLocalDate();

        return stockReferenceDateRepository.findAllByStockCodeInAndReferenceDateBetween(stockCodes, startDate, endDate);
    }

    private LocalDate getTodayLocalDate() {
        return KoreanDateTimeUtil.getTodayLocalDate();
    }

    private LocalDate getLocalDateInPastThreeMonths() {
        return KoreanDateTimeUtil.getPastMonthFromCurrent(THREE_MONTHS).toLocalDate();
    }

    public Optional<StockReferenceDate> findById(Long stockReferenceDateId) {
        return stockReferenceDateRepository.findById(stockReferenceDateId);
    }

    public StockReferenceDate getStockReferenceDate(Long stockReferenceDateId) {
        return findById(stockReferenceDateId)
            .orElseThrow(() -> new BadRequestException("해당 기준일 정보가 없습니다."));
    }

    public StockReferenceDate createStockReferenceDate(StockReferenceDateDto stockReferenceDateDto) {
        validateForCreate(stockReferenceDateDto);

        return stockReferenceDateRepository.save(
            stockReferenceDateRequestConverter.convert(stockReferenceDateDto)
        );
    }

    public void deleteStockReferenceDate(StockReferenceDate stockReferenceDate) {
        stockReferenceDateRepository.delete(stockReferenceDate);
    }

    private void validateForCreate(StockReferenceDateDto stockReferenceDateDto) {
        stockReferenceDateValidator.validate(stockReferenceDateDto);

        getByStockCodeAndReferenceDate(stockReferenceDateDto.getStockCode(), stockReferenceDateDto.getReferenceDate())
            .ifPresent(stockReferenceDate -> {
                throw new BadRequestException("해당종목에 이미 존재하는 기준일입니다.");
            });
    }

    private Optional<StockReferenceDate> getByStockCodeAndReferenceDate(String stockCode, LocalDate referenceDate) {
        return stockReferenceDateRepository.findByStockCodeAndReferenceDate(
            stockCode,
            referenceDate
        );
    }

    public LocalDate findReferenceDate(String type, String stockCode, Long stockReferenceDateId) {
        if (!Objects.equals(DigitalDocumentType.DIGITAL_PROXY.name(), type)) {
            return null;
        }

        Optional<StockReferenceDate> optionalStockReferenceDate = findById(
            stockReferenceDateId
        );

        StockReferenceDate stockReferenceDate = optionalStockReferenceDate
            .orElseThrow(() -> new BadRequestException("해당 기준일 정보가 없습니다."));

        if (!Objects.equals(stockReferenceDate.getStockCode(), stockCode)) {
            throw new BadRequestException("해당 종목의 기준일 정보가 아닙니다.");
        }

        return stockReferenceDate.getReferenceDate();
    }

    public StockReferenceDate updateStockReferenceDate(
        Long stockReferenceDateId,
        StockReferenceDateDto updateStockReferenceDateDto
    ) {
        StockReferenceDate stockReferenceDate = getStockReferenceDate(stockReferenceDateId);

        validateUpdateReferenceDate(updateStockReferenceDateDto, stockReferenceDate);

        stockReferenceDate.setReferenceDate(updateStockReferenceDateDto.getReferenceDate());

        return stockReferenceDateRepository.save(stockReferenceDate);
    }

    private void validateUpdateReferenceDate(StockReferenceDateDto updateStockReferenceDateDto, StockReferenceDate stockReferenceDate) {
        if (!Objects.equals(stockReferenceDate.getStockCode(), updateStockReferenceDateDto.getStockCode())) {
            throw new BadRequestException("수정 대상 기준일 종목코드가 일치하지 않습니다.");
        }

        if (Objects.equals(stockReferenceDate.getReferenceDate(), updateStockReferenceDateDto.getReferenceDate())) {
            throw new BadRequestException("수정 대상 기준일이 기존 기준일과 동일합니다.");
        }

        validateStockReferenceDateIsAlreadyUsedByDigitalDocuments(stockReferenceDate.getId(), updateStockReferenceDateDto);
    }

    private void validateStockReferenceDateIsAlreadyUsedByDigitalDocuments(
        Long stockReferenceDateId,
        StockReferenceDateDto updateStockReferenceDateDto
    ) {
        List<DigitalDocument> documentsThatAlreadyUsingIt = getAllDigitalDocumentsByStockReferenceDateIdAndStockCode(
            stockReferenceDateId,
            updateStockReferenceDateDto.getStockCode()
        );

        if (!documentsThatAlreadyUsingIt.isEmpty()) {
            throw new BadRequestException(
                "%s 기준일에 이미 사용중인 전자문서가 존재합니다. 문서제목 : [%s]".formatted(
                    updateStockReferenceDateDto.getReferenceDate().format(DateTimeFormatUtil.yyyyMMdd()),
                    documentsThatAlreadyUsingIt.stream()
                        .map(DigitalDocument::getTitle)
                        .collect(Collectors.joining(","))
                )
            );
        }
    }

    private List<DigitalDocument> getAllDigitalDocumentsByStockReferenceDateIdAndStockCode(Long stockReferenceDateId, String stockCode) {
        return digitalDocumentRepository.findAllDigitalDocumentByStockReferenceDateIdAndStockCode(
            stockReferenceDateId, stockCode
        );
    }

    public List<StockReferenceDate> getReferenceDatesByStockCode(String stockCode) {
        return stockReferenceDateRepository.findAllByStockCode(stockCode);
    }

    public List<StockReferenceDate> findAllByStockCodeAndReferenceDate(String stockCode, LocalDate referenceDate) {
        return stockReferenceDateRepository.findAllByStockCodeAndReferenceDate(stockCode, referenceDate);
    }
}
