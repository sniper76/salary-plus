package ag.act.converter.stock;

import ag.act.converter.Converter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.dto.user.SolidarityLeaderApplicantUserDto;
import ag.act.entity.User;
import ag.act.model.GetSolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.util.badge.BadgeLabelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SolidarityLeaderApplicantConverter
    implements Converter<SolidarityLeaderApplicantUserDto, SolidarityLeaderApplicantResponse> {

    private final DecryptColumnConverter decryptColumnConverter;
    private final BadgeLabelGenerator badgeLabelGenerator;

    @Override
    public SolidarityLeaderApplicantResponse apply(SolidarityLeaderApplicantUserDto solidarityLeaderApplicantUserDto) {
        return this.convert(solidarityLeaderApplicantUserDto);
    }

    public SolidarityLeaderApplicantResponse convert(SolidarityLeaderApplicantUserDto solidarityLeader) {

        if (solidarityLeader == null
            || solidarityLeader.user() == null
            || solidarityLeader.solidarityLeaderApplicant() == null
        ) {
            return null;
        }

        return new SolidarityLeaderApplicantResponse()
            .id(solidarityLeader.user().getId())
            .name(solidarityLeader.user().getName())
            .nickname(solidarityLeader.user().getNickname())
            .phoneNumber(decryptColumnConverter.convert(solidarityLeader.user().getHashedPhoneNumber()))
            .solidarityApplicantId(solidarityLeader.solidarityLeaderApplicant().getId());
    }

    public GetSolidarityLeaderApplicantResponse convert(List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants) {
        return new GetSolidarityLeaderApplicantResponse()
            .data(
                solidarityLeaderApplicants.stream()
                    .map(this::convert)
                    .toList()
            );
    }

    private SolidarityLeaderApplicantResponse convert(SolidarityLeaderApplicantDto solidarityLeaderApplicantDto) {
        final Long stockQuantity = solidarityLeaderApplicantDto.stockQuantity();

        return new SolidarityLeaderApplicantResponse()
            .solidarityApplicantId(solidarityLeaderApplicantDto.solidarityApplicantId())
            .id(solidarityLeaderApplicantDto.userId())
            .nickname(solidarityLeaderApplicantDto.nickname())
            .profileImageUrl(solidarityLeaderApplicantDto.profileImageUrl())
            .individualStockCountLabel(badgeLabelGenerator.generateStockQuantityBadge(stockQuantity))
            .commentsForStockHolder(solidarityLeaderApplicantDto.commentsForStockHolder());
    }

    public SolidarityLeaderApplicantResponse convert(User user) {
        return new SolidarityLeaderApplicantResponse()
            .id(user.getId())
            .nickname(user.getNickname());
    }
}
