package ag.act.service.stock;

import ag.act.entity.StockReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.BadRequestException;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.StockReferenceDateRepository;
import ag.act.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockReferenceDateWithDigitalDocumentUpdateService {
    private final StockReferenceDateRepository stockReferenceDateRepository;
    private final DigitalDocumentRepository digitalDocumentRepository;

    public StockReferenceDate update(Long digitalDocumentId, Long stockReferenceDateId, LocalDate updateReferenceDate) {
        final StockReferenceDate stockReferenceDate = getStockReferenceDate(stockReferenceDateId);
        final DigitalDocument digitalDocument = getDigitalDocument(digitalDocumentId);

        validateDigitalDocumentForUpdate(stockReferenceDate, digitalDocument);

        /*
        stockReferenceDate 의 기준일로 조회된 전자문서 리스트를 조회한다
            1개일때 digitalDocument 와 일치하는지 조회한다 -> 매치되면 둘다 수정
            2개이상일때 digitalDocument 의 기준일을 업데이트하고 신규로 기준일 테이블에 등록
                                                        신규 등록시 있으면 스킵 없으면 등록
         */
        if (isOnlyOneDigitalDocumentThatUsingReferenceDate(stockReferenceDateId, stockReferenceDate.getStockCode())) {
            return updateBothDigitalDocumentAndStockReferenceDate(updateReferenceDate, digitalDocument, stockReferenceDate);
        } else {
            updateDigitalDocument(updateReferenceDate, digitalDocument);
            return getOrCreateStockReferenceDate(updateReferenceDate, stockReferenceDate.getStockCode());
        }
    }

    private StockReferenceDate updateBothDigitalDocumentAndStockReferenceDate(
        LocalDate updateReferenceDate,
        DigitalDocument digitalDocument,
        StockReferenceDate stockReferenceDate
    ) {
        updateDigitalDocument(updateReferenceDate, digitalDocument);

        return updateStockReferenceDate(updateReferenceDate, stockReferenceDate);
    }

    private StockReferenceDate updateStockReferenceDate(LocalDate updateReferenceDate, StockReferenceDate stockReferenceDate) {
        stockReferenceDate.setReferenceDate(updateReferenceDate);
        return stockReferenceDateRepository.save(stockReferenceDate);
    }

    private void updateDigitalDocument(LocalDate updateReferenceDate, DigitalDocument digitalDocument) {
        digitalDocument.setStockReferenceDate(updateReferenceDate);
        digitalDocumentRepository.save(digitalDocument);
    }

    private void validateDigitalDocumentForUpdate(StockReferenceDate stockReferenceDate, DigitalDocument digitalDocument) {
        if (!Objects.equals(stockReferenceDate.getReferenceDate(), digitalDocument.getStockReferenceDate())) {
            throw new BadRequestException("전자문서의 기준일과 일치하지 않습니다.");
        }

        if (DateTimeUtil.isNowAfter(digitalDocument.getTargetStartDate())) {
            throw new BadRequestException("기준일을 변경하려는 전자문서 참여일이 이미 시작되었습니다.");
        }
    }

    private StockReferenceDate getStockReferenceDate(Long stockReferenceDateId) {
        return stockReferenceDateRepository.findById(stockReferenceDateId)
            .orElseThrow(() -> new BadRequestException("해당 기준일 정보가 없습니다."));
    }

    private Optional<StockReferenceDate> getByStockCodeAndReferenceDate(String stockCode, LocalDate referenceDate) {
        return stockReferenceDateRepository.findByStockCodeAndReferenceDate(
            stockCode,
            referenceDate
        );
    }

    private DigitalDocument getDigitalDocument(Long digitalDocumentId) {
        return digitalDocumentRepository.findById(digitalDocumentId)
            .orElseThrow(() -> new BadRequestException("기준일 수정대상 전자문서 위임장이 없습니다."));
    }

    private StockReferenceDate getOrCreateStockReferenceDate(LocalDate referenceDate, String stockCode) {
        return getByStockCodeAndReferenceDate(stockCode, referenceDate)
            .orElseGet(() -> {
                StockReferenceDate newStockReferenceDate = new StockReferenceDate();
                newStockReferenceDate.setStockCode(stockCode);
                return updateStockReferenceDate(referenceDate, newStockReferenceDate);
            });
    }

    private boolean isOnlyOneDigitalDocumentThatUsingReferenceDate(Long stockReferenceDateId, String stockCode) {
        return countDigitalDocumentUsingStockReferenceDate(stockReferenceDateId, stockCode) == 1;
    }

    private int countDigitalDocumentUsingStockReferenceDate(Long stockReferenceDateId, String stockCode) {
        return getAllDigitalDocumentsByStockReferenceDateIdAndStockCode(stockReferenceDateId, stockCode).size();
    }

    private List<DigitalDocument> getAllDigitalDocumentsByStockReferenceDateIdAndStockCode(Long stockReferenceDateId, String stockCode) {
        return digitalDocumentRepository.findAllDigitalDocumentByStockReferenceDateIdAndStockCode(
            stockReferenceDateId,
            stockCode
        );
    }
}
