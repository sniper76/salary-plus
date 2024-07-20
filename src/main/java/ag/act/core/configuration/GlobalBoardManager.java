package ag.act.core.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GlobalBoardManager {
    private final String stockCode;

    public GlobalBoardManager(@Value("${act.board.stock-code}") String stockCode) {
        this.stockCode = stockCode;
    }

    public boolean isGlobalStockCode(String stockCode) {
        return this.stockCode.equals(stockCode);
    }
}
