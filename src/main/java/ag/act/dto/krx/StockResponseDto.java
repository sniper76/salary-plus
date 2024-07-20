package ag.act.dto.krx;

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
public class StockResponseDto {
    private String isuCd;//표준코드
    private String isuNm;//한글 종목명
    //    private String mktNm;//시장구분
    private String tddClsprc;//종가
}
