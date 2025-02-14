package ag.act.api;

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
 * A delegate to be called by the {@link AdminSolidarityApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminSolidarityApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PATCH /api/admin/solidarity/{solidarityId}/active : 주주 연대 활성화하기
     *
     * @param solidarityId Solidarity ID parameter (required)
     * @return Success (status code 200)
     * @see AdminSolidarityApi#updateSolidarityToActive
     */
    default ResponseEntity<SolidarityDataResponse> updateSolidarityToActive(Long solidarityId) {
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
