package ag.act.api;

import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CreateCommentRequest;
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
 * A delegate to be called by the {@link StockBoardGroupPostCommentReplyApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockBoardGroupPostCommentReplyApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies : 댓글의 답글 작성하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @param createCommentRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see StockBoardGroupPostCommentReplyApi#createBoardGroupPostCommentReply
     */
    default ResponseEntity<CommentDataResponse> createBoardGroupPostCommentReply(String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        CreateCommentRequest createCommentRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"replyCommentCount\" : 5, \"likeCount\" : 1, \"userId\" : 6, \"userProfile\" : { \"leadingStocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"deleted\" : true, \"hasStocks\" : true, \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"reported\" : true, \"userIp\" : \"userIp\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, \"content\" : \"content\", \"liked\" : true, \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deleted\" : true, \"reported\" : true, \"editedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 0, \"replyComments\" : [ null, null ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies : 댓글의 답글 목록 조회
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see StockBoardGroupPostCommentReplyApi#getBoardGroupPostCommentsReplies
     */
    default ResponseEntity<CommentPagingResponse> getBoardGroupPostCommentsReplies(String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"replyCommentCount\" : 5, \"likeCount\" : 1, \"userId\" : 6, \"userProfile\" : { \"leadingStocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"deleted\" : true, \"hasStocks\" : true, \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"reported\" : true, \"userIp\" : \"userIp\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, \"content\" : \"content\", \"liked\" : true, \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deleted\" : true, \"reported\" : true, \"editedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 0, \"replyComments\" : [ null, null ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"replyCommentCount\" : 5, \"likeCount\" : 1, \"userId\" : 6, \"userProfile\" : { \"leadingStocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"deleted\" : true, \"hasStocks\" : true, \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"reported\" : true, \"userIp\" : \"userIp\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, \"content\" : \"content\", \"liked\" : true, \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deleted\" : true, \"reported\" : true, \"editedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 0, \"replyComments\" : [ null, null ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
