package ag.act.handler.admin;

import ag.act.api.AdminCorporateUserApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.dto.admin.GetCorporateUsersSearchDto;
import ag.act.facade.admin.AdminCorporateUserFacade;
import ag.act.model.CorporateUserDataResponse;
import ag.act.model.CorporateUserRequest;
import ag.act.model.GetCorporateUserDataResponse;
import ag.act.model.SimpleStringResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminCorporateUserApiDelegateImpl implements AdminCorporateUserApiDelegate {
    private final AdminCorporateUserFacade adminCorporateUserFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<CorporateUserDataResponse> createCorporateUser(CorporateUserRequest corporateUserRequest) {
        return ResponseEntity.ok(adminCorporateUserFacade.createCorporateUser(corporateUserRequest));
    }

    @Override
    public ResponseEntity<CorporateUserDataResponse> updateCorporateUser(Long corporateId, CorporateUserRequest corporateUserRequest) {
        return ResponseEntity.ok(adminCorporateUserFacade.updateCorporateUser(corporateId, corporateUserRequest));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteCorporateUser(Long corporateId) {
        return ResponseEntity.ok(adminCorporateUserFacade.deleteCorporateUser(corporateId));
    }

    @Override
    public ResponseEntity<GetCorporateUserDataResponse> getCorporateUsers(
        String searchType, String searchKeyword, Integer page, Integer size, List<String> sorts
    ) {
        final GetCorporateUsersSearchDto getCorporateUsersSearchDto = new GetCorporateUsersSearchDto()
            .searchKeyword(searchKeyword)
            .searchType(searchType)
            .pageRequest(pageDataConverter.convert(page, size, sorts));
        return ResponseEntity.ok(adminCorporateUserFacade.getCorporateUsers(getCorporateUsersSearchDto));
    }
}
