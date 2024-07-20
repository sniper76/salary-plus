package ag.act.dto.mydata;

import ag.act.util.NumberUtil;
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
public class BasicInfoDto {

    private String currencyCode; // 통화코드
    private String withholdingsAmt; // 예수금
    private String creditLoanAmt; // 신용융자
    private String mortgageAmt; // 대출금

    public long getLoanPrice() {
        return NumberUtil.toLong(creditLoanAmt, 0L) + NumberUtil.toLong(mortgageAmt, 0L);
    }
}
