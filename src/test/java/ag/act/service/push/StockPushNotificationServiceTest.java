package ag.act.service.push;

import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.Push;
import ag.act.entity.Stock;
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

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockPushNotificationServiceTest {

    @InjectMocks
    private StockPushNotificationService service;
    @Mock
    private FirebaseMessagingService firebaseMessagingService;
    @Mock
    private UserService userService;

    @Nested
    class SendPushNotification {

        @Mock
        private Push push;
        @Mock
        private Stock stock;
        private String pushContent;
        private String stockCode;
        @Mock
        private List<String> tokens;
        private String linkUrl;
        private String title;
        private CreateFcmPushDataDto createFcmPushDataDto;

        @BeforeEach
        void setUp() {
            final String stockName = someString(10);
            final String content = someString(20);
            title = someAlphanumericString(5);
            pushContent = "[%s] %s".formatted(stockName, content);
            stockCode = someStockCode();

            linkUrl = someAlphanumericString(10);

            createFcmPushDataDto = CreateFcmPushDataDto.newInstance(title, pushContent, linkUrl);

            given(push.getTitle()).willReturn(title);
            given(push.getLinkUrl()).willReturn(linkUrl);
            given(push.getPushTargetType()).willReturn(PushTargetType.STOCK);
            given(push.getContent()).willReturn(content);
            given(push.getStock()).willReturn(stock);
            given(push.getStockCode()).willReturn(stockCode);
            given(stock.getName()).willReturn(stockName);

            given(userService.getPushTokens(stockCode)).willReturn(tokens);
            given(firebaseMessagingService.sendPushNotification(
                    eq(createFcmPushDataDto),
                    eq(tokens)
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
                    eq(tokens)
                );
        }

        @Test
        void shouldCallUserServiceToGetPushTokens() {
            then(userService).should().getPushTokens(stockCode);
        }

    }
}
