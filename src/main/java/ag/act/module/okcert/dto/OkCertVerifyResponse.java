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
public class OkCertVerifyResponse implements OkCertResponse {

    // common
    private String rsltCd;
    private String rsltMsg;
    private String cpCd;
    private String txSeqNo;

    // when fail
    private String mdlTkn;

    // when success
    private String ci;
    private String di;
    private String ciUpdate;
    private String rsltName;
    private String telNo;
    private String telComCd;
    private String rsltBirthday;
    private String rsltSexCd;
    private String rsltNtvFrnrCd;
}
