package ag.act.handler;

import ag.act.api.HomeApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.HomeFacade;
import ag.act.model.HomeLinkResponse;
import ag.act.model.HomeResponse;
import ag.act.model.MySolidarityDataArrayResponse;
import ag.act.model.UpdateMySolidarityRequest;
import ag.act.module.solidarity.MySolidarityPageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class})
public class HomeApiDelegateImpl implements HomeApiDelegate {
    private final PageDataConverter pageDataConverter;
    private final HomeFacade homeFacade;
    private final MySolidarityPageableFactory mySolidarityPageableFactory;

    @Override
    public ResponseEntity<HomeResponse> getHome() {
        return ResponseEntity.ok(homeFacade.getHome());
    }

    @Override
    public ResponseEntity<MySolidarityDataArrayResponse> getMySolidarityList(Integer page, Integer size, List<String> sorts) {
        final PageRequest pageRequest = pageDataConverter.convert(page, mySolidarityPageableFactory.getDefaultSize(size), sorts);
        return ResponseEntity.ok(pageDataConverter.convert(
            homeFacade.getMySolidarityList(pageRequest),
            MySolidarityDataArrayResponse.class
        ));
    }

    @Override
    public ResponseEntity<MySolidarityDataArrayResponse> updateMySolidarityListDisplayOrder(
        UpdateMySolidarityRequest updateMySolidarityRequest
    ) {
        List<String> stockCodes = updateMySolidarityRequest.getData();
        return ResponseEntity.ok(pageDataConverter.convert(
            homeFacade.updateMySolidarityListDisplayOrder(stockCodes, mySolidarityPageableFactory.getPageable()),
            MySolidarityDataArrayResponse.class
        ));
    }

    @Override
    public ResponseEntity<HomeLinkResponse> getHomeLink(String linkType) {
        return ResponseEntity.ok(homeFacade.getHomeLink(linkType));
    }
}
