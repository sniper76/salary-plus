package ag.act.api;

import ag.act.model.CreateStockAcceptorUserRequest;
import ag.act.model.DeleteStockAcceptorUserRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
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
 * A delegate to be called by the {@link AdminStockAcceptorUserApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminStockAcceptorUserApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/stocks/{code}/acceptor-users : CMS 종목 상세 수임인 선정
     *
     * @param code Stock Code (required)
     * @param createStockAcceptorUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStockAcceptorUserApi#createStockAcceptorUser
     */
    default ResponseEntity<SimpleStringResponse> createStockAcceptorUser(String code,
        CreateStockAcceptorUserRequest createStockAcceptorUserRequest) {
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

    /**
     * DELETE /api/admin/stocks/{code}/acceptor-users : CMS 종목 상세 수임인 해임
     *
     * @param code Stock Code (required)
     * @param deleteStockAcceptorUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStockAcceptorUserApi#deleteStockAcceptorUser
     */
    default ResponseEntity<SimpleStringResponse> deleteStockAcceptorUser(String code,
        DeleteStockAcceptorUserRequest deleteStockAcceptorUserRequest) {
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
