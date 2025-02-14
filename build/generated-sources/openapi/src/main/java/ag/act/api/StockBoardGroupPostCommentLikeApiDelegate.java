package ag.act.api;

import ag.act.model.ErrorResponse;
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
 * A delegate to be called by the {@link StockBoardGroupPostCommentLikeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockBoardGroupPostCommentLikeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/likes : 댓글 or 답글 좋아요
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see StockBoardGroupPostCommentLikeApi#likeBoardGroupPostComment
     */
    default ResponseEntity<SimpleStringResponse> likeBoardGroupPostComment(String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId) {
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

    /**
     * DELETE /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/likes : 댓글 or 답글 좋아요 취소
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see StockBoardGroupPostCommentLikeApi#unlikeBoardGroupPostComment
     */
    default ResponseEntity<SimpleStringResponse> unlikeBoardGroupPostComment(String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId) {
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
