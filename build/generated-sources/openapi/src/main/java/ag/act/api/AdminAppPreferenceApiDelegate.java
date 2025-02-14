package ag.act.api;

import ag.act.model.AppPreferenceDataResponse;
import ag.act.model.AppPreferenceResponse;
import ag.act.model.AppPreferenceUpdateRequest;
import ag.act.model.ErrorResponse;
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
 * A delegate to be called by the {@link AdminAppPreferenceApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminAppPreferenceApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/app-preferences/{appPreferenceId} : CMS에서 APP Preference 단건 조회
     *
     * @param appPreferenceId  (required)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     * @see AdminAppPreferenceApi#getAppPreferenceDetails
     */
    default ResponseEntity<AppPreferenceResponse> getAppPreferenceDetails(Long appPreferenceId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"updatedBy\" : 1, \"createdBy\" : 6, \"id\" : 0, \"value\" : \"value\", \"appPreferenceType\" : \"appPreferenceType\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/app-preferences : CMS에서 App Preference 조회
     *
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     * @see AdminAppPreferenceApi#getAppPreferences
     */
    default ResponseEntity<AppPreferenceDataResponse> getAppPreferences(Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"updatedBy\" : 1, \"createdBy\" : 6, \"id\" : 0, \"value\" : \"value\", \"appPreferenceType\" : \"appPreferenceType\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"updatedBy\" : 1, \"createdBy\" : 6, \"id\" : 0, \"value\" : \"value\", \"appPreferenceType\" : \"appPreferenceType\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/admin/app-preferences/{appPreferenceId} : App Preference 수정
     *
     * @param appPreferenceId  (required)
     * @param appPreferenceUpdateRequest  (required)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     * @see AdminAppPreferenceApi#updateAppPreference
     */
    default ResponseEntity<AppPreferenceResponse> updateAppPreference(Long appPreferenceId,
        AppPreferenceUpdateRequest appPreferenceUpdateRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"updatedBy\" : 1, \"createdBy\" : 6, \"id\" : 0, \"value\" : \"value\", \"appPreferenceType\" : \"appPreferenceType\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
