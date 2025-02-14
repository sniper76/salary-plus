package ag.act.api;

import ag.act.model.CommentDataResponse;
import ag.act.model.CommentPagingResponse;
import ag.act.model.CommentUpdateStatusRequest;
import ag.act.model.CreateCommentRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.UpdateCommentRequest;
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
 * A delegate to be called by the {@link AdminBoardGroupPostCommentApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminBoardGroupPostCommentApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments : CMS 댓글 작성하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param createCommentRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see AdminBoardGroupPostCommentApi#createPostComment
     */
    default ResponseEntity<CommentDataResponse> createPostComment(String stockCode,
        String boardGroupName,
        Long postId,
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
     * POST /api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies : CMS 답글 작성하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @param createCommentRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see AdminBoardGroupPostCommentApi#createPostCommentReplies
     */
    default ResponseEntity<CommentDataResponse> createPostCommentReplies(String stockCode,
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
     * GET /api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments : CMS 공통 댓글 목록 조회하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName BoardGroup ACTION or ANALYSIS or DEBATE or GLOBALBOARD (required)
     * @param postId Post ID (required)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminBoardGroupPostCommentApi#getComments
     */
    default ResponseEntity<CommentPagingResponse> getComments(String stockCode,
        String boardGroupName,
        Long postId,
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

    /**
     * GET /api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies : CMS 공통 답글 목록 조회하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName BoardGroup ACTION or ANALYSIS or DEBATE or GLOBALBOARD (required)
     * @param postId Post ID (required)
     * @param commentId Comment ID (required)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminBoardGroupPostCommentApi#getReplies
     */
    default ResponseEntity<CommentPagingResponse> getReplies(String stockCode,
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

    /**
     * PATCH /api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId} : CMS 공통 댓글 or 답글 수정하기(어드민 사용자의 글만 가능하다)
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName BoardGroup ACTION or ANALYSIS or DEBATE or GLOBALBOARD (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @param updateCommentRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see AdminBoardGroupPostCommentApi#updatePostComment
     */
    default ResponseEntity<CommentDataResponse> updatePostComment(String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        UpdateCommentRequest updateCommentRequest) {
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
     * PATCH /api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/status : CMS 공통 댓글 or 답글 상태 변경하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName BoardGroup ACTION or ANALYSIS or DEBATE or GLOBALBOARD (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @param commentUpdateStatusRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see AdminBoardGroupPostCommentApi#updateStatusPostComment
     */
    default ResponseEntity<CommentDataResponse> updateStatusPostComment(String stockCode,
        String boardGroupName,
        Long postId,
        Long commentId,
        CommentUpdateStatusRequest commentUpdateStatusRequest) {
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

}
