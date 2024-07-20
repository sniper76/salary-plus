package ag.act.service.push;

import ag.act.enums.push.PushTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
class PushNotificationServiceResolverTest {

    private PushNotificationServiceResolver resolver;

    @Mock
    private TopicPushNotificationService topicPushNotificationService;
    @Mock
    private StockPushNotificationService stockPushNotificationService;
    @Mock
    private StockGroupPushNotificationService stockGroupPushNotificationService;
    @Mock
    private DefaultPushNotificationService defaultPushNotificationService;

    @BeforeEach
    void setUp() {

        given(topicPushNotificationService.getSupportPushTargetType()).willReturn(PushTargetType.ALL);
        given(stockPushNotificationService.getSupportPushTargetType()).willReturn(PushTargetType.STOCK);
        given(stockGroupPushNotificationService.getSupportPushTargetType()).willReturn(PushTargetType.STOCK_GROUP);
        given(defaultPushNotificationService.getSupportPushTargetType()).willReturn(PushTargetType.UNKNOWN);

        List<PushNotificationService> pushNotificationServices = List.of(
            topicPushNotificationService,
            stockPushNotificationService,
            stockGroupPushNotificationService,
            defaultPushNotificationService
        );

        resolver = new PushNotificationServiceResolver(
            pushNotificationServices,
            defaultPushNotificationService
        );
        resolver.init();
    }

    @ParameterizedTest(name = "{index} => inputDate=''{0}''")
    @MethodSource("valueProvider")
    void shouldReturnCorrectPushNotificationService(PushTargetType stockTargetType, Class<PushNotificationService> serviceClassType) {

        // When
        final PushNotificationService actualService = resolver.resolve(stockTargetType);

        // Then
        assertThat(actualService, instanceOf(serviceClassType));
    }

    private static Stream<Arguments> valueProvider() {
        return Stream.of(
            Arguments.of(PushTargetType.ALL, TopicPushNotificationService.class),
            Arguments.of(PushTargetType.STOCK, StockPushNotificationService.class),
            Arguments.of(PushTargetType.STOCK_GROUP, StockGroupPushNotificationService.class),
            Arguments.of(PushTargetType.UNKNOWN, DefaultPushNotificationService.class),
            Arguments.of(null, DefaultPushNotificationService.class)
        );
    }
}
