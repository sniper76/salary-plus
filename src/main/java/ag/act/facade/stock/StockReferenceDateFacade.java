package ag.act.facade.stock;

import ag.act.converter.stock.StockReferenceDateRequestConverter;
import ag.act.converter.stock.StockReferenceDateResponseConverter;
import ag.act.entity.StockReferenceDate;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.CreateStockReferenceDateRequest;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.service.stock.StockReferenceDateWithDigitalDocumentUpdateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockReferenceDateFacade {

    private final StockReferenceDateResponseConverter stockReferenceDateResponseConverter;
    private final StockReferenceDateRequestConverter stockReferenceDateRequestConverter;
    private final StockReferenceDateService stockReferenceService;
    private final StockReferenceDateWithDigitalDocumentUpdateService stockReferenceDateWithDigitalDocumentUpdateService;
    private final DigitalDocumentService digitalDocumentService;

    public ag.act.model.StockReferenceDateResponse createStockReferenceDate(
        String stockCode,
        CreateStockReferenceDateRequest createStockReferenceDateRequest
    ) {
        return stockReferenceDateResponseConverter.apply(
            stockReferenceService.createStockReferenceDate(
                stockReferenceDateRequestConverter.convert(stockCode, createStockReferenceDateRequest)
            )
        );
    }

    public void deleteStockReferenceDate(Long stockReferenceDateId) {
        StockReferenceDate stockReferenceDate = stockReferenceService.findById(stockReferenceDateId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 기준일입니다."));

        digitalDocumentService.findDigitalDocument(stockReferenceDate.getStockCode(), stockReferenceDate.getReferenceDate())
            .ifPresent(it -> {
                throw new BadRequestException(String.format(
                    "전자문서에서 사용된 종목(%s)의 기준일(%s)은 삭제할 수 없습니다.",
                    stockReferenceDate.getStockCode(), stockReferenceDate.getReferenceDate()));
            });

        stockReferenceService.deleteStockReferenceDate(stockReferenceDate);
    }

    public ag.act.model.StockReferenceDateResponse updateStockReferenceDate(
        String stockCode, Long stockReferenceDateId, ag.act.model.CreateStockReferenceDateRequest updateStockReferenceDateRequest
    ) {
        return stockReferenceDateResponseConverter.apply(
            stockReferenceService.updateStockReferenceDate(
                stockReferenceDateId,
                stockReferenceDateRequestConverter.convert(stockCode, updateStockReferenceDateRequest)
            )
        );
    }

    public ag.act.model.StockReferenceDateResponse updateDigitalDocumentStockReferenceDate(
        Long digitalDocumentId, Long stockReferenceDateId, ag.act.model.CreateStockReferenceDateRequest updateStockReferenceDateRequest
    ) {
        return stockReferenceDateResponseConverter.apply(
            stockReferenceDateWithDigitalDocumentUpdateService.update(
                digitalDocumentId,
                stockReferenceDateId,
                updateStockReferenceDateRequest.getReferenceDate()
            )
        );
    }

    public List<ag.act.model.StockReferenceDateResponse> getStockReferenceDates(String stockCode) {
        return stockReferenceDateResponseConverter.convertList(
            stockReferenceService.getReferenceDatesByStockCode(stockCode)
        );
    }
}
