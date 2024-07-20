package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.user.BlockedUserResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.user.BlockedUserDto;
import ag.act.entity.BlockedUser;
import ag.act.entity.User;
import ag.act.enums.admin.BlockedUserFilterType;
import ag.act.exception.BadRequestException;
import ag.act.model.BlockedUserResponse;
import ag.act.repository.BlockedUserRepository;
import ag.act.validator.user.BlockedUserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static ag.act.enums.admin.BlockedUserFilterType.NORMAL_USER;
import static ag.act.enums.admin.BlockedUserFilterType.SOLIDARITY_LEADER;

@Service
@Transactional
@RequiredArgsConstructor
public class BlockedUserService {
    private final BlockedUserRepository blockedUserRepository;
    private final BlockedUserValidator blockedUserValidator;
    private final BlockedUserResponseConverter blockedUserResponseConverter;

    public List<Long> getBlockUserIdListOfMine() {
        return ActUserProvider.get()
            .map(User::getId)
            .map(blockedUserRepository::findBlockedUserIdsByUserId)
            .orElse(List.of());
    }

    public void blockUser(User user, User tartgetUser) {
        final Long userId = user.getId();
        final Long targetUserId = tartgetUser.getId();

        blockedUserValidator.validateTargetUser(tartgetUser);

        if (userId.equals(targetUserId)) {
            throw new BadRequestException("자기 자신을 차단할 수 없습니다.");
        }

        if (isBlockedUser(userId, targetUserId)) {
            throw new BadRequestException("이미 차단한 사용자입니다.");
        }

        blockedUserRepository.save(makeBlockedUser(userId, targetUserId));
    }

    public boolean isBlockedUser(Long userId, Long targetUserId) {
        return blockedUserRepository.existsByUserIdAndBlockedUserId(userId, targetUserId);
    }

    private BlockedUser makeBlockedUser(Long userId, Long targetUserId) {
        BlockedUser blockedUser = new BlockedUser();
        blockedUser.setUserId(userId);
        blockedUser.setBlockedUserId(targetUserId);

        return blockedUser;
    }

    public void unblockUser(Long userId, Long blockedUserId) {
        blockedUserRepository.deleteByUserIdAndBlockedUserId(userId, blockedUserId);
    }

    public SimplePageDto<BlockedUserResponse> getBlockedUsers(BlockedUserFilterType blockedUserFilterType, Pageable pageable) {
        final User user = ActUserProvider.getNoneNull();

        Page<BlockedUserDto> blockedUserPage = getBlockedUserPage(blockedUserFilterType, pageable, user);

        return new SimplePageDto<>(blockedUserPage.map(blockedUserResponseConverter::convert));
    }

    private Page<BlockedUserDto> getBlockedUserPage(BlockedUserFilterType blockedUserFilterType, Pageable pageable, User user) {

        if (blockedUserFilterType == SOLIDARITY_LEADER) {
            return blockedUserRepository.findBlockedUsersByUserIdAndIsSolidarityLeader(user.getId(), pageable);
        } else if (blockedUserFilterType == NORMAL_USER) {
            return blockedUserRepository.findBlockedUsersByUserIdAndIsNotSolidarityLeader(user.getId(), pageable);
        }

        return blockedUserRepository.findBlockedUsersByUserId(user.getId(), pageable);
    }
}
