package ag.act.service;

import ag.act.dto.MySolidarityDto;
import ag.act.dto.mysolidarity.InProgressActionUserStatus;
import ag.act.entity.Stock;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static ag.act.dto.mysolidarity.InProgressActionUserStatus.ALL_COMPLETED;
import static ag.act.dto.mysolidarity.InProgressActionUserStatus.AT_LEAST_ONE_REMAINED;
import static ag.act.dto.mysolidarity.InProgressActionUserStatus.NONE;
import static ag.act.enums.DigitalDocumentAnswerStatus.COMPLETE;
import static ag.act.enums.DigitalDocumentAnswerStatus.SAVE;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class ActionLinkServiceTest {
    @InjectMocks
    private ActionLinkService service;
    @Mock
    private MySolidarityDto mySolidarityDto;
    @Mock
    private Stock stock;
    private static final String stockCode = someNumericString(6);
    private static final DigitalDocumentUser digitalDocumentUserWithCompletedStatus1 = mock(DigitalDocumentUser.class);
    private static final DigitalDocumentUser digitalDocumentUserWithCompletedStatus2 = mock(DigitalDocumentUser.class);
    private static final DigitalDocumentUser digitalDocumentUserWithSavedStatus = mock(DigitalDocumentUser.class);

    private static Stream<Arguments> mySolidaritiesActionStatusProvider() {
        return Stream.of(
            Arguments.of(Set.of(), Map.of(), NONE),
            Arguments.of(Set.of(stockCode), Map.of(), AT_LEAST_ONE_REMAINED),
            Arguments.of(
                Set.of(),
                Map.of(stockCode, Arrays.asList(digitalDocumentUserWithCompletedStatus1, digitalDocumentUserWithCompletedStatus2)),
                ALL_COMPLETED
            ),
            Arguments.of(
                Set.of(),
                Map.of(stockCode, Arrays.asList(null, digitalDocumentUserWithCompletedStatus2)),
                AT_LEAST_ONE_REMAINED
            ),
            Arguments.of(
                Set.of(),
                Map.of(stockCode, Arrays.asList(digitalDocumentUserWithCompletedStatus1, digitalDocumentUserWithSavedStatus)),
                AT_LEAST_ONE_REMAINED
            )
        );
    }

    @BeforeEach
    void setUp() {
        given(mySolidarityDto.getStock()).willReturn(stock);
        given(stock.getCode()).willReturn(stockCode);
        given(digitalDocumentUserWithCompletedStatus1.getDigitalDocumentAnswerStatus())
            .willReturn(COMPLETE);
        given(digitalDocumentUserWithCompletedStatus2.getDigitalDocumentAnswerStatus())
            .willReturn(COMPLETE);
        given(digitalDocumentUserWithSavedStatus.getDigitalDocumentAnswerStatus())
            .willReturn(SAVE);
    }

    @ParameterizedTest
    @MethodSource("mySolidaritiesActionStatusProvider")
    void shouldSetInProgressActionUserStatus(
        Set<String> inProgressDigitalProxiesStockCodes,
        Map<String, List<DigitalDocumentUser>> inProgressStockCodeDigitalDocumentsUsersMap,
        Object expected
    ) {
        service.determineMySolidaritiesActionStatus(
            List.of(mySolidarityDto), inProgressDigitalProxiesStockCodes, inProgressStockCodeDigitalDocumentsUsersMap
        );
        then(mySolidarityDto).should().setInProgressActionUserStatus((InProgressActionUserStatus) expected);
    }
}