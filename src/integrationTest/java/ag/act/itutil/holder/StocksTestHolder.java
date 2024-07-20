package ag.act.itutil.holder;

import ag.act.entity.Stock;

public class StocksTestHolder extends ActEntityTestHolder<Stock, String> {
    
    @Override
    public String getId(Stock item) {
        return item.getCode();
    }
}
