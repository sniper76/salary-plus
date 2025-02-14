package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.PostCopyRequest;
import ag.act.model.PostDetailsDataResponse;
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
 * A delegate to be called by the {@link AdminPostApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminPostApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/posts/{postId}/csv-download : 설문 게시글 설문 결과 다운로드
     *
     * @param postId Post id (required)
     * @return Poll User responses file download (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminPostApi#downloadPostPollResultsInCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPostPollResultsInCsv(Long postId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/admin/posts/{postId}/duplicate : 게시글을 그룹 카테고리에 복제한다.
     *
     * @param postId Post ID parameter (required)
     * @param postCopyRequest  (required)
     * @return Successful response (status code 200)
     *         or Board group of the category does not match (status code 400)
     *         or Not Found (status code 404)
     * @see AdminPostApi#duplicatePost
     */
    default ResponseEntity<PostDetailsDataResponse> duplicatePost(Long postId,
        PostCopyRequest postCopyRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/admin/posts/{postId}/stock-groups/{stockGroupId}/duplicate : 같은 종목 그룹에 게시글을 복제한다.
     *
     * @param postId Post ID parameter (required)
     * @param stockGroupId Stock group ID parameter (required)
     * @return Successful response (status code 200)
     * @see AdminPostApi#duplicatePosts
     */
    default ResponseEntity<SimpleStringResponse> duplicatePosts(Long postId,
        Long stockGroupId) {
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
