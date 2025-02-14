package ag.act.api;

import ag.act.model.AuthUserResponse;
import ag.act.model.ErrorResponse;
import ag.act.model.ResendAuthRequest;
import ag.act.model.ResendAuthResponse;
import ag.act.model.SendAuthRequest;
import ag.act.model.SendAuthResponse;
import ag.act.model.VerifyAuthCodeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * A delegate to be called by the {@link SmsAuthenticationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface SmsAuthenticationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/sms/resend-auth-request : Resend Auth Request
     *
     * @param resendAuthRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     *         or Service Unavailable (status code 503)
     * @see SmsAuthenticationApi#resendAuthRequest
     */
    default ResponseEntity<ResendAuthResponse> resendAuthRequest(ResendAuthRequest resendAuthRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"resultCode\" : \"B000\", \"resultMessage\" : \"인증번호 재발송 성공\", \"resendCount\" : 2, \"txSeqNo\" : \"230712102659KC687332\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/sms/send-auth-request
     *
     * @param sendAuthRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     *         or Service Unavailable (status code 503)
     * @see SmsAuthenticationApi#sendAuthRequest
     */
    default ResponseEntity<SendAuthResponse> sendAuthRequest(SendAuthRequest sendAuthRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"telComResCd\" : \"telComResCd\", \"txSeqNo\" : \"txSeqNo\", \"resultCode\" : \"resultCode\", \"resultMessage\" : \"resultMessage\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/sms/verify-auth-code : Verify Auth Code
     *
     * @param verifyAuthCodeRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     *         or Service Unavailable (status code 503)
     * @see SmsAuthenticationApi#verifyAuthCode
     */
    default ResponseEntity<AuthUserResponse> verifyAuthCode(VerifyAuthCodeRequest verifyAuthCodeRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"user\" : { \"id\" : \"1\", \"name\" : \"이동훈\", \"email\" : \"yanggun7201@gmail.com\", \"birthDate\" : \"1999-08-07T12:00:00.000Z\", \"phoneNumber\" : \"01055556666\", \"gender\" : \"M\", \"lastPinNumberVerifiedAt\" : \"2023-07-13T08:40:08.021Z\", \"status\" : \"PROCESSING\", \"authType\" : \"PIN\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"isPinNumberRegistered\" : true, \"isSolidarityLeaderConfidentialAgreementSigned\" : true, \"userBadgeVisibility\" : [ { \"label\" : \"주식수\", \"isVisible\" : true, \"name\" : \"isVisibilityStockQuantity\" }, { \"label\" : \"자산\", \"isVisible\" : true, \"name\" : \"isVisibilityTotalAsset\" } ], \"lastNicknameUpdatedAt\" : \"null || 2023-07-18T13:52:45.028685Z\", \"roles\" : [ \"USER\", \"ADMIN\", \"SUPER_ADMIN\" ], \"leadingSolidarityStockCodes\" : [ \"005930\", \"555666\", \"000660\" ], \"holdingStocks\" : [ { \"code\" : \"005930\", \"name\" : \"삼성전자\" }, { \"code\" : \"000660\", \"name\" : \"SK하이닉스\" } ] }, \"token\" : { \"accessToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjg5MTMyNzIwLCJleHAiOjE2ODk3Mzc1MjB9.Wo3iBvhSyf3ujtQiqA9BPyZ54tQuKL_bNRAN6qS3Ih8\", \"finpongAccessToken\" : \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjg5MTMyNzIwLCJleHAiOjE2ODk3Mzc1MjB9.Wo3iBvhSyf3ujtQiqA9BPyZ54tQuKL_bNRAN6qS3Ih8\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
