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
 * A delegate to be called by the {@link SolidarityLeaderElectionPostPollAnswerApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface SolidarityLeaderElectionPostPollAnswerApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/answers : 주주대표 선출 투표하기
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityLeaderElectionId Poll ID parameter (required)
     * @param postPollAnswerRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see SolidarityLeaderElectionPostPollAnswerApi#createElectionPostPollAnswer
     */
    default ResponseEntity<PollAnswerDataArrayResponse> createElectionPostPollAnswer(String stockCode,
        Long solidarityLeaderElectionId,
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
     * PUT /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/answers : 주주대표 선출 투표 수정하기
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityLeaderElectionId Poll ID parameter (required)
     * @param postPollAnswerRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see SolidarityLeaderElectionPostPollAnswerApi#updateElectionPostPollAnswer
     */
    default ResponseEntity<PollAnswerDataArrayResponse> updateElectionPostPollAnswer(String stockCode,
        Long solidarityLeaderElectionId,
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
