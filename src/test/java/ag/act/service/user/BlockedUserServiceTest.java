package ag.act.service.user;

import ag.act.entity.BlockedUser;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.repository.BlockedUserRepository;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.validator.user.BlockedUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static ag.act.TestUtil.assertException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings
class BlockedUserServiceTest {

    @InjectMocks
    private BlockedUserService blockedUserService;
    @Mock
    private BlockedUserRepository blockedUserRepository;
    @Mock
    private BlockedUserValidator blockedUserValidator;

    @Mock
    private User user;
    @Mock
    private User targetUser;
    private Long userId;
    private Long targetUserId;

    @BeforeEach
    void setUp() {
        userId = someLong();
        targetUserId = someLong();
    }

    @Nested
    class WhenNormal {

        @BeforeEach
        void setUp() {
            willDoNothing().given(blockedUserValidator).validateTargetUser(targetUser);
            given(user.getId()).willReturn(userId);
            given(targetUser.getId()).willReturn(targetUserId);
            given(blockedUserRepository.existsByUserIdAndBlockedUserId(userId, targetUserId)).willReturn(false);

            blockedUserService.blockUser(user, targetUser);
        }

        @Test
        void shouldCallSave() {
            then(blockedUserRepository).should().save(any(BlockedUser.class));
        }
    }

    @Nested
    class WhenBlockUserSelf {

        @BeforeEach
        void setUp() {
            given(user.getId()).willReturn(userId);
            given(targetUser.getId()).willReturn(userId);
            willDoNothing().given(blockedUserValidator).validateTargetUser(targetUser);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> blockedUserService.blockUser(user, targetUser),
                "자기 자신을 차단할 수 없습니다."
            );
        }
    }

    @Nested
    class WhenDuplicated {

        @BeforeEach
        void setUp() {
            given(user.getId()).willReturn(userId);
            given(targetUser.getId()).willReturn(targetUserId);
            willDoNothing().given(blockedUserValidator).validateTargetUser(targetUser);
            given(blockedUserRepository.existsByUserIdAndBlockedUserId(userId, targetUserId)).willReturn(true);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> blockedUserService.blockUser(user, targetUser),
                "이미 차단한 사용자입니다."
            );
        }
    }

    @Nested
    class WhenExceptedBlockUser {

        @BeforeEach
        void setUp() {
            given(user.getId()).willReturn(userId);
            given(targetUser.getId()).willReturn(targetUserId);
            willThrow(new BadRequestException("해당 계정은 운영계정으로 차단이 불가합니다.")).given(blockedUserValidator).validateTargetUser(targetUser);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> blockedUserService.blockUser(user, targetUser),
                "해당 계정은 운영계정으로 차단이 불가합니다."
            );
        }
    }

    @Nested
    class WhenUnblockUser {

        @Test
        void shouldCallDeleted() {
            blockedUserService.unblockUser(userId, targetUserId);

            then(blockedUserRepository).should().deleteByUserIdAndBlockedUserId(userId, targetUserId);
        }
    }
}