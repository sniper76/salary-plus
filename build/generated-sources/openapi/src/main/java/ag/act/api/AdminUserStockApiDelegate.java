package ag.act.api;

import ag.act.model.AddDummyStockToUserRequest;
import ag.act.model.DeleteDummyStockFromUserRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetUserDummyStockResponse;
import ag.act.model.GetUserStockResponse;
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
 * A delegate to be called by the {@link AdminUserStockApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminUserStockApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/users/{userId}/dummy-stock : Add dummy stock to the user
     *
     * @param userId User ID parameter (required)
     * @param addDummyStockToUserRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminUserStockApi#addDummyStockToUser
     */
    default ResponseEntity<SimpleStringResponse> addDummyStockToUser(Long userId,
        AddDummyStockToUserRequest addDummyStockToUserRequest) {
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
     * DELETE /api/admin/users/{userId}/dummy-stock : Delete dummy stock From the user
     *
     * @param userId User ID parameter (required)
     * @param deleteDummyStockFromUserRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminUserStockApi#deleteDummyStockFromUser
     */
    default ResponseEntity<SimpleStringResponse> deleteDummyStockFromUser(Long userId,
        DeleteDummyStockFromUserRequest deleteDummyStockFromUserRequest) {
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
     * GET /api/admin/users/{userId}/dummy-stock : Get dummy stocks from the user
     *
     * @param userId User ID parameter (required)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminUserStockApi#getUserDummyStocks
     */
    default ResponseEntity<GetUserDummyStockResponse> getUserDummyStocks(Long userId,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"code\" : \"145020\", \"name\" : \"종목명\", \"quantity\" : \"주식수\", \"referenceDate\" : \"2023-08-10\", \"registerDate\" : \"2023-08-10\" }, { \"code\" : \"145020\", \"name\" : \"종목명\", \"quantity\" : \"주식수\", \"referenceDate\" : \"2023-08-10\", \"registerDate\" : \"2023-08-10\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/users/{userId}/stocks : CMS 사용자 상세 하단 주식 정보 리스트
     *
     * @param userId User ID parameter (required)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminUserStockApi#getUserStocks
     */
    default ResponseEntity<GetUserStockResponse> getUserStocks(Long userId,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"code\" : \"145020\", \"name\" : \"종목명\", \"quantity\" : \"주식수\", \"referenceDate\" : \"2023-08-10\", \"registerDate\" : \"2023-08-10\" }, { \"code\" : \"145020\", \"name\" : \"종목명\", \"quantity\" : \"주식수\", \"referenceDate\" : \"2023-08-10\", \"registerDate\" : \"2023-08-10\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
