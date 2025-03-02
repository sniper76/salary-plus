/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.ReportContentRequest;
import ag.act.model.SimpleStringResponse;
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
public interface StockBoardGroupPostCommentReportApi {

    default StockBoardGroupPostCommentReportApiDelegate getDelegate() {
        return new StockBoardGroupPostCommentReportApiDelegate() {};
    }

    /**
     * POST /api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/reports : 댓글 or 답글 신고하기
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @param postId Post ID parameter (required)
     * @param commentId Comment ID parameter (required)
     * @param reportContentRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/reports",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> reportBoardGroupPostComment(
         @PathVariable("stockCode") String stockCode,
         @PathVariable("boardGroupName") String boardGroupName,
         @PathVariable("postId") Long postId,
         @PathVariable("commentId") Long commentId,
         @Valid @RequestBody ReportContentRequest reportContentRequest
    ) {
        return getDelegate().reportBoardGroupPostComment(stockCode, boardGroupName, postId, commentId, reportContentRequest);
    }

}
