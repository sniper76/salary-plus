package ag.act.service;

import ag.act.enums.ActErrorCode;
import ag.act.exception.KcbOkCertException;
import ag.act.module.okcert.OkCertService;
import ag.act.module.okcert.dto.OkCertResendRequest;
import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertResponse;
import ag.act.module.okcert.dto.OkCertResponseErrorMessage;
import ag.act.module.okcert.dto.OkCertSendRequest;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.module.okcert.dto.OkCertVerifyRequest;
import ag.act.module.okcert.dto.OkCertVerifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KcbService {
    private static final String MOBILE_NUMBER_SUCCESS_CODE = "B000";

    private final OkCertService okCertService;

    public KcbService(OkCertService okCertService) {
        this.okCertService = okCertService;
    }

    public OkCertSendResponse sendAuthRequest(OkCertSendRequest okCertSendRequest) {
        final OkCertSendResponse okCertSendResponse = okCertService.okCertSendRequest(okCertSendRequest);
        return (OkCertSendResponse) validateAndResult(okCertSendResponse, "인증번호 요청 중에 오류가 발생하였습니다.");
    }

    public OkCertResendResponse resendAuthRequest(OkCertResendRequest okCertResendRequest) {
        final OkCertResendResponse okCertResendResponse = okCertService.okCertResendRequest(okCertResendRequest);
        return (OkCertResendResponse) validateAndResult(okCertResendResponse, "인증번호 재요청 중에 오류가 발생하였습니다.");
    }

    public OkCertVerifyResponse verifyAuthCode(OkCertVerifyRequest okCertVerifyRequest) {
        final OkCertVerifyResponse okCertVerifyResponse = okCertService.okCertVerifyAuth(okCertVerifyRequest);
        return (OkCertVerifyResponse) validateAndResult(okCertVerifyResponse, "인증번호 확인 중에 오류가 발생하였습니다.");
    }

    private OkCertResponse validateAndResult(OkCertResponse responseDto, String generalErrorMessage) {
        if (isSuccess(responseDto.getRsltCd())) {
            return responseDto;
        }

        final OkCertResponseErrorMessage errorMessage = OkCertResponseErrorMessage.fromValue(responseDto.getRsltCd());

        log.warn("모바일 인증서버 응답실패 {} :: {} :: {} :: {}",
            generalErrorMessage,
            errorMessage,
            responseDto.getRsltCd(),
            responseDto.getRsltMsg()
        );
        throw new KcbOkCertException(ActErrorCode.KCB_OK_CERT_FAILED.getCode(), errorMessage.getMessage());
    }

    private boolean isSuccess(String rsltCd) {
        return MOBILE_NUMBER_SUCCESS_CODE.equals(rsltCd);
    }
}
