package ag.act.module.dart;

import ag.act.entity.StockDartCorporation;
import ag.act.model.Status;
import ag.act.module.dart.dto.CorpCodeItem;
import org.springframework.stereotype.Component;

@Component
public class DartCorpCodeItemConverter {

    public StockDartCorporation convert(CorpCodeItem corpCodeItem) {
        final StockDartCorporation stockDartCorporation = new StockDartCorporation();
        stockDartCorporation.setCorpCode(corpCodeItem.getCorpCode().trim());
        stockDartCorporation.setCorpName(corpCodeItem.getCorpName().trim());
        stockDartCorporation.setStockCode(corpCodeItem.getStockCode().trim());
        stockDartCorporation.setModifyDate(corpCodeItem.getModifyDate().trim());
        stockDartCorporation.setStatus(Status.ACTIVE);

        return stockDartCorporation;
    }
}
