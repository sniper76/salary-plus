package ag.act.converter.kcb;

import ag.act.enums.Nationality;
import ag.act.module.okcert.dto.OkCertResendRequest;
import ag.act.module.okcert.dto.OkCertSendRequest;
import ag.act.module.okcert.dto.OkCertVerifyRequest;
import org.springframework.stereotype.Component;

@Component
public class KcbOkCertRequestConverter {

    public OkCertSendRequest convertSendAuthRequest(ag.act.model.SendAuthRequest sendAuthRequestDto) {
        OkCertSendRequest dto = new OkCertSendRequest();
        dto.setTel_no(sendAuthRequestDto.getPhoneNumber());
        dto.setTel_com_cd(sendAuthRequestDto.getProvider());
        dto.setBirthday(sendAuthRequestDto.getBirthDate());
        dto.setSex_cd(sendAuthRequestDto.getGender());
        dto.setName(sendAuthRequestDto.getName());
        dto.setNtv_frnr_cd(Nationality.of(sendAuthRequestDto.getFirstNumberOfIdentification()).getType());
        dto.setRqst_caus_cd("00");
        dto.setAgree1("Y");
        dto.setAgree2("Y");
        dto.setAgree3("Y");
        dto.setAgree4("Y");
        return dto;
    }

    public OkCertVerifyRequest convertVerifyAuthRequest(ag.act.model.VerifyAuthCodeRequest verifyAuthRequestDto) {
        OkCertVerifyRequest dto = new OkCertVerifyRequest();
        dto.setTx_seq_no(verifyAuthRequestDto.getTxSeqNo());
        dto.setTel_no(verifyAuthRequestDto.getPhoneNumber());
        dto.setOtp_no(verifyAuthRequestDto.getCode());
        return dto;
    }

    public OkCertResendRequest convertResendAuthRequest(ag.act.model.ResendAuthRequest resendAuthRequestDto) {
        OkCertResendRequest dto = new OkCertResendRequest();
        dto.setTx_seq_no(resendAuthRequestDto.getTxSeqNo());
        dto.setTel_no(resendAuthRequestDto.getPhoneNumber());
        return dto;
    }
}
