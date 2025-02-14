package ag.act.api;

import ag.act.model.DigitalProxySignResponse;
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
 * A delegate to be called by the {@link DigitalProxyApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface DigitalProxyApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/digital-proxy : 의결권위임하고 위임장 url 받기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see DigitalProxyApi#signAndGetEmbeddedUrl
     */
    default ResponseEntity<DigitalProxySignResponse> signAndGetEmbeddedUrl(String stockCode,
        String boardGroupName,
        Long postId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"embeddedUrl\" : \"embeddedUrl\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
