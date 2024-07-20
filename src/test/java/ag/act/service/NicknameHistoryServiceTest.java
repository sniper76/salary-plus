package ag.act.service;

import ag.act.entity.NicknameHistory;
import ag.act.entity.User;
import ag.act.model.Status;
import ag.act.repository.NicknameHistoryRepository;
import ag.act.validator.user.NicknameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class NicknameHistoryServiceTest {
    @InjectMocks
    private NicknameHistoryService service;
    @Mock
    private NicknameHistoryRepository nicknameHistoryRepository;
    @Mock
    private NicknameValidator nicknameValidator;
    @Mock
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = someLong();
        final String nickname = someString(10);

        willDoNothing().given(nicknameValidator).validateNickname(user);
        given(user.getId()).willReturn(userId);
        given(user.getNickname()).willReturn(nickname);
        given(nicknameHistoryRepository.countByUserId(userId)).willReturn(10);
        given(nicknameHistoryRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class WhenFirstNickname {

        @Test
        void shouldCreateNicknameHistory() {
            // Given
            given(nicknameHistoryRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE)).willReturn(Collections.emptyList());
            given(nicknameHistoryRepository.countByUserId(userId)).willReturn(0);

            // When
            final NicknameHistory nicknameHistory = service.create(user);

            // Then
            assertThat(nicknameHistory.getUserId(), is(userId));
            assertThat(nicknameHistory.getNickname(), is(user.getNickname()));
            assertThat(nicknameHistory.getStatus(), is(Status.ACTIVE));
            assertThat(nicknameHistory.getIsFirst(), is(true));
        }
    }

    @Nested
    class WhenNicknameIsNotFirst {

        @Mock
        private NicknameHistory nicknameHistory;
        private NicknameHistory savedNicknameHistory;

        @BeforeEach
        void setUp() {
            given(nicknameHistoryRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE)).willReturn(List.of(nicknameHistory));
            given(nicknameHistoryRepository.countByUserId(userId)).willReturn(someIntegerBetween(1, 10));

            savedNicknameHistory = service.create(user);
        }

        @Test
        void shouldCreateNicknameHistory() {
            assertThat(savedNicknameHistory.getUserId(), is(userId));
            assertThat(savedNicknameHistory.getNickname(), is(user.getNickname()));
            assertThat(savedNicknameHistory.getStatus(), is(Status.ACTIVE));
            assertThat(savedNicknameHistory.getIsFirst(), is(false));
        }

        @Test
        void shouldDeletePreviousNicknameHistories() {
            then(nicknameHistory).should().setStatus(Status.INACTIVE_BY_USER);
            then(nicknameHistory).should().setDeletedAt(any(LocalDateTime.class));
            then(nicknameHistoryRepository).should().saveAll(List.of(nicknameHistory));
        }
    }
}
