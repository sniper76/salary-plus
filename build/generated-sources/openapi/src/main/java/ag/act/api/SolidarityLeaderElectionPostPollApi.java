/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.SolidarityLeaderElectionDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
@Validated
public interface SolidarityLeaderElectionPostPollApi {

    default SolidarityLeaderElectionPostPollApiDelegate getDelegate() {
        return new SolidarityLeaderElectionPostPollApiDelegate() {};
    }

    /**
     * GET /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/vote-items : 주주대표 선출 투표 현황 조회
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election Id (required)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/vote-items",
        produces = { "application/json" }
    )
    default ResponseEntity<SolidarityLeaderElectionDetailResponse> getSolidarityLeaderElectionVoteItems(
         @PathVariable("stockCode") String stockCode,
         @PathVariable("solidarityLeaderElectionId") Long solidarityLeaderElectionId
    ) {
        return getDelegate().getSolidarityLeaderElectionVoteItems(stockCode, solidarityLeaderElectionId);
    }

}
