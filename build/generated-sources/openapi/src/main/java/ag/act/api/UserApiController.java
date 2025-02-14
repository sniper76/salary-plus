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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
@Controller
@RequestMapping("${openapi.salaryPlusAPIs.base-path:}")
public class UserApiController implements UserApi {

    private final UserApiDelegate delegate;

    public UserApiController(@Autowired(required = false) UserApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new UserApiDelegate() {});
    }

    @Override
    public UserApiDelegate getDelegate() {
        return delegate;
    }

}
