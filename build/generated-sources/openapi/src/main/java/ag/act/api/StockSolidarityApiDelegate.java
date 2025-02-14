package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityDataResponse;
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
 * A delegate to be called by the {@link StockSolidarityApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockSolidarityApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/solidarity/apply-leader : 주주대표 지원하기
     *
     * @param stockCode Stock code parameter (required)
     * @return Successful (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see StockSolidarityApi#applySolidarityLeader
     */
    default ResponseEntity<SimpleStringResponse> applySolidarityLeader(String stockCode) {
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
     * DELETE /api/stocks/{stockCode}/solidarity/apply-leader : 주주대표 지원 취소하기
     *
     * @param stockCode Stock code parameter (required)
     * @return Successful (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see StockSolidarityApi#cancelSolidarityLeaderApplication
     */
    default ResponseEntity<SimpleStringResponse> cancelSolidarityLeaderApplication(String stockCode) {
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
     * GET /api/stocks/{stockCode}/solidarity : 연대 조회하기
     *
     * @param stockCode Stock code parameter (required)
     * @return Successful (status code 200)
     * @see StockSolidarityApi#getSolidarity
     */
    default ResponseEntity<SolidarityDataResponse> getSolidarity(String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"stake\" : 1.4658129, \"requiredMemberCount\" : 5, \"code\" : \"code\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"minThresholdMemberCount\" : 33861, \"memberCount\" : 6, \"name\" : \"name\", \"id\" : 0 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
