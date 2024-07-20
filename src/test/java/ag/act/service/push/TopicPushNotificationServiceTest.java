package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.Push;
import ag.act.enums.push.PushTopic;
import ag.act.service.FirebaseMessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class TopicPushNotificationServiceTest {

    @InjectMocks
    private TopicPushNotificationService service;
    @Mock
    private FirebaseMessagingService firebaseMessagingService;

    @Nested
    class SendPushNotification {

        @Mock
        private Push push;
        private String pushTitle;
        private String pushContent;
        private String linkUrl;
        private CreateFcmPushDataDto createFcmPushDataDto;

        @BeforeEach
        void setUp() {
            pushTitle = someString(10);
            linkUrl = someString(10);
            pushContent = someString(20);
            createFcmPushDataDto = CreateFcmPushDataDto.newInstance(pushTitle, pushContent, linkUrl);

            given(push.getLinkUrl()).willReturn(linkUrl);
            given(push.getTitle()).willReturn(pushTitle);
            given(push.getContent()).willReturn(pushContent);

            given(firebaseMessagingService.sendPushNotification(eq(createFcmPushDataDto), eq(PushTopic.NOTICE)))
                .willReturn(Boolean.TRUE);

            service.sendPushNotification(push);
        }

        @Test
        void shouldCallFirebaseMessagingServiceToSendNotification() {
            then(firebaseMessagingService)
                .should()
                .sendPushNotification(
                    eq(createFcmPushDataDto),
                    eq(PushTopic.NOTICE)
                );
        }
    }
}
