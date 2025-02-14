package ag.act.api;

import ag.act.model.CreatePushRequest;
import ag.act.model.GetPushDataResponse;
import ag.act.model.PushDetailsDataResponse;
import ag.act.model.SimpleStringResponse;


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
public class AdminPushApiController implements AdminPushApi {

    private final AdminPushApiDelegate delegate;

    public AdminPushApiController(@Autowired(required = false) AdminPushApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new AdminPushApiDelegate() {});
    }

    @Override
    public AdminPushApiDelegate getDelegate() {
        return delegate;
    }

}
