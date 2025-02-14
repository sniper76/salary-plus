/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

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
public interface AdminUserDigitalDocumentApi {

    default AdminUserDigitalDocumentApiDelegate getDelegate() {
        return new AdminUserDigitalDocumentApiDelegate() {};
    }

    /**
     * GET /api/admin/users/{userId}/digital-document/{digitalDocumentId}/download-document : CMS 전자문서 사용자별 pdf 다운로드
     *
     * @param userId User ID parameter (required)
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @return Digital Document Files downloaded successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/users/{userId}/digital-document/{digitalDocumentId}/download-document",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> getUserDigitalDocumentPdfByAdmin(
         @PathVariable("userId") Long userId,
         @PathVariable("digitalDocumentId") Long digitalDocumentId
    ) {
        return getDelegate().getUserDigitalDocumentPdfByAdmin(userId, digitalDocumentId);
    }

}
