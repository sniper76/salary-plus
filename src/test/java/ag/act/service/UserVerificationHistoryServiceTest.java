package ag.act.service;

import ag.act.core.holder.RequestContextHolder;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.repository.UserVerificationHistoryRepository;
import ag.act.service.user.UserVerificationHistoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserVerificationHistoryServiceTest {

    @InjectMocks
    private UserVerificationHistoryService service;
    private List<MockedStatic<?>> statics;

    @Mock
    private UserVerificationHistoryRepository userVerificationHistoryRepository;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(RequestContextHolder.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenCreateUserVerificationHistory {

        private UserVerificationHistory actualResult;
        @Mock
        private UserVerificationHistory userVerificationHistory;

        @BeforeEach
        void setUp() {
            final Long userId = someLong();
            final VerificationType verificationType = someEnum(VerificationType.class);
            final VerificationOperationType operationType = someEnum(VerificationOperationType.class);
            final String userIp = someString(5);

            given(RequestContextHolder.getUserIP()).willReturn(userIp);
            given(userVerificationHistoryRepository.save(any(UserVerificationHistory.class))).willReturn(userVerificationHistory);

            actualResult = service.create(userId, verificationType, operationType);
        }

        @Test
        void shouldReturnCreatedUserVerificationHistory() {
            assertThat(actualResult, is(userVerificationHistory));
        }

        @Test
        void shouldCallUserVerificationHistoryRepositorySave() {
            given(userVerificationHistoryRepository.save(any(UserVerificationHistory.class))).willReturn(userVerificationHistory);
        }
    }
}