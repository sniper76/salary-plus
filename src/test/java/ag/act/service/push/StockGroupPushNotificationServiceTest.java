package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.Push;
import ag.act.entity.StockGroup;
import ag.act.enums.push.PushTargetType;
import ag.act.service.FirebaseMessagingService;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockGroupPushNotificationServiceTest {

    @InjectMocks
    private StockGroupPushNotificationService service;
    @Mock
    private FirebaseMessagingService firebaseMessagingService;
    @Mock
    private UserService userService;

    @Nested
    class SendPushNotification {

        @Mock
        private Push push;
        @Mock
        private StockGroup stockGroup;
        private String pushTitle;
        private String pushContent;
        private String linkUrl;
        private Long stockGroupId;
        private Set<String> tokens;
        private CreateFcmPushDataDto createFcmPushDataDto;

        @BeforeEach
        void setUp() {
            pushTitle = someString(10);
            linkUrl = someString(10);
            pushContent = someString(20);
            stockGroupId = someLong();
            tokens = new LinkedHashSet<>(
                List.of(someString(10), someString(10))
            );

            createFcmPushDataDto = CreateFcmPushDataDto.newInstance(pushTitle, pushContent, linkUrl);

            given(push.getTitle()).willReturn(pushTitle);
            given(push.getLinkUrl()).willReturn(linkUrl);
            given(push.getContent()).willReturn(pushContent);

            given(push.getPushTargetType()).willReturn(PushTargetType.STOCK_GROUP);
            given(push.getContent()).willReturn(pushContent);
            given(push.getStockGroup()).willReturn(stockGroup);
            given(push.getStockGroupId()).willReturn(stockGroupId);

            given(userService.getPushTokensByStockGroupId(stockGroupId)).willReturn(tokens);
            given(firebaseMessagingService.sendPushNotification(
                    eq(createFcmPushDataDto),
                    eq(new ArrayList<>(tokens))
                )
            ).willReturn(Boolean.TRUE);

            service.sendPushNotification(push);
        }

        @Test
        void shouldCallFirebaseMessagingServiceToSendNotification() {
            then(firebaseMessagingService)
                .should()
                .sendPushNotification(
                    eq(createFcmPushDataDto),
                    eq(new ArrayList<>(tokens))
                );
        }

        @Test
        void shouldCallUserServiceToGetPushTokensByStockGroupId() {
            then(userService).should().getPushTokensByStockGroupId(stockGroupId);
        }

    }
}
