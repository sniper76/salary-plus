package ag.act.converter.stock;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.dto.user.SolidarityLeaderUserDto;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import ag.act.model.SolidarityLeaderResponse;
import ag.act.service.admin.CorporateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderResponseConverter {
    private final DecryptColumnConverter decryptColumnConverter;
    private final CorporateUserService corporateUserService;

    public SolidarityLeaderResponse convert(SolidarityLeaderUserDto solidarityLeaderUserDto) {

        if (!isValid(solidarityLeaderUserDto)) {
            return null;
        }

        final SolidarityLeader solidarityLeader = solidarityLeaderUserDto.solidarityLeader();
        final User user = solidarityLeaderUserDto.user();
        final String corporateNo = corporateUserService.getNullableCorporateNoByUserId(user.getId());

        return new SolidarityLeaderResponse()
            .userId(user.getId())
            .name(user.getName())
            .birthDate(DateTimeConverter.convert(user.getBirthDate()))
            .email(user.getEmail())
            .status(user.getStatus())
            .nickname(user.getNickname())
            .phoneNumber(decryptColumnConverter.convert(user.getHashedPhoneNumber()))
            .solidarityId(solidarityLeader.getSolidarity().getId())
            .solidarityLeaderId(solidarityLeader.getId())
            .corporateNo(corporateNo)
            .message(solidarityLeader.getMessage());
    }

    private boolean isValid(SolidarityLeaderUserDto solidarityLeaderUserDto) {
        return solidarityLeaderUserDto != null
            && solidarityLeaderUserDto.user() != null
            && solidarityLeaderUserDto.solidarityLeader() != null;
    }
}
