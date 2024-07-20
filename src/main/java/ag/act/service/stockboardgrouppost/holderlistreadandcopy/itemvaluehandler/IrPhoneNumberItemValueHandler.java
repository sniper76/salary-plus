package ag.act.service.stockboardgrouppost.holderlistreadandcopy.itemvaluehandler;

import ag.act.entity.Stock;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.service.stock.StockService;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyItemWithValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IrPhoneNumberItemValueHandler implements HolderListReadAndCopyItemValueHandler {
    private final StockService stockService;

    @Override
    public HolderListReadAndCopyItemType getItemType() {
        return HolderListReadAndCopyItemType.IR_PHONE_NUMBER;
    }

    @Override
    public boolean supports(HolderListReadAndCopyItemType itemType) {
        return getItemType() == itemType;
    }

    @Override
    public HolderListReadAndCopyItemWithValue handle(String stockCode) {
        return HolderListReadAndCopyItemWithValue.of(
            getItemType(),
            getIrPhoneNumber(stockCode)
        );
    }

    private String getIrPhoneNumber(String stockCode) {
        final Stock stock = stockService.getStock(stockCode);
        if (stock.getRepresentativePhoneNumber() == null) {
            return "";
        }
        return stock.getRepresentativePhoneNumber();
    }
}
