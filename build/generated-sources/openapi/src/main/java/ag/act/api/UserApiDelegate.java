package ag.act.api;

import ag.act.model.CreateBlockUserRequest;
import ag.act.model.CreateSolidarityLeaderConfidentialAgreementRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetAnonymousCountResponse;
import ag.act.model.GetBlockedUserResponse;
import ag.act.model.GetSolidarityLeaderConfidentialAgreementResponse;
import ag.act.model.MyStockAuthenticationResponse;
import ag.act.model.SimpleImageDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SimpleUserProfileDataResponse;
import ag.act.model.UpdateMyAddressRequest;
import ag.act.model.UpdateMyAuthTypeRequest;
import ag.act.model.UpdateMyDataRequest;
import ag.act.model.UpdateMyNicknameRequest;
import ag.act.model.UpdateMyProfileImageRequest;
import ag.act.model.UpdateMyProfileRequest;
import ag.act.model.UpdateMyPushTokenRequest;
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
 * A delegate to be called by the {@link UserApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UserApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/users/me/blocked-users : 사용자 차단하기
     *
     * @param createBlockUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Block user self Or Already blocked user (status code 400)
     *         or Not Found (status code 404)
     * @see UserApi#blockUser
     */
    default ResponseEntity<SimpleStringResponse> blockUser(CreateBlockUserRequest createBlockUserRequest) {
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
     * POST /api/users/solidarity-leader-confidential-agreement-document : 주주대표 비밀유지 서약서 제출
     *
     * @param createSolidarityLeaderConfidentialAgreementRequest  (required)
     * @return Successful response (status code 200)
     * @see UserApi#createSolidarityLeaderConfidentialAgreementDocument
     */
    default ResponseEntity<SimpleStringResponse> createSolidarityLeaderConfidentialAgreementDocument(CreateSolidarityLeaderConfidentialAgreementRequest createSolidarityLeaderConfidentialAgreementRequest) {
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
     * GET /api/users/me/blocked-users : Get users who have been blocked by a user.
     *
     * @param blockedUserType BlockedUserType - ALL(전체), NORMAL_USER(일반 사용자), SOLIDARITY_LEADER(주주대표) (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 20)
     * @param sorts Sorting criteria (optional, default to createdAt:asc)
     * @return Successful response (status code 200)
     * @see UserApi#getBlockedUsers
     */
    default ResponseEntity<GetBlockedUserResponse> getBlockedUsers(String blockedUserType,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"blockedUserId\" : 6, \"nickname\" : \"nickname\", \"id\" : 0, \"leadingSolidarityStockNames\" : [ \"leadingSolidarityStockNames\", \"leadingSolidarityStockNames\" ], \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"blockedUserId\" : 6, \"nickname\" : \"nickname\", \"id\" : 0, \"leadingSolidarityStockNames\" : [ \"leadingSolidarityStockNames\", \"leadingSolidarityStockNames\" ], \"profileImageUrl\" : \"profileImageUrl\", \"isSolidarityLeader\" : true } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/users/me : 내 정보 조회하기
     *
     * @return Success (status code 200)
     *         or Unauthorized (status code 401)
     * @see UserApi#getMe
     */
    default ResponseEntity<UserDataResponse> getMe() {
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
     * GET /api/users/me/stocks/{stockCode}/authentication : 주주인증 정보 조회하기
     *
     * @param stockCode Stock code parameter (required)
     * @return Success (status code 200)
     * @see UserApi#getMyStockAuthentication
     */
    default ResponseEntity<MyStockAuthenticationResponse> getMyStockAuthentication(String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"individualStockCountLabel\" : \"개별주식수 라벨\", \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/users/solidarity-leader-confidential-agreement-document-form : 주주대표 비밀유지 서약서 폼 조회
     *
     * @return Successful response (status code 200)
     * @see UserApi#getSolidarityLeaderConfidentialAgreementDocumentForm
     */
    default ResponseEntity<GetSolidarityLeaderConfidentialAgreementResponse> getSolidarityLeaderConfidentialAgreementDocumentForm() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"content\" : \"비밀유지 서약서 내용\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/users/anonymousCount : 사용자의 익명 작성 카운트 조회
     *
     * @return Successful response (status code 200)
     *         or Not Found (status code 404)
     * @see UserApi#getUserAnonymousCount
     */
    default ResponseEntity<GetAnonymousCountResponse> getUserAnonymousCount() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"post\" : { \"current\" : 0, \"max\" : 6 }, \"comment\" : { \"current\" : 0, \"max\" : 6 } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/users/{userId}/profile : 간단 프로필 조회
     *
     * @param userId User ID parameter (required)
     * @param stockCode Stock code filtering parameter (optional)
     * @return Successful response (status code 200)
     *         or Not Found (status code 404)
     * @see UserApi#getUserSimpleProfile
     */
    default ResponseEntity<SimpleUserProfileDataResponse> getUserSimpleProfile(Long userId,
        String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"totalAssetLabel\" : \"totalAssetLabel\", \"nickname\" : \"nickname\", \"individualStockCountLabel\" : \"individualStockCountLabel\", \"profileImageUrl\" : \"profileImageUrl\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/users/withdrawal : 회원 탈퇴 하기
     *
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#requestWithdrawal
     */
    default ResponseEntity<SimpleStringResponse> requestWithdrawal() {
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
     * DELETE /api/users/me/blocked-users/{blockedUserId} : 유저 차단 해제하기
     *
     * @param blockedUserId  (required)
     * @return Successful response (status code 200)
     *         or Unauthorized (status code 401)
     * @see UserApi#unblockUser
     */
    default ResponseEntity<SimpleStringResponse> unblockUser(Long blockedUserId) {
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
     * PATCH /api/users/my-address : 내 주소 변경하기
     *
     * @param updateMyAddressRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#updateMyAddress
     */
    default ResponseEntity<UserDataResponse> updateMyAddress(UpdateMyAddressRequest updateMyAddressRequest) {
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
     * PATCH /api/users/me/auth-type : 인증 수단 변경하기
     *
     * @param updateMyAuthTypeRequest  (required)
     * @return Successful response (status code 200)
     *         or Not Found (status code 404)
     * @see UserApi#updateMyAuthType
     */
    default ResponseEntity<SimpleStringResponse> updateMyAuthType(UpdateMyAuthTypeRequest updateMyAuthTypeRequest) {
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
     * POST /api/users/mydata : 마이데이터 파입 업로드 하기
     *
     * @param updateMyDataRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#updateMyData
     */
    default ResponseEntity<UserDataResponse> updateMyData(UpdateMyDataRequest updateMyDataRequest) {
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
     * PATCH /api/users/nickname : 닉네임 변경하기
     *
     * @param updateMyNicknameRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#updateMyNickname
     */
    default ResponseEntity<SimpleStringResponse> updateMyNickname(UpdateMyNicknameRequest updateMyNicknameRequest) {
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
     * PATCH /api/users/me : 내 프로필 변경하기
     *
     * @param updateMyProfileRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#updateMyProfile
     */
    default ResponseEntity<UserDataResponse> updateMyProfile(UpdateMyProfileRequest updateMyProfileRequest) {
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
     * PATCH /api/users/my-profile-image : 내 프로필 이미지 변경하기
     *
     * @param updateMyProfileImageRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#updateMyProfileImage
     */
    default ResponseEntity<SimpleImageDataResponse> updateMyProfileImage(UpdateMyProfileImageRequest updateMyProfileImageRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"imageId\" : 9, \"imageUrl\" : \"imageUrl\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/users/push-token : 푸시 토큰 변경하기
     *
     * @param updateMyPushTokenRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     * @see UserApi#updateMyPushToken
     */
    default ResponseEntity<SimpleStringResponse> updateMyPushToken(UpdateMyPushTokenRequest updateMyPushTokenRequest) {
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
