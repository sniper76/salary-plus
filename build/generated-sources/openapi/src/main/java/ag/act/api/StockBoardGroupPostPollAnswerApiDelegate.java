package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.PollAnswerDataArrayResponse;
import ag.act.model.PostPollAnswerRequest;
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
 * A delegate to be called by the {@link StockBoardGroupPostPollAnswerApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockBoardGroupPostPollAnswerApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/polls/{pollId}/answers : 게시글 설문 답변하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param pollId Poll ID parameter (required)
     * @param postPollAnswerRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see StockBoardGroupPostPollAnswerApi#createBoardGroupPostPollAnswer
     */
    default ResponseEntity<PollAnswerDataArrayResponse> createBoardGroupPostPollAnswer(String stockCode,
        String boardGroupName,
        Long postId,
        Long pollId,
        PostPollAnswerRequest postPollAnswerRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/polls/{pollId}/answers : 게시글 설문 선택한 답변 수정하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param pollId Poll ID parameter (required)
     * @param postPollAnswerRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see StockBoardGroupPostPollAnswerApi#updateBoardGroupPostPollAnswer
     */
    default ResponseEntity<PollAnswerDataArrayResponse> updateBoardGroupPostPollAnswer(String stockCode,
        String boardGroupName,
        Long postId,
        Long pollId,
        PostPollAnswerRequest postPollAnswerRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
