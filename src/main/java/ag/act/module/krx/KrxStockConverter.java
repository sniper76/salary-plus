package ag.act.module.krx;

import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.entity.Stock;
import ag.act.util.NumberUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KrxStockConverter {

    public Stock convert(Map<String, StkItemPriceDto> priceMap, StockItemDto stockItemDto) {
        final Stock stock = new Stock();
        stock.setCode(stockItemDto.getISU_SRT_CD());
        stock.setStandardCode(stockItemDto.getISU_CD());
        stock.setName(stockItemDto.getISU_ABBRV());
        stock.setFullName(stockItemDto.getISU_NM());
        stock.setMarketType(stockItemDto.getMKT_TP_NM());
        stock.setStockType(stockItemDto.getKIND_STKCERT_TP_NM());
        stock.setClosingPrice(getClosingPrice(priceMap, stockItemDto));
        stock.setTotalIssuedQuantity(NumberUtil.toLong(stockItemDto.getLIST_SHRS(), 0L));
        return stock;
    }

    private Integer getClosingPrice(Map<String, StkItemPriceDto> priceMap, StockItemDto stockItemDto) {
        final StkItemPriceDto stkItemPriceDto = priceMap.get(stockItemDto.getISU_SRT_CD());
        if (stkItemPriceDto == null) {
            return 0;
        }

        return NumberUtil.toInteger(stkItemPriceDto.getTDD_CLSPRC(), 0);
    }
}
