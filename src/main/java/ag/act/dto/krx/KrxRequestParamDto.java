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
public class KrxRequestParamDto {
    //    private String mktCd;//시장구분이 없다.
    private String isuCd;
    //    private String basDd;
}
