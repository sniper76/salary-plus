package ag.act.validator;

import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.exception.BadRequestException;
import ag.act.module.cache.AppPreferenceCache;
import ag.act.validator.user.BlockedUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static ag.act.TestUtil.assertException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings
class BlockedUserValidatorTest {

    @Mock
    private AppPreferenceCache appPreferenceCache;
    @InjectMocks
    private BlockedUserValidator blockedUserValidator;

    @Mock
    private User targetUser;

    @BeforeEach
    void setUp() {
        final Long targetUserId = someLong();
        given(targetUser.getId()).willReturn(targetUserId);
        given(appPreferenceCache.getValue(AppPreferenceType.BLOCK_EXCEPT_USER_IDS)).willReturn(List.of(targetUserId));
    }

    @Nested
    class WhenFailure {

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> blockedUserValidator.validateTargetUser(targetUser),
                "해당 계정은 운영계정으로 차단이 불가합니다."
            );
        }
    }

    @Nested
    class WhenSuccess {

        @BeforeEach
        void setUp() {
            final Long anotherUserId = someLong();
            given(targetUser.getId()).willReturn(anotherUserId);
        }

        @Test
        void shouldNotThrowException() {
            assertDoesNotThrow(() -> blockedUserValidator.validateTargetUser(targetUser));
        }
    }
}