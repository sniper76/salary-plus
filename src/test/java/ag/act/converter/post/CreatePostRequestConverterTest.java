package ag.act.converter.post;

import ag.act.converter.post.poll.CreatePollRequestConverter;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.ClientType;
import ag.act.model.CreatePostRequest;
import ag.act.service.image.ThumbnailImageService;
import ag.act.service.post.PostIsActiveDecisionMaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CreatePostRequestConverterTest {

    @InjectMocks
    private CreatePostRequestConverter converter;
    @Mock
    private PostIsActiveDecisionMaker postIsActiveDecisionMaker;
    @Mock
    private ThumbnailImageService thumbnailImageService;
    @Mock
    private CreatePollRequestConverter createPollRequestConverter;
    @Mock
    private DigitalProxyCreateRequestConverter digitalProxyCreateRequestConverter;

    @Nested
    class Convert {
        @Mock
        private CreatePostRequest createPostRequest;
        @Mock
        private Board board;
        @Mock
        private User user;
        private String thumbnailImageUrl;
        private Long boardId;
        private Long userId;
        private String content;
        private String title;
        private Boolean isAnonymous;
        private ClientType clientType;

        @BeforeEach
        void setUp() {
            boardId = someLong();
            userId = someLong();
            content = someString(10);
            title = someString(10);
            isAnonymous = someBoolean();
            thumbnailImageUrl = someString(30);
            clientType = someEnum(ClientType.class);

            given(board.getId()).willReturn(boardId);
            given(user.getId()).willReturn(userId);
            given(createPostRequest.getContent()).willReturn(content);
            given(createPostRequest.getTitle()).willReturn(title);
            given(createPostRequest.getIsAnonymous()).willReturn(isAnonymous);
            given(createPostRequest.getPolls()).willReturn(null);
            given(createPostRequest.getDigitalProxy()).willReturn(null);
        }

        @Nested
        class WithNoImages {
            @Test
            void shouldMakePost() {
                final Post makePost = converter.convert(createPostRequest, user, board, clientType);

                assertThat(makePost.getContent(), is(content));
                assertThat(makePost.getTitle(), is(title));
                assertThat(makePost.getFirstPoll(), is(nullValue()));
                assertThat(makePost.getDigitalProxy(), is(nullValue()));
                assertThat(makePost.getIsAnonymous(), is(isAnonymous));
                assertThat(makePost.getBoardId(), is(boardId));
                assertThat(makePost.getUserId(), is(userId));
            }
        }

        @Nested
        class WithImagesByIds {

            @BeforeEach
            void setUp() {
                final List<Long> imageIds = List.of(someLong(), someLong());

                given(createPostRequest.getImageIds()).willReturn(imageIds);
                given(thumbnailImageService.generate(imageIds, content)).willReturn(thumbnailImageUrl);
            }

            @Test
            void shouldMakePost() {
                final Post makePost = converter.convert(createPostRequest, user, board, clientType);

                assertThat(makePost.getContent(), is(content));
                assertThat(makePost.getTitle(), is(title));
                assertThat(makePost.getFirstPoll(), is(nullValue()));
                assertThat(makePost.getDigitalProxy(), is(nullValue()));
                assertThat(makePost.getIsAnonymous(), is(isAnonymous));
                assertThat(makePost.getBoardId(), is(boardId));
                assertThat(makePost.getUserId(), is(userId));
                assertThat(makePost.getThumbnailImageUrl(), is(thumbnailImageUrl));
            }
        }

        @Nested
        class WithImagesByHtmlContent {
            @BeforeEach
            void setUp() {
                given(createPostRequest.getImageIds()).willReturn(List.of());
                given(thumbnailImageService.generate(List.of(), content)).willReturn(thumbnailImageUrl);
            }

            @Test
            void shouldMakePost() {
                final Post makePost = converter.convert(createPostRequest, user, board, clientType);

                assertThat(makePost.getContent(), is(content));
                assertThat(makePost.getTitle(), is(title));
                assertThat(makePost.getFirstPoll(), is(nullValue()));
                assertThat(makePost.getDigitalProxy(), is(nullValue()));
                assertThat(makePost.getIsAnonymous(), is(isAnonymous));
                assertThat(makePost.getBoardId(), is(boardId));
                assertThat(makePost.getUserId(), is(userId));
                assertThat(makePost.getThumbnailImageUrl(), is(thumbnailImageUrl));
            }
        }
    }
}
