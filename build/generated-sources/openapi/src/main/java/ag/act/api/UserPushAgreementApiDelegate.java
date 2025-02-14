package ag.act.api;

import java.util.List;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserPushAgreementItem;
import ag.act.model.UserPushAgreementItemsDataResponse;
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
 * A delegate to be called by the {@link UserPushAgreementApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UserPushAgreementApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/users/push-agreements : 푸시 동의 상태 조회
     *
     * @return Successful response (status code 200)
     * @see UserPushAgreementApi#getUserPushAgreements
     */
    default ResponseEntity<UserPushAgreementItemsDataResponse> getUserPushAgreements() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"itemType\" : \"ALL / SUB\", \"title\" : \"푸시 알림 받기(전체)\", \"agreementTypes\" : [ \"ACT_NOTICE\", \"ACT_NOTICE\" ], \"value\" : true }, { \"itemType\" : \"ALL / SUB\", \"title\" : \"푸시 알림 받기(전체)\", \"agreementTypes\" : [ \"ACT_NOTICE\", \"ACT_NOTICE\" ], \"value\" : true } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/users/push-agreements : 푸시 동의 상태 업데이트
     *
     * @param userPushAgreementItem  (required)
     * @return Push Agreement Updated successfully. (status code 200)
     * @see UserPushAgreementApi#updateUserPushAgreements
     */
    default ResponseEntity<SimpleStringResponse> updateUserPushAgreements(List<UserPushAgreementItem> userPushAgreementItem) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
