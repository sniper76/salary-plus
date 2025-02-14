package ag.act.api;

import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetCampaignsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateCampaignRequest;
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
 * A delegate to be called by the {@link AdminCampaignApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminCampaignApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/campaigns : CMS 캠페인 생성
     *
     * @param createCampaignRequest  (required)
     * @return Successful response (status code 200)
     *         or Board group of the category does not match (status code 400)
     *         or Not Found (status code 404)
     * @see AdminCampaignApi#createCampaign
     */
    default ResponseEntity<CampaignDetailsDataResponse> createCampaign(CreateCampaignRequest createCampaignRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"sourcePost\" : { \"activeStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"isPush\" : false, \"polls\" : [ { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"digitalProxy\" : { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"templateRole\" : \"templateRole\", \"templateName\" : \"templateName\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 1, \"templateId\" : \"templateId\", \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, \"likeCount\" : 1, \"poll\" : { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, \"title\" : \"title\", \"isActive\" : true, \"userProfile\" : { \"leadingStocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"deleted\" : true, \"hasStocks\" : true, \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"reported\" : true, \"userIp\" : \"userIp\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, \"content\" : \"content\", \"liked\" : true, \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"thumbnailImageUrl\" : \"thumbnailImageUrl\", \"reported\" : true, \"id\" : 1, \"viewCount\" : 5, \"stock\" : { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, \"activeEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"boardGroup\" : \"boardGroup\", \"digitalDocument\" : { \"id\" : \"digitalDocumentId\", \"answerStatus\" : \"SAVE or COMPLETE\", \"digitalDocumentType\" : \"DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT\", \"joinUserCount\" : \"전자문서 참여자수\", \"shareholdingRatio\" : \"지분율\", \"targetStartDate\" : \"전자문서 시작일자\", \"targetEndDate\" : \"전자문서 종료일자\", \"user\" : \"전자문서 위임인 정보\", \"stock\" : \"전자문서 위임인 주식정보\", \"acceptUser\" : \"전자문서 수임인 정보\", \"items\" : \"전자문서 위임장 찬성/반대/기권 기본 선택정보\", \"attachOptions\" : \"전자문서 첨부 옵션\", \"digitalDocumentDownload\" : \"전자문서 다운로드 정보\" }, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"isPinned\" : false, \"boardCategory\" : { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" }, \"isExclusiveToHolders\" : false, \"postImageList\" : [ { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" }, { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" } ], \"isNew\" : true, \"isNotification\" : true, \"userId\" : 7, \"commentCount\" : 4, \"isAuthorAdmin\" : true, \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"holderListReadAndCopyDigitalDocument\" : { \"digitalDocumentId\" : 1, \"fileName\" : \"삼성전자_3월말_주주명부열람등사청구.pdf\", \"userId\" : 1 }, \"deleted\" : true, \"editedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"boardId\" : 6 }, \"sourceStockGroupName\" : \"1\", \"id\" : 1, \"campaignPosts\" : [ { \"postId\" : 0, \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } }, { \"postId\" : 0, \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } } ], \"title\" : \"캠페인 제목\", \"sourceStockGroupId\" : 1, \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/admin/campaigns/{campaignId}/zip-file-request : 캠페인 전체 전자문서 ZIP 파일 생성요청
     *
     * @param campaignId Campaign document id parameter (required)
     * @param isSecured ZIP file is secured with password or not (optional, default to true)
     * @return Successful response (status code 200)
     * @see AdminCampaignApi#createCampaignDigitalDocumentZipFile
     */
    default ResponseEntity<SimpleStringResponse> createCampaignDigitalDocumentZipFile(Long campaignId,
        Boolean isSecured) {
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
     * DELETE /api/admin/campaigns/{campaignId} : CMS 캠페인 삭제
     *
     * @param campaignId campaign id parameter (required)
     * @return Successful response (status code 200)
     *         or Not Found. (status code 400)
     * @see AdminCampaignApi#deleteCampaign
     */
    default ResponseEntity<SimpleStringResponse> deleteCampaign(Long campaignId) {
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
     * POST /api/admin/campaigns/{campaignId}/csv-download : 캠페인 내의 전자문서 혹은 설문 응답내역 엑셀 파일 다운로드
     *
     * @param campaignId Campaign document id parameter (required)
     * @return Campaign Document Files download (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminCampaignApi#downloadCampaignUserResponseInCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadCampaignUserResponseInCsv(Long campaignId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/campaigns/{campaignId} : CMS 캠페인 상세조회
     *
     * @param campaignId campaign id parameter (required)
     * @return Successful response (status code 200)
     *         or Not Found. (status code 400)
     * @see AdminCampaignApi#getCampaignDetailsAdmin
     */
    default ResponseEntity<CampaignDetailsDataResponse> getCampaignDetailsAdmin(Long campaignId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"sourcePost\" : { \"activeStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"isPush\" : false, \"polls\" : [ { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"digitalProxy\" : { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"templateRole\" : \"templateRole\", \"templateName\" : \"templateName\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 1, \"templateId\" : \"templateId\", \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, \"likeCount\" : 1, \"poll\" : { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, \"title\" : \"title\", \"isActive\" : true, \"userProfile\" : { \"leadingStocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"deleted\" : true, \"hasStocks\" : true, \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"reported\" : true, \"userIp\" : \"userIp\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, \"content\" : \"content\", \"liked\" : true, \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"thumbnailImageUrl\" : \"thumbnailImageUrl\", \"reported\" : true, \"id\" : 1, \"viewCount\" : 5, \"stock\" : { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, \"activeEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"boardGroup\" : \"boardGroup\", \"digitalDocument\" : { \"id\" : \"digitalDocumentId\", \"answerStatus\" : \"SAVE or COMPLETE\", \"digitalDocumentType\" : \"DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT\", \"joinUserCount\" : \"전자문서 참여자수\", \"shareholdingRatio\" : \"지분율\", \"targetStartDate\" : \"전자문서 시작일자\", \"targetEndDate\" : \"전자문서 종료일자\", \"user\" : \"전자문서 위임인 정보\", \"stock\" : \"전자문서 위임인 주식정보\", \"acceptUser\" : \"전자문서 수임인 정보\", \"items\" : \"전자문서 위임장 찬성/반대/기권 기본 선택정보\", \"attachOptions\" : \"전자문서 첨부 옵션\", \"digitalDocumentDownload\" : \"전자문서 다운로드 정보\" }, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"isPinned\" : false, \"boardCategory\" : { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" }, \"isExclusiveToHolders\" : false, \"postImageList\" : [ { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" }, { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" } ], \"isNew\" : true, \"isNotification\" : true, \"userId\" : 7, \"commentCount\" : 4, \"isAuthorAdmin\" : true, \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"holderListReadAndCopyDigitalDocument\" : { \"digitalDocumentId\" : 1, \"fileName\" : \"삼성전자_3월말_주주명부열람등사청구.pdf\", \"userId\" : 1 }, \"deleted\" : true, \"editedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"boardId\" : 6 }, \"sourceStockGroupName\" : \"1\", \"id\" : 1, \"campaignPosts\" : [ { \"postId\" : 0, \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } }, { \"postId\" : 0, \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } } ], \"title\" : \"캠페인 제목\", \"sourceStockGroupId\" : 1, \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/campaigns : 캠페인 목록 조회하기
     *
     * @param searchType CampaignSearchType (TITLE, STOCK_GROUP_NAME) (optional)
     * @param searchKeyword Search keyword for title for stock (optional)
     * @param boardCategory BoardCategory action surveys and etc (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminCampaignApi#getCampaigns
     */
    default ResponseEntity<GetCampaignsDataResponse> getCampaigns(String searchType,
        String searchKeyword,
        String boardCategory,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : 1, \"title\" : \"캠페인 제목\", \"sourcePostId\" : 1, \"sourceStockGroupId\" : 1, \"isPoll\" : false, \"isDigitalDocument\" : false, \"mappedStocksCount\" : 1, \"joinUserCount\" : 1, \"joinStockCount\" : 1, \"status\" : \"ACTIVE\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"deletedAt\" : null, \"campaignDownload\" : { \"id\" : 1, \"requestUserId\" : 1, \"zipFileStatus\" : \"Zip 파일 상태\", \"zipFilePath\" : \"Zip 파일 정보\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }, { \"id\" : 1, \"title\" : \"캠페인 제목\", \"sourcePostId\" : 1, \"sourceStockGroupId\" : 1, \"isPoll\" : false, \"isDigitalDocument\" : false, \"mappedStocksCount\" : 1, \"joinUserCount\" : 1, \"joinStockCount\" : 1, \"status\" : \"ACTIVE\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"deletedAt\" : null, \"campaignDownload\" : { \"id\" : 1, \"requestUserId\" : 1, \"zipFileStatus\" : \"Zip 파일 상태\", \"zipFilePath\" : \"Zip 파일 정보\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/admin/campaigns/{campaignId} : CMS 캠페인 수정
     *
     * @param campaignId campaign id parameter (required)
     * @param updateCampaignRequest  (required)
     * @return Successful response (status code 200)
     *         or Not Found. (status code 400)
     * @see AdminCampaignApi#updateCampaign
     */
    default ResponseEntity<CampaignDetailsDataResponse> updateCampaign(Long campaignId,
        UpdateCampaignRequest updateCampaignRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"sourcePost\" : { \"activeStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"isPush\" : false, \"polls\" : [ { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"digitalProxy\" : { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"templateRole\" : \"templateRole\", \"templateName\" : \"templateName\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 1, \"templateId\" : \"templateId\", \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, \"likeCount\" : 1, \"poll\" : { \"voteType\" : \"voteType\", \"targetEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"answers\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 4, \"stockQuantity\" : 1, \"id\" : 3, \"pollItemId\" : 2, \"userId\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"postId\" : 6, \"voteTotalCount\" : 1, \"title\" : \"title\", \"content\" : \"content\", \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"targetStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"selectionOption\" : \"selectionOption\", \"id\" : 0, \"voteTotalStockSum\" : 5, \"pollItems\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"pollId\" : 2, \"id\" : 5, \"text\" : \"text\", \"voteItemStockSum\" : 9, \"voteItemCount\" : 7, \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"status\" : \"status\", \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, \"title\" : \"title\", \"isActive\" : true, \"userProfile\" : { \"leadingStocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ], \"deleted\" : true, \"hasStocks\" : true, \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"reported\" : true, \"userIp\" : \"userIp\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, \"content\" : \"content\", \"liked\" : true, \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"thumbnailImageUrl\" : \"thumbnailImageUrl\", \"reported\" : true, \"id\" : 1, \"viewCount\" : 5, \"stock\" : { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, \"activeEndDate\" : \"2000-01-23T04:56:07.000+00:00\", \"boardGroup\" : \"boardGroup\", \"digitalDocument\" : { \"id\" : \"digitalDocumentId\", \"answerStatus\" : \"SAVE or COMPLETE\", \"digitalDocumentType\" : \"DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT\", \"joinUserCount\" : \"전자문서 참여자수\", \"shareholdingRatio\" : \"지분율\", \"targetStartDate\" : \"전자문서 시작일자\", \"targetEndDate\" : \"전자문서 종료일자\", \"user\" : \"전자문서 위임인 정보\", \"stock\" : \"전자문서 위임인 주식정보\", \"acceptUser\" : \"전자문서 수임인 정보\", \"items\" : \"전자문서 위임장 찬성/반대/기권 기본 선택정보\", \"attachOptions\" : \"전자문서 첨부 옵션\", \"digitalDocumentDownload\" : \"전자문서 다운로드 정보\" }, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"isPinned\" : false, \"boardCategory\" : { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" }, \"isExclusiveToHolders\" : false, \"postImageList\" : [ { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" }, { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" } ], \"isNew\" : true, \"isNotification\" : true, \"userId\" : 7, \"commentCount\" : 4, \"isAuthorAdmin\" : true, \"deletedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"holderListReadAndCopyDigitalDocument\" : { \"digitalDocumentId\" : 1, \"fileName\" : \"삼성전자_3월말_주주명부열람등사청구.pdf\", \"userId\" : 1 }, \"deleted\" : true, \"editedAt\" : \"2000-01-23T04:56:07.000+00:00\", \"boardId\" : 6 }, \"sourceStockGroupName\" : \"1\", \"id\" : 1, \"campaignPosts\" : [ { \"postId\" : 0, \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } }, { \"postId\" : 0, \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } } ], \"title\" : \"캠페인 제목\", \"sourceStockGroupId\" : 1, \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
