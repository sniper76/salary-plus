package ag.act.dto.mydata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestInfo {
    private String orgName; // 증권사명
    private String orgCode; // 기관코드
    private String accountNum; // 증권계좌번호
    private String baseDate; // 기준일자
    private String paidInAmt; // 연금상품 납부된 총액 pensionPaidAmt
    private String withdrawalAmt; // 연금상품 출금한 총액 pensionWithdrawalAmt
    private List<AccountProductDto> acctPrdList; // 계좌상품정보목록
    private List<AccountTransactionDto> acctTranList; //계좌거래내역목록
    private List<BasicInfoDto> basicList; //기본정보목록

    public List<AccountProductDto> getAcctPrdList() {
        return emptyIfNull(acctPrdList);
    }

    public List<AccountTransactionDto> getAcctTranList() {
        return emptyIfNull(acctTranList);
    }

    public List<BasicInfoDto> getBasicList() {
        return emptyIfNull(basicList);
    }
}
