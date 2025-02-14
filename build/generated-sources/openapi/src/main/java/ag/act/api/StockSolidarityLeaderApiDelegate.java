package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateSolidarityLeaderMessageRequest;
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
 * A delegate to be called by the {@link StockSolidarityLeaderApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockSolidarityLeaderApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PATCH /api/stocks/{stockCode}/solidarity/{solidarityId}/leader/message : 주주대표 한마디 수정하기
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityId Solidarity ID parameter (required)
     * @param updateSolidarityLeaderMessageRequest  (required)
     * @return Successful response (status code 200)
     *         or Inappropriate Message. (status code 400)
     *         or No Solidarity Leader Authority (status code 401)
     *         or No Solidarity Leader Found (status code 404)
     * @see StockSolidarityLeaderApi#updateSolidarityLeaderMessage
     */
    default ResponseEntity<SimpleStringResponse> updateSolidarityLeaderMessage(String stockCode,
        Long solidarityId,
        UpdateSolidarityLeaderMessageRequest updateSolidarityLeaderMessageRequest) {
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
