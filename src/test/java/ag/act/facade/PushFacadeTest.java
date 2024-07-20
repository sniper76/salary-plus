package ag.act.facade;

import ag.act.converter.push.PushResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.push.PushSearchDto;
import ag.act.entity.Push;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.NotFoundException;
import ag.act.model.PushDetailsResponse;
import ag.act.service.push.PushNotificationService;
import ag.act.service.push.PushNotificationServiceResolver;
import ag.act.service.push.PushService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shiver.me.timbers.data.random.RandomStrings;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.somePage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class PushFacadeTest {

    @InjectMocks
    private PushFacade facade;
    @Mock
    private PushService pushService;
    @Mock
    private PushResponseConverter pushResponseConverter;
    @Mock
    private PushNotificationServiceResolver pushNotificationServiceResolver;

    @Nested
    class GetPushListItems {
        @Mock
        private PushSearchDto pushSearchDto;
        @Mock
        private Pageable pageable;
        @Mock
        private Page<Push> pushPage;
        @SuppressWarnings("rawtypes")
        private Page mappedPushPage;
        private SimplePageDto<PushDetailsResponse> actualResponse;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void setUp() {
            mappedPushPage = somePage(List.<Push>of());

            given(pushService.getPushList(pushSearchDto, pageable)).willReturn(pushPage);
            given(pushPage.getContent()).willReturn(List.of());
            given(pushPage.map(any())).willReturn(mappedPushPage);
            given(pushResponseConverter.convert(any(Push.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            actualResponse = facade.getPushListItems(pushSearchDto, pageable);
        }

        @Test
        void shouldReturnPushListItemResponse() {
            assertThat(actualResponse.getContent(), is(mappedPushPage.getContent()));
        }

        @Test
        void shouldCallPushServiceToGetPushList() {
            then(pushService).should().getPushList(pushSearchDto, pageable);
        }
    }

    @Nested
    class CreatePush {
        @Mock
        private ag.act.model.CreatePushRequest createPushRequest;
        @Mock
        private Push push;
        @Mock
        private PushDetailsResponse pushDetailsResponse;
        private PushDetailsResponse actualResult;

        @BeforeEach
        void setUp() {
            given(pushService.createPush(createPushRequest)).willReturn(push);
            given(pushResponseConverter.convert(push)).willReturn(pushDetailsResponse);

            actualResult = facade.createPush(createPushRequest);
        }

        @Test
        void shouldReturnPushDetailsResponse() {
            assertThat(actualResult, is(pushDetailsResponse));
        }

        @Test
        void shouldCallPushServiceToCreatePush() {
            then(pushService).should().createPush(createPushRequest);
        }

        @Test
        void shouldCallPushResponseConverterToConvertPush() {
            then(pushResponseConverter).should().convert(push);
        }
    }

    @Nested
    class GetPushDetails {
        @Mock
        private Push push;
        private Long pushId;
        @Mock
        private PushDetailsResponse pushDetailsResponse;

        @BeforeEach
        void setUp() {
            pushId = someLong();
        }

        @Nested
        class WhenFound {
            @Test
            void shouldReturnPush() {
                // Given
                given(pushService.findPush(pushId)).willReturn(Optional.of(push));
                given(pushResponseConverter.convert(push)).willReturn(pushDetailsResponse);

                // When
                PushDetailsResponse actual = facade.getPushDetails(pushId);

                // Then
                assertThat(actual, is(pushDetailsResponse));
                then(pushService).should().findPush(pushId);
                then(pushResponseConverter).should().convert(push);
            }

        }

        @Nested
        class WhenNotFound {
            @Test
            void shouldThrowNotFoundException() {
                // Given
                given(pushService.findPush(pushId)).willReturn(Optional.empty());

                // When // Then
                assertException(
                    NotFoundException.class,
                    () -> facade.getPushDetails(pushId),
                    "해당 푸시를 찾을 수 없습니다."
                );
            }

        }
    }

    @Nested
    class SendPush {

        @Mock
        private PushNotificationService pushNotificationService;

        @Mock
        private Push push;

        @BeforeEach
        void setUp() {
            String linkUrl = RandomStrings.someAlphanumericString(10);

            given(pushService.updatePushToProcessing(push)).willReturn(push);
            given(pushService.updatePushToComplete(push)).willReturn(push);
            given(push.getLinkUrl()).willReturn(linkUrl);
        }

        @Nested
        class WhenStockTargetTypeIsAll {

            @BeforeEach
            void setUp() {
                final PushTargetType stockTargetType = PushTargetType.ALL;

                given(push.getPushTargetType()).willReturn(stockTargetType);
                given(pushNotificationServiceResolver.resolve(stockTargetType)).willReturn(pushNotificationService);

                facade.sendPush(push);
            }

            @Test
            void shouldCallFirebaseMessagingServiceToSendNotification() {
                then(pushNotificationService)
                    .should()
                    .sendPushNotification(push);
            }

            @Test
            void shouldCallPushServiceToUpdatePushToProcessing() {
                then(pushService).should().updatePushToProcessing(push);
            }

            @Test
            void shouldCallPushServiceToUpdatePushToComplete() {
                then(pushService).should().updatePushToComplete(push);
            }

        }

        @Nested
        class WhenStockTargetTypeIsStock {

            @BeforeEach
            void setUp() {
                final PushTargetType stockTargetType = PushTargetType.STOCK;

                given(push.getPushTargetType()).willReturn(stockTargetType);
                given(pushNotificationServiceResolver.resolve(stockTargetType)).willReturn(pushNotificationService);

                facade.sendPush(push);
            }

            @Test
            void shouldCallFirebaseMessagingServiceToSendNotification() {
                then(pushNotificationService)
                    .should()
                    .sendPushNotification(push);
            }

            @Test
            void shouldCallPushServiceToUpdatePushToProcessing() {
                then(pushService).should().updatePushToProcessing(push);
            }

            @Test
            void shouldCallPushServiceToUpdatePushToComplete() {
                then(pushService).should().updatePushToComplete(push);
            }

        }

        @Nested
        class WhenStockTargetTypeIsStockGroup {

            @BeforeEach
            void setUp() {
                final PushTargetType stockTargetType = PushTargetType.STOCK_GROUP;

                given(push.getPushTargetType()).willReturn(stockTargetType);
                given(pushNotificationServiceResolver.resolve(stockTargetType)).willReturn(pushNotificationService);

                facade.sendPush(push);
            }

            @Test
            void shouldCallFirebaseMessagingServiceToSendNotification() {
                then(pushNotificationService)
                    .should()
                    .sendPushNotification(push);
            }

            @Test
            void shouldCallPushServiceToUpdatePushToProcessing() {
                then(pushService).should().updatePushToProcessing(push);
            }

            @Test
            void shouldCallPushServiceToUpdatePushToComplete() {
                then(pushService).should().updatePushToComplete(push);
            }
        }
    }
}
