package ag.act.module.okcert.dto;

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
public class OkCertSendResponse implements OkCertResponse {
    private String rsltCd;
    private String rsltMsg;
    private String telComCd;
    private String txSeqNo;
    private String mdlTkn;
    private String telComResCd;
}
