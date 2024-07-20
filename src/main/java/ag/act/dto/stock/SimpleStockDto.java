package ag.act.dto.stock;

import ag.act.entity.Stock;
import ag.act.repository.interfaces.SimpleStock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SimpleStockDto implements SimpleStock {
    private String code;
    private String name;
    private String standardCode;

    public static SimpleStockDto of(String code, String name, String standardCode) {
        return new SimpleStockDto(code, name, standardCode);
    }

    public static SimpleStockDto from(SimpleStock stock) {
        return of(stock.getCode(), stock.getName(), stock.getStandardCode());
    }

    public static SimpleStockDto from(Stock stock) {
        return of(stock.getCode(), stock.getName(), stock.getStandardCode());
    }
}
