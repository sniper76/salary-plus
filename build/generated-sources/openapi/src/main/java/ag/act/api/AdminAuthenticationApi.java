/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.AuthUserResponse;
import ag.act.model.ChangePasswordRequest;
import ag.act.model.CmsLoginRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.UserDataResponse;
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
public interface AdminAuthenticationApi {

    default AdminAuthenticationApiDelegate getDelegate() {
        return new AdminAuthenticationApiDelegate() {};
    }

    /**
     * POST /api/admin/auth/change-password : Admin Change Password
     *
     * @param changePasswordRequest  (required)
     * @return Logged in successfully (status code 200)
     *         or Bad Request. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/auth/change-password",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<UserDataResponse> changeMyPassword(
         @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        return getDelegate().changeMyPassword(changePasswordRequest);
    }


    /**
     * POST /api/admin/auth/login : Admin Login
     *
     * @param cmsLoginRequest  (required)
     * @return Logged in successfully (status code 200)
     *         or Bad Request. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/auth/login",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<AuthUserResponse> loginAdmin(
         @Valid @RequestBody CmsLoginRequest cmsLoginRequest
    ) {
        return getDelegate().loginAdmin(cmsLoginRequest);
    }

}
