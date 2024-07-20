package ag.act.dto.mydata;

import ag.act.parser.DateTimeParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransactionDto {

    // prodCode / currencyCode=KRW
    // transDtime 앞에 8자리만 yyyyMMdd
    private String prodName; // 종목명(상품명)
    private String prodCode; // 종목코드(상품코드)
    private String transDtime; // 거래일시 또는 거래일자: 거래 또는 거래취소가 발생한 기준일(시) 시각 정보가 없는 경우 거래일자인 YYYYMMDD 회신 (일부 기관의 경우 거래일자+거래번호로 관리)
    private String transNo; // 거래번호

    @Builder.Default
    private boolean isReverse = false;// 거래내역을 0 원부터 시작해서 역으로 추적해야 하는 케이스

    /**
     * 거래종류(코드).
     *
     * @see TransactionType
     */
    private String transType;

    /**
     * 거래종류 상세.
     *
     * @see TransactionType
     */
    private String transTypeDetail;
    private String transNum; // 거래수량
    private String baseAmt; // 거래단가
    private String transAmt; // 거래금액
    private String settleAmt; // 정산금액
    private String balanceAmt; // 거래후잔액
    private String currencyCode; // 통화코드 KRW
    private String exCode; // 해외주식 거래소 코드

    public String getTransactionDate() {
        if (StringUtils.isBlank(transDtime)) {
            return "";
        }

        // TODO 날짜가 8자리도 있고, 14자리도 있는데, 14자리는 어떻게 처리할지 고민해봐야 함.
        //      특히 같은 날일때는 뒤에 시분초까지 고려해야 정확한 데이터를 뽑을 수 있지 않을까?
        final String trimmed = transDtime.trim();
        if (trimmed.length() < 8) {
            return trimmed;
        }
        return trimmed.substring(0, 8);
    }

    public LocalDateTime getTransactionLocalDateTime() {
        return DateTimeParser.parseDate(getTransactionDate());
    }

    public TransactionType getTransactionType() {
        return TransactionType.fromValue(transType);
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(prodName)
            && StringUtils.isNotBlank(prodCode)
            // && StringUtils.isBlank(exCode) // 현재 보유하지 않은 종목의 거래내역은 exCode에 001 같은 값이 들어 있다.
            && StringUtils.isNotBlank(transDtime)
            && transDtime.trim().length() >= 8
            && TransactionType.getStockTransactions().contains(getTransactionType());
    }
}
