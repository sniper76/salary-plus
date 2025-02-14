package ag.act.api;

import ag.act.model.EditUserNicknameRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetUserResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserDataResponse;
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
 * A delegate to be called by the {@link AdminUserApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminUserApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * PATCH /api/admin/users/{userId}/nickname : edit user&#39;s nickname by admin
     *
     * @param userId User ID parameter (required)
     * @param editUserNicknameRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     * @see AdminUserApi#editUserNickname
     */
    default ResponseEntity<UserDataResponse> editUserNickname(Long userId,
        EditUserNicknameRequest editUserNicknameRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : \"1\", \"name\" : \"이동훈\", \"email\" : \"yanggun7201@gmail.com\", \"birthDate\" : \"1999-08-07T12:00:00.000Z\", \"phoneNumber\" : \"01055556666\", \"gender\" : \"M\", \"lastPinNumberVerifiedAt\" : \"2023-07-13T08:40:08.021Z\", \"status\" : \"PROCESSING\", \"authType\" : \"PIN\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"isPinNumberRegistered\" : true, \"isSolidarityLeaderConfidentialAgreementSigned\" : true, \"userBadgeVisibility\" : [ { \"label\" : \"주식수\", \"isVisible\" : true, \"name\" : \"isVisibilityStockQuantity\" }, { \"label\" : \"자산\", \"isVisible\" : true, \"name\" : \"isVisibilityTotalAsset\" } ], \"lastNicknameUpdatedAt\" : \"null || 2023-07-18T13:52:45.028685Z\", \"roles\" : [ \"USER\", \"ADMIN\", \"SUPER_ADMIN\" ], \"leadingSolidarityStockCodes\" : [ \"005930\", \"555666\", \"000660\" ], \"holdingStocks\" : [ { \"code\" : \"005930\", \"name\" : \"삼성전자\" }, { \"code\" : \"000660\", \"name\" : \"SK하이닉스\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/users/{userId} : Details of User
     *
     * @param userId User ID parameter (required)
     * @return Successful response (status code 200)
     * @see AdminUserApi#getUserDetailsAdmin
     */
    default ResponseEntity<UserDataResponse> getUserDetailsAdmin(Long userId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : \"1\", \"name\" : \"이동훈\", \"email\" : \"yanggun7201@gmail.com\", \"birthDate\" : \"1999-08-07T12:00:00.000Z\", \"phoneNumber\" : \"01055556666\", \"gender\" : \"M\", \"lastPinNumberVerifiedAt\" : \"2023-07-13T08:40:08.021Z\", \"status\" : \"PROCESSING\", \"authType\" : \"PIN\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"isPinNumberRegistered\" : true, \"isSolidarityLeaderConfidentialAgreementSigned\" : true, \"userBadgeVisibility\" : [ { \"label\" : \"주식수\", \"isVisible\" : true, \"name\" : \"isVisibilityStockQuantity\" }, { \"label\" : \"자산\", \"isVisible\" : true, \"name\" : \"isVisibilityTotalAsset\" } ], \"lastNicknameUpdatedAt\" : \"null || 2023-07-18T13:52:45.028685Z\", \"roles\" : [ \"USER\", \"ADMIN\", \"SUPER_ADMIN\" ], \"leadingSolidarityStockCodes\" : [ \"005930\", \"555666\", \"000660\" ], \"holdingStocks\" : [ { \"code\" : \"005930\", \"name\" : \"삼성전자\" }, { \"code\" : \"000660\", \"name\" : \"SK하이닉스\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/users : CMS 사용자 목록 조회하기
     *
     * @param filterType UserFilterType - ALL(전체), ADMIN(어드민), SOLIDARITY_LEADER(주주대표), ACCEPTOR_USER(수임인) (optional)
     * @param searchType UserSearchType - NAME(이름), USER_ID(사용자 아이디), NICKNAME(닉네임), PHONE_NUMBER(휴대폰번호), EMAIL(이메일) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminUserApi#getUsers
     */
    default ResponseEntity<GetUserResponse> getUsers(String filterType,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : \"1\", \"name\" : \"이동훈\", \"email\" : \"yanggun7201@gmail.com\", \"birthDate\" : \"1999-08-07T12:00:00.000Z\", \"phoneNumber\" : \"01055556666\", \"gender\" : \"M\", \"lastPinNumberVerifiedAt\" : \"2023-07-13T08:40:08.021Z\", \"status\" : \"PROCESSING\", \"authType\" : \"PIN\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"isPinNumberRegistered\" : true, \"isSolidarityLeaderConfidentialAgreementSigned\" : true, \"userBadgeVisibility\" : [ { \"label\" : \"주식수\", \"isVisible\" : true, \"name\" : \"isVisibilityStockQuantity\" }, { \"label\" : \"자산\", \"isVisible\" : true, \"name\" : \"isVisibilityTotalAsset\" } ], \"lastNicknameUpdatedAt\" : \"null || 2023-07-18T13:52:45.028685Z\", \"roles\" : [ \"USER\", \"ADMIN\", \"SUPER_ADMIN\" ], \"leadingSolidarityStockCodes\" : [ \"005930\", \"555666\", \"000660\" ], \"holdingStocks\" : [ { \"code\" : \"005930\", \"name\" : \"삼성전자\" }, { \"code\" : \"000660\", \"name\" : \"SK하이닉스\" } ] }, { \"id\" : \"1\", \"name\" : \"이동훈\", \"email\" : \"yanggun7201@gmail.com\", \"birthDate\" : \"1999-08-07T12:00:00.000Z\", \"phoneNumber\" : \"01055556666\", \"gender\" : \"M\", \"lastPinNumberVerifiedAt\" : \"2023-07-13T08:40:08.021Z\", \"status\" : \"PROCESSING\", \"authType\" : \"PIN\", \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"isPinNumberRegistered\" : true, \"isSolidarityLeaderConfidentialAgreementSigned\" : true, \"userBadgeVisibility\" : [ { \"label\" : \"주식수\", \"isVisible\" : true, \"name\" : \"isVisibilityStockQuantity\" }, { \"label\" : \"자산\", \"isVisible\" : true, \"name\" : \"isVisibilityTotalAsset\" } ], \"lastNicknameUpdatedAt\" : \"null || 2023-07-18T13:52:45.028685Z\", \"roles\" : [ \"USER\", \"ADMIN\", \"SUPER_ADMIN\" ], \"leadingSolidarityStockCodes\" : [ \"005930\", \"555666\", \"000660\" ], \"holdingStocks\" : [ { \"code\" : \"005930\", \"name\" : \"삼성전자\" }, { \"code\" : \"000660\", \"name\" : \"SK하이닉스\" } ] } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/admin/users/mydata/withdraw/{finpongAccessToken} : 마이데이터 토큰으로 마이데이터 탈퇴하기
     *
     * @param finpongAccessToken Mydata token parameter (required)
     * @return Successful response (status code 200)
     * @see AdminUserApi#withdrawMyDataWithToken
     */
    default ResponseEntity<SimpleStringResponse> withdrawMyDataWithToken(String finpongAccessToken) {
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
