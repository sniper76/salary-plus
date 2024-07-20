package ag.act.service;

import ag.act.dto.MySolidarityDto;
import ag.act.dto.mysolidarity.InProgressActionUserStatus;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static ag.act.dto.mysolidarity.InProgressActionUserStatus.ALL_COMPLETED;
import static ag.act.dto.mysolidarity.InProgressActionUserStatus.AT_LEAST_ONE_REMAINED;
import static ag.act.dto.mysolidarity.InProgressActionUserStatus.NONE;
import static ag.act.enums.DigitalDocumentAnswerStatus.COMPLETE;

@Service
public class ActionLinkService {
    public List<MySolidarityDto> determineMySolidaritiesActionStatus(
        List<MySolidarityDto> mySolidarities,
        Set<String> inProgressDigitalProxiesStockCodes,
        Map<String, List<DigitalDocumentUser>> inProgressStockCodeDigitalDocumentsUsersMap
    ) {
        return mySolidarities.stream()
            .peek(dto -> dto.setInProgressActionUserStatus(
                    determineInProgressActionUserStatus(
                        dto.getStock().getCode(),
                        inProgressDigitalProxiesStockCodes,
                        inProgressStockCodeDigitalDocumentsUsersMap
                    )
                )
            )
            .toList();
    }

    private InProgressActionUserStatus determineInProgressActionUserStatus(
        String stockCode,
        Set<String> inProgressDigitalProxiesStockCodes,
        Map<String, List<DigitalDocumentUser>> inProgressStockCodeDigitalDocumentsUsersMap
    ) {
        if (inProgressDigitalProxiesStockCodes.contains(stockCode)) {
            return AT_LEAST_ONE_REMAINED;
        }

        final List<DigitalDocumentUser> digitalDocumentUsers = inProgressStockCodeDigitalDocumentsUsersMap.get(stockCode);
        if (CollectionUtils.isEmpty(digitalDocumentUsers)) {
            return NONE;
        }

        final boolean isAnyNotComplete = digitalDocumentUsers.stream()
            .anyMatch(this::isNotComplete);

        return isAnyNotComplete ? AT_LEAST_ONE_REMAINED : ALL_COMPLETED;
    }

    private boolean isNotComplete(DigitalDocumentUser digitalDocumentUser) {
        return digitalDocumentUser == null || digitalDocumentUser.getDigitalDocumentAnswerStatus() != COMPLETE;
    }
}
