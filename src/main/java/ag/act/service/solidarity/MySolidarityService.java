package ag.act.service.solidarity;

import ag.act.converter.stock.MySolidarityResponseConverter;
import ag.act.dto.MySolidarityDto;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.service.ActionLinkService;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.modusign.DigitalProxyModuSignService;
import ag.act.service.stock.StockCodeService;
import ag.act.service.user.UserHoldingStockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class MySolidarityService {
    private final UserHoldingStockService userHoldingStockService;
    private final MySolidarityResponseConverter mySolidarityResponseConverter;
    private final DigitalProxyModuSignService digitalProxyModuSignService;
    private final DigitalDocumentService digitalDocumentService;
    private final StockCodeService stockCodeService;
    private final ActionLinkService actionLinkService;

    public List<ag.act.model.MySolidarityResponse> getSolidarityResponsesIncludingLinks(User user) {
        final List<MySolidarityDto> mySolidarities = userHoldingStockService.getTop20SortedMySolidarityList(user.getId());
        final List<String> stockCodes = stockCodeService.getStockCodes(mySolidarities);

        final Set<String> inProgressDigitalProxiesStockCodes = digitalProxyModuSignService.getInProgressDigitalProxiesStockCodes(stockCodes);
        final Map<String, List<DigitalDocumentUser>> inProgressStockCodeDigitalDocumentsUsersMap =
            digitalDocumentService.getInProgressStockCodeDigitalDocumentsUsersMap(user.getId(), stockCodes);

        final List<MySolidarityDto> mySolidaritiesWithInProgressActionUserStatus = actionLinkService.determineMySolidaritiesActionStatus(
            mySolidarities,
            inProgressDigitalProxiesStockCodes,
            inProgressStockCodeDigitalDocumentsUsersMap
        );

        return mySolidaritiesWithInProgressActionUserStatus
            .stream()
            .map(mySolidarityResponseConverter)
            .toList();
    }
}
