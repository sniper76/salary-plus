/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.AppPreferenceDataResponse;
import ag.act.model.AppPreferenceResponse;
import ag.act.model.AppPreferenceUpdateRequest;
import ag.act.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
@Validated
public interface AdminAppPreferenceApi {

    default AdminAppPreferenceApiDelegate getDelegate() {
        return new AdminAppPreferenceApiDelegate() {};
    }

    /**
     * GET /api/admin/app-preferences/{appPreferenceId} : CMS에서 APP Preference 단건 조회
     *
     * @param appPreferenceId  (required)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/app-preferences/{appPreferenceId}",
        produces = { "application/json" }
    )
    default ResponseEntity<AppPreferenceResponse> getAppPreferenceDetails(
         @PathVariable("appPreferenceId") Long appPreferenceId
    ) {
        return getDelegate().getAppPreferenceDetails(appPreferenceId);
    }


    /**
     * GET /api/admin/app-preferences : CMS에서 App Preference 조회
     *
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/app-preferences",
        produces = { "application/json" }
    )
    default ResponseEntity<AppPreferenceDataResponse> getAppPreferences(
         @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
         @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
         @Valid @RequestParam(value = "sorts", required = false, defaultValue = "createdAt:desc") List<String> sorts
    ) {
        return getDelegate().getAppPreferences(page, size, sorts);
    }


    /**
     * PATCH /api/admin/app-preferences/{appPreferenceId} : App Preference 수정
     *
     * @param appPreferenceId  (required)
     * @param appPreferenceUpdateRequest  (required)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.PATCH,
        value = "/api/admin/app-preferences/{appPreferenceId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<AppPreferenceResponse> updateAppPreference(
         @PathVariable("appPreferenceId") Long appPreferenceId,
         @Valid @RequestBody AppPreferenceUpdateRequest appPreferenceUpdateRequest
    ) {
        return getDelegate().updateAppPreference(appPreferenceId, appPreferenceUpdateRequest);
    }

}
