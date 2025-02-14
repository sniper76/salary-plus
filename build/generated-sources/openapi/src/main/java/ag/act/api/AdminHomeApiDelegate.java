package ag.act.api;

import ag.act.model.HomeLinkResponse;
import ag.act.model.UpdateHomeLinkRequest;
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
 * A delegate to be called by the {@link AdminHomeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminHomeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PATCH /api/admin/links/{linkType} : 메인 홈 공지사항 블로그 사이트 URL 업데이트
     *
     * @param linkType Link type NEWS or RANKING parameter (required)
     * @param updateHomeLinkRequest  (required)
     * @return Success (status code 200)
     * @see AdminHomeApi#updateHomeLink
     */
    default ResponseEntity<HomeLinkResponse> updateHomeLink(String linkType,
        UpdateHomeLinkRequest updateHomeLinkRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"id\" : \"1\", \"linkType\" : \"NOTICE or BLOG\", \"linkUrl\" : \"사이트 URL\", \"status\" : \"PROCESSING\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
