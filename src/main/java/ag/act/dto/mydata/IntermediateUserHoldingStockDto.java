package ag.act.dto.mydata;

import ag.act.enums.StockCreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntermediateUserHoldingStockDto {
    private String stockCode;
    private String stockName;
    private String myDataProdCode;
    private StockCreditType creditType;
    private Long quantity;
    private Long purchasePrice;
    private Long cashQuantity;
    private Long creditQuantity;
    private Long secureLoanQuantity;

    public IntermediateUserHoldingStockDto withCreditType(StockCreditType creditType) {
        this.creditType = creditType;
        creditType.updateCreditTypeQuantity(this);
        return this;
    }

    public Long getQuantity() {
        return quantity == null ? 0L : quantity;
    }

    public Long getCashQuantity() {
        return cashQuantity == null ? 0L : cashQuantity;
    }

    public Long getCreditQuantity() {
        return creditQuantity == null ? 0L : creditQuantity;
    }

    public Long getSecureLoanQuantity() {
        return secureLoanQuantity == null ? 0L : secureLoanQuantity;
    }

    public Long getPurchasePrice() {
        return purchasePrice == null ? 0L : purchasePrice;
    }
}
