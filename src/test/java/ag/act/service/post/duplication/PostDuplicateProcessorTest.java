package ag.act.service.post.duplication;

import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.DigitalDocumentType;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserProfileService;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostDuplicateProcessorTest {
    @InjectMocks
    private PostDuplicateProcessor processor;
    @Mock
    private PostService postService;
    @Mock
    private PostUserProfileService postUserProfileService;
    @Mock
    private PostImageService postImageService;
    @Mock
    private StockService stockService;
    @Mock
    private NotificationService notificationService;

    @Nested
    class DuplicatePosts {
        @Mock
        private Post sourcePost;
        @Mock
        private Post expectedDuplicatedPost;
        @Mock
        private Board postBoard;
        @Mock
        private Board anotherBoard;
        @Mock
        private PostUserProfile postUserProfile;
        @Mock
        private PostUserProfile duplicatedPostUserProfile;
        @Mock
        private PostImage postImage1;
        @Mock
        private PostImage postImage2;
        @Mock
        private Stock anotherStock;
        private String anotherStockCode;
        private Long duplicatedPostId;
        private Long postImage1Id;
        private Long postImage2Id;
        private List<PostImage> duplicatedPostImages;
        private List<PostImage> postImages;
        private Post actualDuplicatedPost;

        @BeforeEach
        void setUp() {
            anotherStockCode = someStockCode();
            postImages = List.of(postImage1, postImage2);
            final Long anotherBoardId = someLong();
            duplicatedPostId = someLong();
            postImage1Id = someLong();
            postImage2Id = someLong();
            BoardCategory category = someEnum(BoardCategory.class);
            duplicatedPostImages = List.of();

            given(sourcePost.getId()).willReturn(someLong());
            given(sourcePost.copyOf(anotherBoardId)).willReturn(expectedDuplicatedPost);
            given(postService.savePost(expectedDuplicatedPost)).willReturn(expectedDuplicatedPost);
            given(expectedDuplicatedPost.getId()).willReturn(duplicatedPostId);

            given(postImage1.getId()).willReturn(postImage1Id);
            given(postImage2.getId()).willReturn(postImage2Id);

            given(postBoard.getId()).willReturn(someLong());
            given(anotherBoard.getId()).willReturn(anotherBoardId);
            given(postBoard.getCategory()).willReturn(category);

            given(postUserProfile.copyOf(duplicatedPostId)).willReturn(duplicatedPostUserProfile);
            given(postUserProfileService.save(duplicatedPostUserProfile)).willReturn(duplicatedPostUserProfile);

            given(postImageService.savePostImageList(duplicatedPostId, List.of(postImage1Id, postImage2Id)))
                .willReturn(duplicatedPostImages);
        }

        @Nested
        class WhenNormalPost extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                actualDuplicatedPost = processor.copyPost(
                    sourcePost,
                    postUserProfile,
                    postImages,
                    anotherBoard
                );
            }
        }

        @Nested
        class WhenPostHasDigitalDocument extends DefaultTestCases {

            @Mock
            private DigitalDocument sourceDigitalDocument;
            @Mock
            private DigitalDocument duplicatedDigitalDocument;
            private String stockName;

            @BeforeEach
            void setUp() {
                stockName = someString(10);

                given(anotherBoard.getStockCode()).willReturn(anotherStockCode);
                given(stockService.findByCode(anotherStockCode)).willReturn(Optional.of(anotherStock));
                given(anotherStock.getName()).willReturn(stockName);

                given(sourcePost.getDigitalDocument()).willReturn(sourceDigitalDocument);
                given(sourceDigitalDocument.copyOf(expectedDuplicatedPost, anotherStockCode)).willReturn(duplicatedDigitalDocument);
                given(expectedDuplicatedPost.getDigitalDocument()).willReturn(duplicatedDigitalDocument);
                given(duplicatedDigitalDocument.getType()).willReturn(DigitalDocumentType.ETC_DOCUMENT);

                actualDuplicatedPost = processor.copyPost(
                    sourcePost,
                    postUserProfile,
                    postImages,
                    anotherBoard
                );
            }

            @Test
            void shouldSetCompanyNameWithStockName() {
                then(duplicatedDigitalDocument).should().setCompanyName(stockName);
            }

            @Test
            void shouldGetStock() {
                then(stockService).should().findByCode(anotherStockCode);
            }
        }

        @Nested
        class WhenPostHasPoll extends DefaultTestCases {

            @Mock
            private Poll sourcePoll;
            @Mock
            private Poll duplicatedPoll;
            @Mock
            private PollItem sourcePollItem;
            @Mock
            private PollItem duplicatedPollItem;

            @BeforeEach
            void setUp() {

                List<PollItem> sourcePollItems = List.of(sourcePollItem);

                given(sourcePost.getPolls()).willReturn(List.of(sourcePoll));
                given(sourcePoll.copyOf(expectedDuplicatedPost)).willReturn(duplicatedPoll);
                given(sourcePoll.getPollItemList()).willReturn(sourcePollItems);
                given(sourcePollItem.copyOf(duplicatedPoll)).willReturn(duplicatedPollItem);

                actualDuplicatedPost = processor.copyPost(
                    sourcePost,
                    postUserProfile,
                    postImages,
                    anotherBoard
                );
            }

            @Test
            void shouldCallCopyPoll() {
                then(sourcePoll).should().copyOf(expectedDuplicatedPost);
            }

            @Test
            void shouldCallCopyPollItems() {
                then(sourcePollItem).should().copyOf(duplicatedPoll);
            }

            @Test
            void shouldSetDuplicatedPollItemList() {
                then(duplicatedPoll).should().setPollItemList(List.of(duplicatedPollItem));
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldReturnDuplicatedCount() {
                assertThat(actualDuplicatedPost, is(expectedDuplicatedPost));
            }

            @Test
            void shouldCreateNotification() {
                then(notificationService).should().createNotification(expectedDuplicatedPost);
            }

            @Test
            void shouldSaveCopiedPost() {
                verify(postService, times(2)).savePost(expectedDuplicatedPost);
            }

            @Test
            void shouldSaveCopiedPostUserProfile() {
                then(postUserProfileService).should().save(duplicatedPostUserProfile);
            }

            @Test
            void shouldSaveCopiedPostImages() {
                given(postImageService.savePostImageList(duplicatedPostId, List.of(postImage1Id, postImage2Id)))
                    .willReturn(duplicatedPostImages);
            }
        }
    }
}
