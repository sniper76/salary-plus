package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.StockHomeResponse;
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
 * A delegate to be called by the {@link StockHomeApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockHomeApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/stocks/{stockCode}/home : 종목 홈 화면 조회
     *
     * @param stockCode Stock code parameter (required)
     * @return Success (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see StockHomeApi#getStockHome
     */
    default ResponseEntity<StockHomeResponse> getStockHome(String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"stock\" : { \"code\" : \"005930\", \"name\" : \"삼성전자\" }, \"dashboard\" : { \"descriptionLabel\" : \"최종 업데이트(전일대비): 2023-08-23 21:49\", \"items\" : [ { \"title\" : \"종목명\", \"value\" : \"삼성전자\", \"variation\" : { \"text\" : \"1.23%\", \"color\" : \"#FF0000\" } }, { \"title\" : \"현재가\", \"value\" : \"80,000\", \"variation\" : { \"text\" : \"1.23%\", \"color\" : \"#FF0000\" } } ] }, \"leader\" : { \"status\" : \"ELECTED\", \"message\" : \"안녕하세요. 주주대표입니다.\", \"applied\" : true, \"solidarityId\" : 1 }, \"leaderApplication\" : { \"solidarityLeaderApplicationId\" : 1, \"applyStatus\" : \"COMPLETED\" }, \"sections\" : [ { \"type\" : \"list\", \"header\" : { \"title\" : \"최신글\", \"link\" : \"/stock/005930/news\" }, \"listItems\" : { \"title\" : \"제목\", \"boardCategory\" : { \"name\" : \"DEBATE\", \"displayName\" : \"토론방\", \"isExclusiveToHolders\" : false, \"isGroup\" : false }, \"viewCount\" : 3, \"link\" : \"/stock/005930/board/debate/posts/1\", \"likeCount\" : 10, \"reported\" : true, \"deleted\" : false, \"isNew\" : false, \"isExclusiveToHolders\" : true, \"commentCount\" : \"0,\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\" } } ], \"notices\" : [ { \"noticeLevel\" : \"INFO\", \"message\" : \"공지사항입니다.\" } ], \"leaderElectionDetail\" : { \"solidarityLeaderElectionId\" : 1, \"electionStatus\" : \"FINISHED\", \"winner\" : { \"isElectedByAdmin\" : \"관리자에 의해 선출된 주주대표인 경우 true\", \"nickname\" : \"닉네임\", \"profileImageUrl\" : \"프로필 이미지 URL\" }, \"startDate\" : \"2024-05-24T06:30:41Z\", \"endDate\" : \"2024-05-30T07:31:34Z\", \"electionProcesses\" : [ { \"title\" : \"후보자 등록 기간\" }, { \"detail\" : { \"title\" : \"지원자 현황\", \"value\" : 3, \"unit\" : \"명\", \"startDate\" : \"2024-05-24T06:30:41Z\", \"endDate\" : \"2024-05-28T07:31:34Z\" } }, { \"label\" : { \"title\" : \"진행중\", \"color\" : \"#FF0000\" } } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
