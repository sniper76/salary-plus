package ag.act.validator;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.exception.BadRequestException;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreateDigitalProxyRequest;
import ag.act.model.CreatePollAnswerItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.repository.BoardRepository;
import ag.act.repository.PostRepository;
import ag.act.service.user.UserRoleService;
import ag.act.validator.post.StockBoardGroupPostValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someBoardCategoryExceptDebate;
import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static ag.act.TestUtil.someLocalDateTimeInThePast;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostValidatorTest {

    @InjectMocks
    @Spy
    private StockBoardGroupPostValidator validator;
    private List<MockedStatic<?>> statics;

    @Mock
    private PostRepository postRepository;
    @Mock
    private DefaultObjectValidator defaultObjectValidator;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private User user;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        final Long userId = someLong();

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
    }

    @Nested
    class ValidatePollForAnswer {

        @Mock
        private Poll poll;
        private Long pollId;

        @BeforeEach
        void setUp() {
            pollId = someLong();
            final LocalDateTime startDateTime = someLocalDateTimeInThePast().minusDays(10);
            final LocalDateTime endDateTime = startDateTime.plusDays(someIntegerBetween(3, 6));

            given(poll.getId()).willReturn(pollId);
            given(poll.getTargetStartDate()).willReturn(startDateTime);
            given(poll.getTargetStartDate()).willReturn(endDateTime);
            given(poll.getPollItemList()).willReturn(List.of(mock(PollItem.class)));
        }

        @Nested
        class WhenPollIsValid {

            @Test
            void shouldNotThrowAnyException() {
                validator.validatePollBeforeAnswer(poll, pollId);
            }
        }

        @Nested
        class WhenPollIsNull {

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validatePollBeforeAnswer(null, someLong()),
                    "게시글에 설문 정보가 존재하지 않습니다."
                );
            }
        }

        @Nested
        class WhenPollIdIsNotMatched {

            @Test
            void shouldThrowBadRequestException() {

                // Given
                final Long wrongPollId = someLong();

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validatePollBeforeAnswer(poll, wrongPollId),
                    "게시글과 설문 정보가 일치하지 않습니다."
                );
            }
        }

        @Nested
        class WhenPollItemsAreInvalid {

            @Test
            void shouldThrowBadRequestException() {

                // Given
                given(poll.getPollItemList()).willReturn(Collections.emptyList());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validatePollBeforeAnswer(poll, pollId),
                    "설문 항목이 존재하지 않습니다."
                );
            }
        }

        @Nested
        class WhenPollIsNotStartedYet {

            @Test
            void shouldThrowBadRequestException() {

                // Given
                given(poll.getTargetStartDate()).willReturn(someLocalDateTimeInTheFuture());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validatePollBeforeAnswer(poll, pollId),
                    "진행하기 전 설문입니다."
                );
            }
        }

        @Nested
        class WhenPollIsAlreadyFinished {

            @Test
            void shouldThrowBadRequestException() {

                // Given
                given(poll.getTargetEndDate()).willReturn(someLocalDateTimeInThePast());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validatePollBeforeAnswer(poll, pollId),
                    "이미 종료된 설문입니다."
                );
            }
        }

    }

    @Nested
    class ValidateUserPermission {
        @Mock
        private ag.act.model.CreatePostRequest request;
        private BoardCategory boardCategory;

        @BeforeEach
        void setUp() {

            // Given
            boardCategory = BoardCategory.DEBATE;

            given(user.isAdmin()).willReturn(false);
            given(request.getBoardCategory()).willReturn(boardCategory.name());
        }

        @Nested
        class WhenUserIsAdmin {

            @Test
            void shouldNotThrowAnyException() {
                // Given
                given(user.isAdmin()).willReturn(true);

                // When
                validator.validateUserPermission(request, false);
            }
        }

        @Nested
        class WhenUserIsNotAdmin {

            @Nested
            class AndBoardCategoryNotFound {
                @Test
                void shouldThrowBadRequest() {

                    // Given
                    given(request.getBoardCategory()).willReturn(someString(5));

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validateUserPermission(request, false),
                        "게시판 카테고리가 존재하지 않습니다."
                    );
                }
            }

            @Nested
            class AndBoardCategoryIsNotDebate {

                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    final BoardCategory boardCategory = someBoardCategoryExceptDebate();
                    given(request.getBoardCategory()).willReturn(boardCategory.name());

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validateUserPermission(request, false),
                        "%s 게시판에 글쓰기 권한이 없습니다.".formatted(boardCategory.getDisplayName())
                    );
                }
            }

            @Nested
            class AndTryToCreateDigitalProxy {

                @Mock
                private ag.act.model.CreateDigitalProxyRequest createDigitalProxyRequest;

                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    given(request.getDigitalProxy()).willReturn(createDigitalProxyRequest);

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validateUserPermission(request, false),
                        "%s 게시판에 의결권위임을 생성할 권한이 없습니다.".formatted(boardCategory.getDisplayName())
                    );
                }
            }
        }
    }

    @Nested
    class ValidatePollAnswers {

        private List<PollItem> pollItemList;
        private List<CreatePollAnswerItemRequest> pollAnswers;
        @Mock
        private CreatePollAnswerItemRequest answerItem1;
        @Mock
        private CreatePollAnswerItemRequest answerItem2;
        @Mock
        private CreatePollAnswerItemRequest answerItem3;
        @Mock
        private PollItem pollItem1;
        @Mock
        private PollItem pollItem2;
        @Mock
        private PollItem pollItem3;

        @BeforeEach
        void setUp() {
            final Long pollItemId1 = someLong();
            final Long pollItemId2 = someLong();
            final Long pollItemId3 = someLong();

            given(pollItem1.getId()).willReturn(pollItemId1);
            given(pollItem2.getId()).willReturn(pollItemId2);
            given(pollItem3.getId()).willReturn(pollItemId3);
            given(answerItem1.getPollItemId()).willReturn(pollItemId1);
            given(answerItem2.getPollItemId()).willReturn(pollItemId2);
            given(answerItem3.getPollItemId()).willReturn(pollItemId3);
        }

        @Nested
        class WhenPollAnswersAndPollItemsAreMatched {

            @BeforeEach
            void setUp() {
                pollAnswers = List.of(answerItem1, answerItem2, answerItem3);
                pollItemList = List.of(pollItem1, pollItem2, pollItem3);
            }

            @Test
            void shouldValidate() {
                validator.validatePollAnswers(pollAnswers, pollItemList);
            }
        }

        @Nested
        class WhenPollAnswerHasMoreItems {
            @BeforeEach
            void setUp() {
                pollAnswers = List.of(answerItem1, answerItem2);
                pollItemList = List.of(pollItem1, pollItem2, pollItem3);
            }

            @Test
            void shouldValidate() {
                validator.validatePollAnswers(pollAnswers, pollItemList);
            }
        }

        @Nested
        class WhenPollItemHasMoreItems {
            @BeforeEach
            void setUp() {
                pollAnswers = List.of(answerItem1, answerItem2, answerItem3);
                pollItemList = List.of(pollItem1, pollItem2);
            }

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validatePollAnswers(pollAnswers, pollItemList),
                    "게시글의 설문 항목과 일치하지 않습니다."
                );
            }
        }
    }

    @Nested
    class ValidateAuthor {

        @Mock
        private User currentUser;

        @Nested
        class WhenUserIsAdmin {

            @Nested
            class AndAuthorIsAdmin {
                @Test
                void shouldReturnWithoutAnyException() {
                    Long authorId = someLong();

                    // Given
                    given(currentUser.isAdmin()).willReturn(true);
                    given(userRoleService.isAdmin(authorId)).willReturn(true);

                    // When // Then
                    validator.validateAuthor(currentUser, authorId, "게시글");
                }
            }

            @Nested
            class AndAuthorIsNotAdmin {
                @Test
                void shouldReturnWithoutAnyException() {
                    Long authorId = someLong();

                    // Given
                    given(currentUser.isAdmin()).willReturn(true);
                    given(userRoleService.isAdmin(authorId)).willReturn(false);

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validateAuthor(currentUser, authorId, "게시글"),
                        "게시글 등록자와 현재사용자가 일치하지 않습니다."
                    );
                }
            }
        }

        @Nested
        class WhenUserIsAuthor {
            @Test
            void shouldReturnWithoutAnyException() {

                // Given
                final Long userId = someLong();

                given(currentUser.isAdmin()).willReturn(false);
                given(currentUser.getId()).willReturn(userId);

                // When // Then
                validator.validateAuthor(currentUser, userId, "게시글");
            }
        }

        @Nested
        class WhenUserIsNotAuthor {
            @Test
            void shouldThrowBadRequestException() {

                // Given
                final String message = someString(10);
                given(currentUser.isAdmin()).willReturn(false);
                given(currentUser.getId()).willReturn(someLong());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validateAuthor(currentUser, someLong(), message),
                    "%s 등록자와 현재사용자가 일치하지 않습니다.".formatted(message)
                );
            }
        }
    }

    @Nested
    class ValidateCategory {
        private ag.act.model.CreatePostRequest request;

        @Nested
        class WhenSurveyAndPollIsNull {
            //TODO polls(null) 로 변경해야 함
            //            @Test
            //            void shouldThrowBadRequest() {
            //                request = new CreatePostRequest()
            //                    .boardCategory(BoardCategory.SURVEYS.name())
            //                    .poll(null);
            //
            //                assertException(
            //                    BadRequestException.class,
            //                    () -> validator.validateCategory(request),
            //                    "설문 정보가 없습니다."
            //                );
            //            }
        }

        @Nested
        class WhenBoardCategoryInvalid {
            @Test
            void shouldThrowBadRequest() {
                request = new CreatePostRequest()
                    .boardCategory(someString(10))
                    .polls(null);

                assertException(
                    BadRequestException.class,
                    () -> validator.validateCategory(request),
                    "게시글 카테고리를 확인해 주세요."
                );
            }
        }

        @Nested
        class WhenDigitalDocumentBoardCategoryButDigitalDocumentIsNull {
            @Test
            void shouldThrowBadRequest() {
                request = new CreatePostRequest()
                    .boardCategory(someThing(BoardCategory.ETC, BoardCategory.CO_HOLDING_ARRANGEMENTS).name())
                    .digitalDocument(null);

                assertException(
                    BadRequestException.class,
                    () -> validator.validateCategory(request),
                    "전자문서 정보가 없습니다."
                );
            }
        }

        @Nested
        class WhenDigitalDelegationBoardCategoryButDigitalProxyIsNull {
            @Test
            void shouldThrowBadRequest() {
                request = new CreatePostRequest()
                    .boardCategory(BoardCategory.DIGITAL_DELEGATION.name())
                    .digitalDocument(null)
                    .digitalProxy(null);

                assertException(
                    BadRequestException.class,
                    () -> validator.validateCategory(request),
                    "모두싸인 혹은 전자문서 정보가 없습니다."
                );
            }
        }

        @Nested
        class WhenSubModuleMoreThanOne {
            @Test
            void shouldThrowBadRequest() {
                request = new CreatePostRequest()
                    .boardCategory(BoardCategory.DIGITAL_DELEGATION.name())
                    .digitalDocument(new CreateDigitalDocumentRequest())
                    .digitalProxy(someThing(new CreateDigitalProxyRequest(), null))
                    .polls(List.of(new CreatePollRequest()));

                assertException(
                    BadRequestException.class,
                    () -> validator.validateCategory(request),
                    "2개 이상의 서브 모듈을 등록할 수 없습니다."
                );
            }
        }
    }


    @Nested
    class ValidateChangeAnonymous {

        @Mock
        private Post post;

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(true, false),
                Arguments.of(false, true)
            );
        }

        @ParameterizedTest(name = "{index} => requestedIsAnonymous=''{0}'', postIsAnonymous=''{1}''")
        @MethodSource("valueProvider")
        void shouldThrowBadRequest(Boolean requestedIsAnonymous, Boolean postIsAnonymous) {

            // Given
            given(post.getIsAnonymous()).willReturn(postIsAnonymous);

            // When // Then
            assertException(
                BadRequestException.class,
                () -> validator.validateChangeAnonymous(post, requestedIsAnonymous),
                "게시글의 익명 여부는 변경할 수 없습니다."
            );
        }
    }
}
