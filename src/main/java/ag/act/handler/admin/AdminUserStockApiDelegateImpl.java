package ag.act.handler.admin;

import ag.act.api.AdminUserStockApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.facade.admin.stock.AdminDummyStockFacade;
import ag.act.facade.user.MyDataFacade;
import ag.act.model.AddDummyStockToUserRequest;
import ag.act.model.DeleteDummyStockFromUserRequest;
import ag.act.model.GetUserDummyStockResponse;
import ag.act.model.GetUserStockResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserDummyStockResponse;
import ag.act.model.UserStockResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminUserStockApiDelegateImpl implements AdminUserStockApiDelegate {

    private final MyDataFacade myDataFacade;
    private final PageDataConverter pageDataConverter;
    private final AdminDummyStockFacade adminDummyStockFacade;

    @Override
    public ResponseEntity<GetUserStockResponse> getUserStocks(
        Long userId,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<UserStockResponse> userStockItems = myDataFacade.getUserStocks(
            userId,
            pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(pageDataConverter.convert(userStockItems, GetUserStockResponse.class));
    }

    @Override
    public ResponseEntity<GetUserDummyStockResponse> getUserDummyStocks(
            Long userId,
            Integer page,
            Integer size,
            List<String> sorts
    ) {
        final SimplePageDto<UserDummyStockResponse> userDummyStockItems = adminDummyStockFacade.getUserDummyStocks(
                userId,
                pageDataConverter.convert(page, size, sorts)
        );

        return ResponseEntity.ok(pageDataConverter.convert(userDummyStockItems, GetUserDummyStockResponse.class));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> addDummyStockToUser(
            Long userId,
            AddDummyStockToUserRequest addDummyStockToUserRequest
    ) {
        adminDummyStockFacade.addDummyStockToUser(userId, addDummyStockToUserRequest);

        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteDummyStockFromUser(
            Long userId, DeleteDummyStockFromUserRequest deleteDummyStockFromUserRequest
    ) {
        adminDummyStockFacade.deleteDummyStockToUser(userId, deleteDummyStockFromUserRequest);

        return SimpleStringResponseUtil.okResponse();
    }
}
