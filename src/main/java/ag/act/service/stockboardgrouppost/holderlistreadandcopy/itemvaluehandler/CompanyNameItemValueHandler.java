package ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler;

import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stock.StockService;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyItemWithValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyNameItemValueHandler implements HolderListReadAndCopyItemValueHandler {
    private final StockService stockService;

    @Override
    public HolderListReadAndCopyItemType getItemType() {
        return HolderListReadAndCopyItemType.COMPANY_NAME;
    }

    @Override
    public boolean supports(HolderListReadAndCopyItemType itemType) {
        return getItemType() == itemType;
    }

    @Override
    public HolderListReadAndCopyItemWithValue handle(String stockCode) {
        return HolderListReadAndCopyItemWithValue.of(
            getItemType(),
            stockService.getSimpleStock(stockCode).getName()
        );
    }
}
