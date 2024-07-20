package ag.act.module.okcert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("checkstyle:MemberName")
public class OkCertVerifyRequest {
    private String tx_seq_no = "";
    private String tel_no = "";
    private String otp_no = "";
    private String sms_resend_yn = "n";
}
