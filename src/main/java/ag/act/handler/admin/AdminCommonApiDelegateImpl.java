package ag.act.handler.admin;

import ag.act.api.AdminCommonApiDelegate;
import ag.act.converter.SimpleBoardGroupResponseConverter;
import ag.act.enums.BoardGroup;
import ag.act.facade.stock.StockFacade;
import ag.act.facade.stock.group.StockGroupFacade;
import ag.act.model.CmsCommonsDataResponse;
import ag.act.model.CmsCommonsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminCommonApiDelegateImpl implements AdminCommonApiDelegate {

    private final StockFacade stockFacade;
    private final StockGroupFacade stockGroupFacade;
    private final SimpleBoardGroupResponseConverter simpleStockResponseConverter;

    @Override
    public ResponseEntity<CmsCommonsDataResponse> getCmsCommons() {
        final CmsCommonsResponse cmsCommonsResponse = new CmsCommonsResponse()
            .stockGroups(stockGroupFacade.getSimpleStockGroups())
            .stocks(stockFacade.getSimpleStocks())
            .boardGroups(simpleStockResponseConverter.convertList(
                List.of(BoardGroup.values()))
            );

        return ResponseEntity.ok(new CmsCommonsDataResponse().data(cmsCommonsResponse));
    }
}
