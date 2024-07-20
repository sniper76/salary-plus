package ag.act.module.okcert;

import ag.act.exception.InternalServerException;
import ag.act.module.okcert.converter.OkCertRequestConverter;
import ag.act.module.okcert.converter.OkCertResponseConverter;
import ag.act.module.okcert.dto.OkCertResendRequest;
import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertSendRequest;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.module.okcert.dto.OkCertVerifyRequest;
import ag.act.module.okcert.dto.OkCertVerifyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import kcb.module.v3.exception.OkCertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OkCertService {

    private static final String RESEND_Y = "Y";
    private final OkCertClient okCertClient;
    private final IOkCertResourceManager okCertResourceManager;
    private final OkCertRequestConverter okCertRequestConverter;
    private final OkCertResponseConverter okCertResponseConverter;

    public OkCertService(
        OkCertClient okCertClient,
        IOkCertResourceManager okCertResourceManager,
        OkCertRequestConverter okCertRequestConverter,
        OkCertResponseConverter okCertResponseConverter
    ) {
        this.okCertClient = okCertClient;
        this.okCertResourceManager = okCertResourceManager;
        this.okCertRequestConverter = okCertRequestConverter;
        this.okCertResponseConverter = okCertResponseConverter;
    }

    public OkCertSendResponse okCertSendRequest(OkCertSendRequest req) {
        req.setSite_name(okCertResourceManager.getSiteName());
        req.setSite_url(okCertResourceManager.getSiteUrl());

        return callOkCert(okCertResourceManager.getReqSvcName(), toRequestBody(req), OkCertSendResponse.class);
    }

    public OkCertResendResponse okCertResendRequest(OkCertResendRequest req) {
        req.setSms_resend_yn(RESEND_Y);

        return callOkCert(okCertResourceManager.getReqSvcName(), toRequestBody(req), OkCertResendResponse.class);
    }

    public OkCertVerifyResponse okCertVerifyAuth(OkCertVerifyRequest req) {
        return callOkCert(okCertResourceManager.getResSvcName(), toRequestBody(req), OkCertVerifyResponse.class);
    }

    private <T> T callOkCert(String serviceName, String requestBody, Class<T> responseClass) {
        try {
            final String response = okCertClient.callOkCert(
                okCertResourceManager.getTarget(),
                okCertResourceManager.getCpCd(),
                serviceName,
                okCertResourceManager.getOkCertLicencePath(),
                requestBody
            );
            return okCertResponseConverter.convert(response, responseClass);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OkCert Response: {}", e.getMessage(), e);
            throw new InternalServerException("휴대폰 인증서버 응답을 처리하는 중에 오류가 발생하였습니다.", e);
        } catch (ReflectiveOperationException e) {
            log.error("Failed to map OkCert Response to dto: {}", e.getMessage(), e);
            throw new InternalServerException("휴대폰 인증서버 응답을 처리하는 중에 오류가 발생하였습니다.", e);
        } catch (OkCertException e) {
            log.error("Failed to call OkCert Server: {}", e.getMessage(), e);
            throw new InternalServerException("휴대폰 인증서버 호출 중에 오류가 발생하였습니다.", e);
        } catch (Exception e) {
            log.error("Failed to call OkCert Server: {}", e.getMessage(), e);
            throw new InternalServerException("휴대폰 인증서버 호출 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private <T> String toRequestBody(T dto) {
        return okCertRequestConverter.convert(dto);
    }

}
