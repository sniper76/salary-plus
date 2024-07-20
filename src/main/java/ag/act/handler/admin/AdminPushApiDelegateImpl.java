package ag.act.handler.admin;

import ag.act.api.AdminPushApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.dto.push.PushSearchDto;
import ag.act.facade.PushFacade;
import ag.act.model.CreatePushRequest;
import ag.act.model.GetPushDataResponse;
import ag.act.model.PushDetailsDataResponse;
import ag.act.model.PushDetailsResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@UseGuards(IsAdminGuard.class)
public class AdminPushApiDelegateImpl implements AdminPushApiDelegate {
    private final PushFacade pushFacade;
    private final PageDataConverter pageDataConverter;

    public AdminPushApiDelegateImpl(PushFacade pushFacade, PageDataConverter pageDataConverter) {
        this.pushFacade = pushFacade;
        this.pageDataConverter = pageDataConverter;
    }

    @Override
    public ResponseEntity<GetPushDataResponse> getAutomatedPushesAdmin(
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<PushDetailsResponse> pushListItems = pushFacade.getAutomatedPushes(
            new PushSearchDto(searchType, searchKeyword),
            pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(pageDataConverter.convert(pushListItems, GetPushDataResponse.class));
    }

    @Override
    public ResponseEntity<GetPushDataResponse> getPushesAdmin(
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<PushDetailsResponse> pushListItems = pushFacade.getPushListItems(
            new PushSearchDto(searchType, searchKeyword),
            pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(pageDataConverter.convert(pushListItems, GetPushDataResponse.class));
    }

    @Override
    public ResponseEntity<PushDetailsDataResponse> createPushAdmin(CreatePushRequest createPushRequest) {
        return ResponseEntity.ok(
            new PushDetailsDataResponse().data(pushFacade.createPush(createPushRequest))
        );
    }

    @Override
    public ResponseEntity<PushDetailsDataResponse> getPushDetailsAdmin(Long pushId) {
        return ResponseEntity.ok(
            new PushDetailsDataResponse().data(pushFacade.getPushDetails(pushId))
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deletePushAdmin(Long pushId) {
        pushFacade.deletePush(pushId);
        return SimpleStringResponseUtil.okResponse();
    }
}
