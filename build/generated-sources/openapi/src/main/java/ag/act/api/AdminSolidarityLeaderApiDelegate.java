package ag.act.api;

import ag.act.model.CreateSolidarityLeaderForCorporateUserRequest;
import ag.act.model.CreateSolidarityLeaderRequest;
import ag.act.model.DismissSolidarityLeaderRequest;
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
 * A delegate to be called by the {@link AdminSolidarityLeaderApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminSolidarityLeaderApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/solidarity-leaders/{solidarityId} : CMS 종목 주주대표 선정하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param createSolidarityLeaderRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminSolidarityLeaderApi#createSolidarityLeader
     */
    default ResponseEntity<SimpleStringResponse> createSolidarityLeader(Long solidarityId,
        CreateSolidarityLeaderRequest createSolidarityLeaderRequest) {
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
     * POST /api/admin/solidarity-leaders/{solidarityId}/corporate-user : CMS 법인사업자 주주대표 선정하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param createSolidarityLeaderForCorporateUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminSolidarityLeaderApi#createSolidarityLeaderForCorporateUser
     */
    default ResponseEntity<SimpleStringResponse> createSolidarityLeaderForCorporateUser(Long solidarityId,
        CreateSolidarityLeaderForCorporateUserRequest createSolidarityLeaderForCorporateUserRequest) {
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
     * DELETE /api/admin/solidarity-leaders/{solidarityId} : CMS 종목 주주대표 해임하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param dismissSolidarityLeaderRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminSolidarityLeaderApi#dismissSolidarityLeader
     */
    default ResponseEntity<SimpleStringResponse> dismissSolidarityLeader(Long solidarityId,
        DismissSolidarityLeaderRequest dismissSolidarityLeaderRequest) {
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
     * PATCH /api/admin/solidarity-leaders/{solidarityId}/message : CMS 주주대표 한마디 수정하기
     *
     * @param solidarityId Solidarity ID parameter (required)
     * @param updateSolidarityLeaderMessageRequest  (required)
     * @return Successful response (status code 200)
     *         or Inappropriate Message. (status code 400)
     *         or No Solidarity Leader Found (status code 404)
     * @see AdminSolidarityLeaderApi#updateSolidarityLeaderMessageByAdmin
     */
    default ResponseEntity<SimpleStringResponse> updateSolidarityLeaderMessageByAdmin(Long solidarityId,
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
