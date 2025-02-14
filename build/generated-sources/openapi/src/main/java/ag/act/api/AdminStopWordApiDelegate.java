package ag.act.api;

import ag.act.model.CreateStopWordRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetStopWordResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StopWordDataResponse;
import ag.act.model.UpdateStopWordRequest;
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
 * A delegate to be called by the {@link AdminStopWordApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminStopWordApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/stop-words : CMS 금칙어 등록
     *
     * @param createStopWordRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Forbidden (status code 403)
     * @see AdminStopWordApi#createStopWord
     */
    default ResponseEntity<StopWordDataResponse> createStopWord(CreateStopWordRequest createStopWordRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"word\" : \"바보\", \"status\" : \"ACTIVE\", \"createdAt\" : \"2024-04-11T08:22:44.548Z\", \"updatedAt\" : \"2024-04-11T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/admin/stop-words/{stopWordId} : CMS 금칙어 삭제
     *
     * @param stopWordId ID (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStopWordApi#deleteStopWord
     */
    default ResponseEntity<SimpleStringResponse> deleteStopWord(Long stopWordId) {
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
     * GET /api/admin/stop-words : 금칙어 목록 조회하기
     *
     * @param filterType StopWordFilterType - ALL(전체), ACTIVE(활성화), INACTIVE(비활성화) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 20)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminStopWordApi#getStopWords
     */
    default ResponseEntity<GetStopWordResponse> getStopWords(String filterType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : 1, \"word\" : \"바보\", \"status\" : \"ACTIVE\", \"createdAt\" : \"2024-04-11T08:22:44.548Z\", \"updatedAt\" : \"2024-04-11T08:22:44.548Z\" }, { \"id\" : 1, \"word\" : \"바보\", \"status\" : \"ACTIVE\", \"createdAt\" : \"2024-04-11T08:22:44.548Z\", \"updatedAt\" : \"2024-04-11T08:22:44.548Z\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/admin/stop-words/{stopWordId} : CMS 금칙어 활성화 or 비활성화
     *
     * @param stopWordId ID (required)
     * @param updateStopWordRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStopWordApi#updateStopWord
     */
    default ResponseEntity<StopWordDataResponse> updateStopWord(Long stopWordId,
        UpdateStopWordRequest updateStopWordRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"word\" : \"바보\", \"status\" : \"ACTIVE\", \"createdAt\" : \"2024-04-11T08:22:44.548Z\", \"updatedAt\" : \"2024-04-11T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
