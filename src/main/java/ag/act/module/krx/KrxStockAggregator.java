package ag.act.module.krx;

import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.entity.Stock;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KrxStockAggregator {
    private final KrxService krxService;
    private final KrxStockConverter krxStockConverter;

    public KrxStockAggregator(KrxService krxService, KrxStockConverter krxStockConverter) {
        this.krxService = krxService;
        this.krxStockConverter = krxStockConverter;
    }

    public List<Stock> getStocksFromKrxService(String date) {
        final List<StockItemDto> allIsuBasicInfos = getAllIsuBasicInfos(date);
        final Map<String, StkItemPriceDto> priceMap = getAllPriceMap(date);

        return allIsuBasicInfos.stream()
            .map(stockItemDto -> krxStockConverter.convert(priceMap, stockItemDto))
            .toList();
    }

    private Map<String, StkItemPriceDto> getAllPriceMap(String date) {
        return krxService.getAllIsuDailyInfos(date)
            .stream()
            .collect(Collectors.toMap(StkItemPriceDto::getISU_CD, Function.identity()));
    }

    private List<StockItemDto> getAllIsuBasicInfos(String date) {
        return krxService.getAllIsuBasicInfos(date);
    }
}
