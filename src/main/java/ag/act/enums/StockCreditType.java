package ag.act.enums;

import ag.act.dto.mydata.IntermediateUserHoldingStockDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum StockCreditType {
    CASH("01", "현금") {
        @Override
        public void updateCreditTypeQuantity(IntermediateUserHoldingStockDto stockDto) {
            stockDto.setCashQuantity(stockDto.getQuantity());
        }
    },
    CREDIT("02", "신용") {
        @Override
        public void updateCreditTypeQuantity(IntermediateUserHoldingStockDto stockDto) {
            stockDto.setCreditQuantity(stockDto.getQuantity());
        }
    },
    SECURED_LOAN("03", "담보대출") {
        @Override
        public void updateCreditTypeQuantity(IntermediateUserHoldingStockDto stockDto) {
            stockDto.setSecureLoanQuantity(stockDto.getQuantity());
        }
    };

    private final String creditTypeCode;
    private final String creditTypeName;

    StockCreditType(String creditTypeCode, String creditTypeName) {
        this.creditTypeCode = creditTypeCode;
        this.creditTypeName = creditTypeName;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static StockCreditType fromValue(String value) {
        for (StockCreditType b : StockCreditType.values()) {
            if (b.getCreditTypeCode().equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 StockCreditType '%s' 타입입니다.".formatted(value));
    }

    public IntermediateUserHoldingStockDto merge(IntermediateUserHoldingStockDto target, IntermediateUserHoldingStockDto addition) {
        target.setQuantity(target.getQuantity() + addition.getQuantity());
        target.setCashQuantity(target.getCashQuantity() + addition.getCashQuantity());
        target.setCreditQuantity(target.getCreditQuantity() + addition.getCreditQuantity());
        target.setSecureLoanQuantity(target.getSecureLoanQuantity() + addition.getSecureLoanQuantity());
        target.setPurchasePrice(target.getPurchasePrice() + addition.getPurchasePrice());

        return target;
    }

    public abstract void updateCreditTypeQuantity(IntermediateUserHoldingStockDto intermediateUserHoldingStockDto);
}
