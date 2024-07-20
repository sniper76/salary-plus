package ag.act.converter;

import ag.act.entity.CorporateUser;
import ag.act.model.CorporateUserDetailsResponse;
import ag.act.service.user.UserHoldingStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CorporateUserConverter implements Converter<CorporateUser, CorporateUserDetailsResponse> {
    private final UserHoldingStockService userHoldingStockService;

    public CorporateUserDetailsResponse convert(CorporateUser corporateUser) {
        return new CorporateUserDetailsResponse()
            .id(corporateUser.getId())
            .userId(corporateUser.getUserId())
            .corporateNo(corporateUser.getCorporateNo())
            .corporateName(corporateUser.getCorporateName())
            .status(corporateUser.getStatus())
            .leadingSolidarityStockCodes(getLeadingSolidarityStockCodes(corporateUser.getUserId()))
            .createdAt(DateTimeConverter.convert(corporateUser.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(corporateUser.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(corporateUser.getDeletedAt()));
    }

    @Override
    public CorporateUserDetailsResponse apply(CorporateUser corporateUser) {
        return convert(corporateUser);
    }

    private List<String> getLeadingSolidarityStockCodes(Long userId) {
        return userHoldingStockService.getLeadingSolidarityStockCodes(userId);
    }
}
