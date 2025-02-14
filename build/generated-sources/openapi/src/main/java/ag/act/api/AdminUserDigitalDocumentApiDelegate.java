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
 * A delegate to be called by the {@link AdminUserDigitalDocumentApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminUserDigitalDocumentApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/users/{userId}/digital-document/{digitalDocumentId}/download-document : CMS 전자문서 사용자별 pdf 다운로드
     *
     * @param userId User ID parameter (required)
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @return Digital Document Files downloaded successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminUserDigitalDocumentApi#getUserDigitalDocumentPdfByAdmin
     */
    default ResponseEntity<org.springframework.core.io.Resource> getUserDigitalDocumentPdfByAdmin(Long userId,
        Long digitalDocumentId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
