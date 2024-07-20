package ag.act.dto.mydata;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT(301, "입금"),
    WITHDRAWAL(302, "출금"),
    REPAYMENT(303, "상환"),
    DEPOSIT_CANCEL(311, "입금취소"),
    WITHDRAWAL_CANCEL(312, "출금취소"),
    DEPOSIT_STOCK(321, "입고"),
    WITHDRAWAL_STOCK(322, "출고"),
    BUY(341, "매수"),
    SELL(342, "매도"),
    OTHER_INCOME(381, "기타(입)"),
    OTHER_EXPENSE(382, "기타(출)"),
    OTHER_INCOME_CANCEL(391, "기타취소(입)"),
    OTHER_EXPENSE_CANCEL(392, "기타취소(출)"),
    UNKNOWN(999, "알수없음");

    private final int code;
    private final String description;

    public boolean isSellTransaction() {
        return getStockSellTransactions().contains(this);
    }

    public boolean isBuyTransaction() {
        return getStockBuyTransactions().contains(this);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TransactionType fromValue(String codeValue) {
        for (TransactionType b : TransactionType.values()) {
            if (String.valueOf(b.getCode()).equals(codeValue)) {
                return b;
            }
        }
        return UNKNOWN;
    }

    public static List<TransactionType> getStockTransactions() {
        return List.of(
            TransactionType.BUY,
            TransactionType.SELL,
            TransactionType.DEPOSIT_STOCK,
            TransactionType.WITHDRAWAL_STOCK
        );
    }

    public static List<TransactionType> getStockBuyTransactions() {
        return List.of(
            TransactionType.BUY,
            TransactionType.DEPOSIT_STOCK
        );
    }

    public static List<TransactionType> getStockSellTransactions() {
        return List.of(
            TransactionType.SELL,
            TransactionType.WITHDRAWAL_STOCK
        );
    }
}

