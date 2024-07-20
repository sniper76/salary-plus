package ag.act.handler.admin.stockgroup;

import ag.act.api.AdminStockGroupApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.stock.GetStockGroupsSearchDto;
import ag.act.facade.stock.group.StockGroupFacade;
import ag.act.model.CreateStockGroupRequest;
import ag.act.model.GetStockGroupDetailsDataResponse;
import ag.act.model.GetStockGroupsResponse;
import ag.act.model.StockGroupDataArrayResponse;
import ag.act.model.StockGroupDataResponse;
import ag.act.model.UpdateStockGroupRequest;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
public class AdminStockGroupApiDelegateImpl implements AdminStockGroupApiDelegate {

    private final PageDataConverter pageDataConverter;
    private final StockGroupFacade stockGroupFacade;

    @Override
    public ResponseEntity<GetStockGroupsResponse> getStockGroups(Long stockGroupId, Integer page, Integer size, List<String> sorts) {

        final GetStockGroupsSearchDto getStockGroupSearchDto = GetStockGroupsSearchDto.builder()
            .stockGroupId(stockGroupId)
            .pageRequest(pageDataConverter.convert(getPageBasedOnStockGroupId(stockGroupId, page), size, sorts))
            .build();

        return ResponseEntity.ok(
            pageDataConverter.convert(
                stockGroupFacade.getStockGroups(getStockGroupSearchDto),
                ag.act.model.GetStockGroupsResponse.class
            )
        );
    }

    @Override
    public ResponseEntity<StockGroupDataResponse> createStockGroup(CreateStockGroupRequest createStockGroupRequest) {
        return ResponseEntity.ok(
            new StockGroupDataResponse().data(stockGroupFacade.createStockGroup(createStockGroupRequest))
        );
    }

    @Override
    public ResponseEntity<StockGroupDataResponse> updateStockGroup(Long stockGroupId, UpdateStockGroupRequest updateStockGroupRequest) {
        return ResponseEntity.ok(
            new StockGroupDataResponse().data(stockGroupFacade.updateStockGroup(stockGroupId, updateStockGroupRequest))
        );
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> deleteStockGroup(Long stockGroupId) {
        stockGroupFacade.deleteStockGroup(stockGroupId);

        return ResponseEntity.ok(
            SimpleStringResponseUtil.ok()
        );
    }

    @Override
    public ResponseEntity<GetStockGroupDetailsDataResponse> getStockGroupDetails(Long stockGroupId) {
        return ResponseEntity.ok(
            new GetStockGroupDetailsDataResponse().data(stockGroupFacade.getStockGroupDetails(stockGroupId))
        );
    }

    @Override
    public ResponseEntity<StockGroupDataArrayResponse> getStockGroupsAutoComplete(String searchKeyword) {
        return ResponseEntity.ok(
            new StockGroupDataArrayResponse().data(
                stockGroupFacade.getStockGroupsAutoComplete(searchKeyword)
            )
        );
    }

    private Integer getPageBasedOnStockGroupId(Long stockGroupId, Integer page) {
        if (stockGroupId != null) {
            return 1; // 검색어가 있을 경우 1페이지로 고정
        }
        return page;
    }
}
