/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.ImageUploadResponse;
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
public interface AdminImageUploadApi {

    default AdminImageUploadApiDelegate getDelegate() {
        return new AdminImageUploadApiDelegate() {};
    }

    /**
     * POST /api/admin/images/{fileContentType} : Upload A Image
     *
     * @param fileContentType File content type (DEFAULT / DEFAULT_PROFILE) (required)
     * @param file  (optional)
     * @param description  (optional)
     * @return Images uploaded successfully (status code 200)
     *         or Bad Request. Invalid file format or size. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/images/{fileContentType}",
        produces = { "application/json" },
        consumes = { "multipart/form-data" }
    )
    default ResponseEntity<ImageUploadResponse> uploadImageAdmin(
         @PathVariable("fileContentType") String fileContentType,
         @RequestPart(value = "file", required = false) MultipartFile file,
         @Valid @RequestParam(value = "description", required = false) String description
    ) {
        return getDelegate().uploadImageAdmin(fileContentType, file, description);
    }

}
