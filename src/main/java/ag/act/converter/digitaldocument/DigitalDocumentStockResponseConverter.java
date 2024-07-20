package ag.act.converter.digitaldocument;

import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.DigitalDocumentStockResponse;
import ag.act.service.stock.StockReferenceDateService;
import ag.act.validator.StockReferenceDateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DigitalDocumentStockResponseConverter {
    private final StockReferenceDateService stockReferenceDateService;
    private final StockReferenceDateValidator stockReferenceDateValidator;

    public DigitalDocumentStockResponse convert(Stock stock, DigitalDocument digitalDocument) {
        return convert(stock, digitalDocument, null);
    }

    public DigitalDocumentStockResponse convert(Stock stock, DigitalDocument digitalDocument, Long referenceDateStockCount) {
        final DigitalDocumentStockResponse digitalDocumentStockResponse = new DigitalDocumentStockResponse()
            .code(stock.getCode())
            .standardCode(stock.getStandardCode())
            .name(stock.getName())
            .referenceDateStockCount(referenceDateStockCount)
            .referenceDate(digitalDocument.getStockReferenceDate());

        if (digitalDocument.getType() != DigitalDocumentType.DIGITAL_PROXY) {
            return digitalDocumentStockResponse;
        }

        return digitalDocumentStockResponse
            .referenceDateId(getStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate()).getId());
    }

    private StockReferenceDate getStockReferenceDate(String code, LocalDate stockReferenceDate1) {
        final List<StockReferenceDate> stockReferenceDateList = stockReferenceDateService.findAllByStockCodeAndReferenceDate(
            code, stockReferenceDate1
        );

        stockReferenceDateValidator.validateOnlyOneReferenceDate(stockReferenceDateList, code, stockReferenceDate1);

        return stockReferenceDateList.get(0);
    }
}
