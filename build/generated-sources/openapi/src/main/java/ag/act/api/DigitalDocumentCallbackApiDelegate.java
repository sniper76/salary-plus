package ag.act.api;

import ag.act.model.DigitalDocumentZipFileCallbackRequest;
import ag.act.model.SimpleStringResponse;
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
 * A delegate to be called by the {@link DigitalDocumentCallbackApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface DigitalDocumentCallbackApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/callback/digital-document-download/{fileType} : 전자문서 ZIP 파일 생성요청
     *
     * @param xApiKey Authorization header for batch (required)
     * @param fileType DIGITAL_DOCUMENT or CAMPAIGN_DIGITAL_DOCUMENT (required)
     * @param digitalDocumentZipFileCallbackRequest  (required)
     * @return Successful response (status code 200)
     * @see DigitalDocumentCallbackApi#digitalDocumentZipFileCallback
     */
    default ResponseEntity<SimpleStringResponse> digitalDocumentZipFileCallback(String xApiKey,
        String fileType,
        DigitalDocumentZipFileCallbackRequest digitalDocumentZipFileCallbackRequest) {
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
