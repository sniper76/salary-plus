package ag.act.converter.kcb;

import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.util.NumberUtil;
import org.springframework.stereotype.Component;

@Component
public class KcbOkCertResponseConverter {

    public ag.act.model.SendAuthResponse convertSendAuthResponse(OkCertSendResponse authResponseDto) {
        ag.act.model.SendAuthResponse sendAuthResponse = new ag.act.model.SendAuthResponse();
        sendAuthResponse.setResultCode(authResponseDto.getRsltCd());
        sendAuthResponse.setResultMessage(authResponseDto.getRsltMsg());
        sendAuthResponse.setTxSeqNo(authResponseDto.getTxSeqNo());
        sendAuthResponse.setTelComResCd(authResponseDto.getTelComResCd());
        return sendAuthResponse;
    }

    public ag.act.model.ResendAuthResponse convertResendAuthResponse(OkCertResendResponse authResponseDto) {
        ag.act.model.ResendAuthResponse resendAuthResponseDto = new ag.act.model.ResendAuthResponse();
        resendAuthResponseDto.setResultCode(authResponseDto.getRsltCd());
        resendAuthResponseDto.setResultMessage(authResponseDto.getRsltMsg());
        resendAuthResponseDto.setResendCount(NumberUtil.toInteger(authResponseDto.getResendCnt(), 0));
        resendAuthResponseDto.setTxSeqNo(authResponseDto.getTxSeqNo());
        return resendAuthResponseDto;
    }
}

