package ag.act.converter;

import ag.act.entity.CorporateUser;
import ag.act.model.CorporateUserDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CorporateUserDetailsResponseConverter {

    public CorporateUserDetailsResponse convert(
        CorporateUser corporateUser
    ) {
        return new CorporateUserDetailsResponse()
            .id(corporateUser.getId())
            .userId(corporateUser.getUserId())
            .corporateNo(corporateUser.getCorporateNo())
            .corporateName(corporateUser.getCorporateName())
            .status(corporateUser.getStatus())
            .createdAt(DateTimeConverter.convert(corporateUser.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(corporateUser.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(corporateUser.getDeletedAt()))
            ;
    }
}
