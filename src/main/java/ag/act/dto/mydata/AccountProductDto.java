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
public class AccountProductDto {
    private String prodName; // 종목명
    private String baseDate; // 기준일자 yyyyMMdd
    private String prodType; // 상품종류 401(국내주식), 402(해외주식)
    private String prodCode; // 종목코드(상품코드)
    private String holdingNum; // 보유량
    private String currencyCode; // 통화코드 KRW
    private String creditType; // 신용구분 (현금 01, 신용 02, 담보대출 03)

    /**
     * 마이데이터 description.
     * 조회시점 기준 해당 계좌 보유상품별 총 매입 금액 (또는 담보대출금액, 거래금액 산정기준과 동일)
     * 체결일 기준이 아닌, 결제일 기준의 정보를 전송함에 따라, 데이터의 상세 기준(예: 수도 결제 반영 여부 등)이 정보제공자 별 로 차이가 있을 수 있음
     */
    private String purchaseAmt; // 매입금액

    public boolean isValid() {
        return MyDataProductType.isSupportedProdCode(prodType);
    }

    public StockCreditType getStockCreditType() {
        return StockCreditType.fromValue(creditType);
    }
}
