package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.PostUserView;
import ag.act.entity.User;
import ag.act.repository.PostUserViewRepository;
import ag.act.service.post.PostUserViewService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostUserViewServiceTest {
    @InjectMocks
    private PostUserViewService postUserViewService;
    @Mock
    private PostUserViewRepository postUserViewRepository;

    @Nested
    class WhenUpdatePostUserView {
        private List<MockedStatic<?>> statics;
        @Mock
        private User user;
        @Mock
        private PostUserView postUserView;
        private Long userId;
        private Long postId;
        private Long count;

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(ActUserProvider.class));
            // Given
            userId = someLong();
            postId = someLong();
            count = someLong();
            given(ActUserProvider.get()).willReturn(Optional.of(user));
            given(user.getId()).willReturn(userId);
            given(postUserView.getCount()).willReturn(count);
        }

        @AfterEach
        void tearDown() {
            statics.forEach(MockedStatic::close);
        }

        @Test
        void shouldUpdatePostUserViewWithExistingPostUserView() {
            // Given
            given(postUserViewRepository.findByPostIdAndUserId(postId, userId))
                .willReturn(Optional.of(postUserView));

            // When
            postUserViewService.createOrUpdatePostUserViewCount(postId);

            // Then
            then(postUserView).should(times(1)).setCount(count + 1);
            then(postUserViewRepository).should().save(postUserView);

        }

        @Test
        void shouldUpdatePostUserViewWithCreatingNewPostUserView() {
            // Given
            given(postUserViewRepository.findByPostIdAndUserId(postId, userId))
                .willReturn(Optional.empty());

            // When
            postUserViewService.createOrUpdatePostUserViewCount(postId);

            // Then
            then(postUserViewRepository).should().save(any(PostUserView.class));
        }
    }
}