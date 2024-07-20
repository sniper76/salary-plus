package ag.act.converter.user;

import ag.act.converter.DateTimeConverter;
import ag.act.dto.user.BlockedUserDto;
import ag.act.model.BlockedUserResponse;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BlockedUserResponseConverter {

    private final SolidarityLeaderService solidarityLeaderService;

    public BlockedUserResponse convert(BlockedUserDto blockedUserDto) {
        final List<String> leadingSolidarityStockNames = solidarityLeaderService.findAllLeadingStockNames(blockedUserDto.getBlockedUserId());

        return new BlockedUserResponse()
            .id(blockedUserDto.getId())
            .blockedUserId(blockedUserDto.getBlockedUserId())
            .nickname(blockedUserDto.getNickname())
            .profileImageUrl(blockedUserDto.getProfileImageUrl())
            .isSolidarityLeader(CollectionUtils.isNotEmpty(leadingSolidarityStockNames))
            .leadingSolidarityStockNames(leadingSolidarityStockNames)
            .createdAt(DateTimeConverter.convert(blockedUserDto.getCreatedAt()));
    }
}
