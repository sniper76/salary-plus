package ag.act.api;

import ag.act.model.SolidarityLeaderElectionDetailResponse;
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
 * A delegate to be called by the {@link SolidarityLeaderElectionPostPollApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface SolidarityLeaderElectionPostPollApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/vote-items : 주주대표 선출 투표 현황 조회
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election Id (required)
     * @return Successful response (status code 200)
     * @see SolidarityLeaderElectionPostPollApi#getSolidarityLeaderElectionVoteItems
     */
    default ResponseEntity<SolidarityLeaderElectionDetailResponse> getSolidarityLeaderElectionVoteItems(String stockCode,
        Long solidarityLeaderElectionId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"solidarityLeaderElectionId\" : 0, \"totalVoterCount\" : 6, \"isVoted\" : true, \"pollApplicants\" : [ { \"solidarityLeaderApplicantId\" : 1, \"resolutionCondition\" : { \"requiredStockQuantityRatio\" : \"requiredStockQuantityRatio\", \"unit\" : \"unit\", \"valueText\" : \"valueText\", \"color\" : \"color\", \"stockQuantity\" : 5, \"label\" : \"label\" }, \"nickname\" : \"nickname\", \"finishedEarlyCondition\" : { \"requiredStockQuantityRatio\" : \"requiredStockQuantityRatio\", \"unit\" : \"unit\", \"valueText\" : \"valueText\", \"color\" : \"color\", \"stockQuantity\" : 5, \"label\" : \"label\" }, \"totalVoteStockQuantity\" : 5, \"pollItemGroups\" : [ { \"stockQuantityPercentage\" : 3, \"stockQuantity\" : 9, \"isVoted\" : true, \"voteCount\" : 7, \"pollItemId\" : 2, \"title\" : \"title\", \"type\" : \"type\" }, { \"stockQuantityPercentage\" : 3, \"stockQuantity\" : 9, \"isVoted\" : true, \"voteCount\" : 7, \"pollItemId\" : 2, \"title\" : \"title\", \"type\" : \"type\" } ] }, { \"solidarityLeaderApplicantId\" : 1, \"resolutionCondition\" : { \"requiredStockQuantityRatio\" : \"requiredStockQuantityRatio\", \"unit\" : \"unit\", \"valueText\" : \"valueText\", \"color\" : \"color\", \"stockQuantity\" : 5, \"label\" : \"label\" }, \"nickname\" : \"nickname\", \"finishedEarlyCondition\" : { \"requiredStockQuantityRatio\" : \"requiredStockQuantityRatio\", \"unit\" : \"unit\", \"valueText\" : \"valueText\", \"color\" : \"color\", \"stockQuantity\" : 5, \"label\" : \"label\" }, \"totalVoteStockQuantity\" : 5, \"pollItemGroups\" : [ { \"stockQuantityPercentage\" : 3, \"stockQuantity\" : 9, \"isVoted\" : true, \"voteCount\" : 7, \"pollItemId\" : 2, \"title\" : \"title\", \"type\" : \"type\" }, { \"stockQuantityPercentage\" : 3, \"stockQuantity\" : 9, \"isVoted\" : true, \"voteCount\" : 7, \"pollItemId\" : 2, \"title\" : \"title\", \"type\" : \"type\" } ] } ], \"status\" : \"status\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
