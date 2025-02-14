package ag.act.api;

import ag.act.model.CreateStockGroupRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetStockGroupDetailsDataResponse;
import ag.act.model.GetStockGroupsResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockGroupDataArrayResponse;
import ag.act.model.StockGroupDataResponse;
import ag.act.model.UpdateStockGroupRequest;


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
public class AdminStockGroupApiController implements AdminStockGroupApi {

    private final AdminStockGroupApiDelegate delegate;

    public AdminStockGroupApiController(@Autowired(required = false) AdminStockGroupApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new AdminStockGroupApiDelegate() {});
    }

    @Override
    public AdminStockGroupApiDelegate getDelegate() {
        return delegate;
    }

}
