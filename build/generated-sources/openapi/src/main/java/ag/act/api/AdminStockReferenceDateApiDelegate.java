package ag.act.api;

import ag.act.model.CreateStockReferenceDateRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockReferenceDateDataArrayResponse;
import ag.act.model.StockReferenceDateDataResponse;
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
 * A delegate to be called by the {@link AdminStockReferenceDateApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminStockReferenceDateApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/stocks/{stockCode}/reference-dates : 종목 기준일 등록 생성하기
     *
     * @param stockCode Stock code parameter (required)
     * @param createStockReferenceDateRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminStockReferenceDateApi#createStockReferenceDate
     */
    default ResponseEntity<StockReferenceDateDataResponse> createStockReferenceDate(String stockCode,
        CreateStockReferenceDateRequest createStockReferenceDateRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"stockCode\" : \"145020\", \"referenceDate\" : \"2023-08-10\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/admin/stocks/{stockCode}/reference-dates/{referenceDateId} : 종목 기준일 삭제하기
     *
     * @param stockCode Stock code parameter (required)
     * @param referenceDateId Stock&#39;s reference Date Id (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminStockReferenceDateApi#deleteStockReferenceDateAdmin
     */
    default ResponseEntity<SimpleStringResponse> deleteStockReferenceDateAdmin(String stockCode,
        Long referenceDateId) {
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
     * GET /api/admin/stocks/{stockCode}/reference-dates : 종목 기준일 목록 조회하기
     *
     * @param stockCode Stock code parameter (required)
     * @return Successful response (status code 200)
     * @see AdminStockReferenceDateApi#getStockReferenceDates
     */
    default ResponseEntity<StockReferenceDateDataArrayResponse> getStockReferenceDates(String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : 1, \"stockCode\" : \"145020\", \"referenceDate\" : \"2023-08-10\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" }, { \"id\" : 1, \"stockCode\" : \"145020\", \"referenceDate\" : \"2023-08-10\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/admin/stocks/{stockCode}/reference-dates/{referenceDateId} : 종목 기준일 수정하기
     *
     * @param stockCode Stock code parameter (required)
     * @param referenceDateId Stock&#39;s reference Date Id (required)
     * @param createStockReferenceDateRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminStockReferenceDateApi#updateStockReferenceDate
     */
    default ResponseEntity<StockReferenceDateDataResponse> updateStockReferenceDate(String stockCode,
        Long referenceDateId,
        CreateStockReferenceDateRequest createStockReferenceDateRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"stockCode\" : \"145020\", \"referenceDate\" : \"2023-08-10\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
