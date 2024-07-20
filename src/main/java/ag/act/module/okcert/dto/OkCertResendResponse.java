package ag.act.module.okcert.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OkCertResendResponse implements OkCertResponse {
    private String rsltCd;
    private String rsltMsg;
    private String telComCd;
    private String cpCd;
    private String txSeqNo;
    private String mdlTkn;
    private String telComResCd;
    private String resendCnt;
}
