package ag.act.api;

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
 * A delegate to be called by the {@link AdminUserDownloadDocumentApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminUserDownloadDocumentApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/users/{userId}/download-document/solidarity-leader-confidential-agreement : CMS 회원의 주주대표 비밀유지서약서 다운로드
     *
     * @param userId User ID parameter (required)
     * @return Solidarity Leader Confidential Agreement Document File downloaded successfully (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     * @see AdminUserDownloadDocumentApi#getSolidarityLeaderConfidentialAgreementDocumentByAdmin
     */
    default ResponseEntity<org.springframework.core.io.Resource> getSolidarityLeaderConfidentialAgreementDocumentByAdmin(Long userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
