package ag.act.api;

import ag.act.model.CreateStockGroupRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetStockGroupDetailsDataResponse;
import ag.act.model.GetStockGroupsResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockGroupDataArrayResponse;
import ag.act.model.StockGroupDataResponse;
import ag.act.model.UpdateStockGroupRequest;
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
 * A delegate to be called by the {@link AdminStockGroupApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminStockGroupApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/stock-groups : CMS 종목그룹 등록 하기
     *
     * @param createStockGroupRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminStockGroupApi#createStockGroup
     */
    default ResponseEntity<StockGroupDataResponse> createStockGroup(CreateStockGroupRequest createStockGroupRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stockCount\" : 6, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/admin/stock-groups/{stockGroupId} : CMS 종목그룹 삭제하기
     *
     * @param stockGroupId Stock group ID (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminStockGroupApi#deleteStockGroup
     */
    default ResponseEntity<SimpleStringResponse> deleteStockGroup(Long stockGroupId) {
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
     * GET /api/admin/stock-groups/{stockGroupId} : CMS 종목그룹 상세 조회하기
     *
     * @param stockGroupId Stock group ID (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminStockGroupApi#getStockGroupDetails
     */
    default ResponseEntity<GetStockGroupDetailsDataResponse> getStockGroupDetails(Long stockGroupId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stock-groups : CMS 종목그룹 목록 조회하기
     *
     * @param stockGroupId Stock group ID (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStockGroupApi#getStockGroups
     */
    default ResponseEntity<GetStockGroupsResponse> getStockGroups(Long stockGroupId,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stockCount\" : 6, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stockCount\" : 6, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stock-groups/auto-complete : CMS 종목그룹 자동 완성
     *
     * @param searchKeyword Search keyword (optional)
     * @return Success (status code 200)
     * @deprecated
     * @see AdminStockGroupApi#getStockGroupsAutoComplete
     */
    @Deprecated
    default ResponseEntity<StockGroupDataArrayResponse> getStockGroupsAutoComplete(String searchKeyword) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stockCount\" : 6, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stockCount\" : 6, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /api/admin/stock-groups/{stockGroupId} : CMS 종목그룹 수정 하기
     *
     * @param stockGroupId Stock group ID (required)
     * @param updateStockGroupRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminStockGroupApi#updateStockGroup
     */
    default ResponseEntity<StockGroupDataResponse> updateStockGroup(Long stockGroupId,
        UpdateStockGroupRequest updateStockGroupRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"description\" : \"description\", \"id\" : 0, \"stockCount\" : 6, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
