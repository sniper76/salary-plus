package ag.act.service;

import ag.act.converter.push.PushRequestConverter;
import ag.act.dto.push.PushSearchDto;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.enums.push.PushSearchType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushTargetType;
import ag.act.repository.PushRepository;
import ag.act.service.post.PostService;
import ag.act.service.push.PushService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import ag.act.validator.PushValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PushServiceTest {

    @InjectMocks
    private PushService service;

    @Mock
    private PushRepository pushRepository;
    @Mock
    private StockService stockService;
    @Mock
    private StockGroupService stockGroupService;
    @Mock
    public PushValidator pushValidator;
    @Mock
    public PushRequestConverter pushRequestConverter;
    @Mock
    public PostService postService;

    @Nested
    class GetPushList {

        @Mock
        private PushSearchDto pushSearchDto;
        @Mock
        private Pageable pageRequest;
        @Mock
        private Page<Push> pushPage;
        private Page<Push> actualPushPage;
        private String searchKeyword;

        @BeforeEach
        void setUp() {
            searchKeyword = someString(5);
        }

        @Nested
        class WhenSearchKeywordIsBlank {

            @BeforeEach
            void setUp() {
                given(pushSearchDto.isEmpty()).willReturn(true);
                given(pushRepository.findAllByPushTargetTypeNot(any(PushTargetType.class), any(Pageable.class))).willReturn(pushPage);

                actualPushPage = service.getPushList(pushSearchDto, pageRequest);
            }

            @Test
            void shouldReturnPushPage() {
                assertThat(actualPushPage, is(pushPage));
            }

            @Test
            void shouldCallPushRepository() {
                then(pushRepository).should().findAllByPushTargetTypeNot(
                    any(PushTargetType.class), any(Pageable.class)
                );
            }
        }

        @Nested
        class WhenSearchKeywordIsStockName {

            @BeforeEach
            void setUp() {
                given(pushSearchDto.isEmpty()).willReturn(false);
                given(pushSearchDto.getPushSearchType()).willReturn(PushSearchType.STOCK_NAME);
                given(pushSearchDto.getKeyword()).willReturn(searchKeyword);
                given(pushRepository.findAllByPushTargetTypeNotAndStockNameContaining(
                    any(PushTargetType.class), eq(searchKeyword), any(Pageable.class))
                ).willReturn(pushPage);

                actualPushPage = service.getPushList(pushSearchDto, pageRequest);
            }

            @Test
            void shouldReturnPushPage() {
                assertThat(actualPushPage, is(pushPage));
            }

            @Test
            void shouldCallPushRepository() {
                then(pushRepository).should().findAllByPushTargetTypeNotAndStockNameContaining(
                    any(PushTargetType.class), eq(searchKeyword), any(Pageable.class)
                );
            }
        }

        @Nested
        class WhenSearchKeywordIsPushContent {

            @BeforeEach
            void setUp() {
                given(pushSearchDto.isEmpty()).willReturn(false);
                given(pushSearchDto.getPushSearchType()).willReturn(PushSearchType.PUSH_CONTENT);
                given(pushSearchDto.getKeyword()).willReturn(searchKeyword);
                given(pushRepository.findAllByPushTargetTypeNotAndContentContaining(
                    any(PushTargetType.class), eq(searchKeyword), any(Pageable.class))
                ).willReturn(pushPage);

                actualPushPage = service.getPushList(pushSearchDto, pageRequest);
            }

            @Test
            void shouldReturnPushPage() {
                assertThat(actualPushPage, is(pushPage));
            }

            @Test
            void shouldCallPushRepository() {
                then(pushRepository).should().findAllByPushTargetTypeNotAndContentContaining(
                    any(PushTargetType.class), eq(searchKeyword), any(Pageable.class)
                );
            }
        }
    }

    @Nested
    class CreatePush {

        @Mock
        private ag.act.model.CreatePushRequest createPushRequest;
        @Mock
        private Push requestPush;
        @Mock
        private Push expectedPush;
        private Push actualPush;
        @Mock
        private Post post;

        @BeforeEach
        void setUp() {
            willDoNothing().given(pushValidator).validate(createPushRequest);
            given(createPushRequest.getPostId()).willReturn(someLong());
            given(createPushRequest.getTitle()).willReturn(someString(5));
            given(postService.getPostNotDeleted(createPushRequest.getPostId())).willReturn(post);
            given(pushRequestConverter.convert(createPushRequest, post)).willReturn(requestPush);
            given(pushRepository.save(requestPush)).willReturn(expectedPush);
        }

        @Nested
        class WhenCreateStockPushSuccess extends DefaultTestCases {
            @Mock
            private Stock stock;
            private String stockCode;

            @BeforeEach
            void setUp() {
                stockCode = someStockCode();

                final PushTargetType stockTargetType = PushTargetType.STOCK;
                given(stockService.findByCode(stockCode)).willReturn(Optional.of(stock));
                given(expectedPush.getStockCode()).willReturn(stockCode);
                given(expectedPush.getPushTargetType()).willReturn(stockTargetType);

                actualPush = service.createPush(createPushRequest);
            }

            @Test
            void shouldNotCallStockGroupService() {
                then(stockGroupService).shouldHaveNoInteractions();
            }

            @Test
            void shouldCallFindByCode() {
                then(stockService).should().findByCode(stockCode);
            }

            @Test
            void shouldSetStockToPush() {
                then(expectedPush).should().setStock(stock);
            }
        }

        @Nested
        class WhenCreateStockGroupPushSuccess extends DefaultTestCases {
            @Mock
            private StockGroup stockGroup;

            private Long stockGroupId;

            @BeforeEach
            void setUp() {
                stockGroupId = someLong();

                final PushTargetType stockTargetType = PushTargetType.STOCK_GROUP;
                given(stockGroupService.findById(stockGroupId)).willReturn(Optional.of(stockGroup));
                given(expectedPush.getStockGroupId()).willReturn(stockGroupId);
                given(expectedPush.getPushTargetType()).willReturn(stockTargetType);

                actualPush = service.createPush(createPushRequest);
            }

            @Test
            void shouldNotCallStockService() {
                then(stockService).shouldHaveNoInteractions();
            }

            @Test
            void shouldCallFindByStockGroupId() {
                then(stockGroupService).should().findById(stockGroupId);
            }

            @Test
            void shouldSetStockGroupToPush() {
                then(expectedPush).should().setStockGroup(stockGroup);
            }
        }

        @SuppressWarnings("unused")
        class DefaultTestCases {

            @Test
            void shouldReturnPush() {
                assertThat(actualPush, is(expectedPush));
            }

            @Test
            void shouldCallPushRequestConverter() {
                then(pushRequestConverter).should().convert(createPushRequest, post);
            }

            @Test
            void shouldCallPushValidator() {
                then(pushValidator).should().validate(createPushRequest);
            }

            @Test
            void shouldCallPushRepository() {
                then(pushRepository).should().save(requestPush);
            }
        }
    }

    @Nested
    class DeletePush {

        private Long pushId;
        @Mock
        private Push push;

        @BeforeEach
        void setUp() {
            pushId = someLong();
        }

        @Nested
        class WhenPushExists {

            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
            private Optional<Push> pushOptional;

            @BeforeEach
            void setUp() {
                pushOptional = Optional.of(push);

                given(pushRepository.findById(pushId)).willReturn(pushOptional);
            }

            @Nested
            class AndDeleteSuccess {
                @BeforeEach
                void setUp() {
                    given(pushValidator.validateForDeleteAndGet(pushOptional)).willReturn(push);

                    service.deletePush(pushId);
                }

                @Test
                void shouldCallPushServiceToFindPush() {
                    then(pushRepository).should().findById(pushId);
                }

                @Test
                void shouldCallPushServiceToDeletePush() {
                    then(pushRepository).should().delete(DeletePush.this.push);
                }

                @Test
                void shouldCallPushValidator() {
                    then(pushValidator).should().validateForDeleteAndGet(pushOptional);
                }
            }

            @Nested
            class AndValidatorThrowException {
                @BeforeEach
                void setUp() {
                    willThrow(RuntimeException.class).given(pushValidator).validateForDeleteAndGet(pushOptional);
                }

                @Test
                void shouldThrowException() {
                    assertThrows(
                        RuntimeException.class,
                        () -> service.deletePush(pushId)
                    );
                }
            }
        }

        @Nested
        class WhenPushValidationFail {
            @SuppressWarnings("unchecked")
            @BeforeEach
            void setUp() {
                given(pushValidator.validateForDeleteAndGet(any(Optional.class))).willThrow(RuntimeException.class);
            }

            @Test
            void shouldThrowException() {
                assertThrows(
                    RuntimeException.class,
                    () -> service.deletePush(pushId)
                );
            }
        }
    }

    @Nested
    class UpdatePushToProcessing {

        @Mock
        private Push push;

        @BeforeEach
        void setUp() {

            given(pushRepository.save(push)).willReturn(push);
            service.updatePushToProcessing(push);
        }

        @Test
        void shouldSetSendStatusToProcessing() {
            then(push).should().setSendStatus(PushSendStatus.PROCESSING);
        }

        @Test
        void shouldSetSentStartDatetime() {
            then(push).should().setSentStartDatetime(any(LocalDateTime.class));
        }

        @Test
        void shouldCallPushRepository() {
            then(pushRepository).should().save(push);
        }
    }

    @Nested
    class UpdatePushToComplete {

        @Mock
        private Push push;

        @BeforeEach
        void setUp() {

            given(pushRepository.save(push)).willReturn(push);
            service.updatePushToComplete(push);
        }

        @Test
        void shouldSetSendStatusToProcessing() {
            then(push).should().setSendStatus(PushSendStatus.COMPLETE);
        }

        @Test
        void shouldSetSentEndDatetime() {
            then(push).should().setSentEndDatetime(any(LocalDateTime.class));
        }

        @Test
        void shouldCallPushRepository() {
            then(pushRepository).should().save(push);
        }
    }
}
