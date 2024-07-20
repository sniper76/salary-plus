package ag.act.service;

import ag.act.converter.stock.MySolidarityResponseConverter;
import ag.act.dto.MySolidarityDto;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.model.MySolidarityResponse;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.modusign.DigitalProxyModuSignService;
import ag.act.service.solidarity.MySolidarityService;
import ag.act.service.stock.StockCodeService;
import ag.act.service.user.UserHoldingStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class MySolidarityServiceTest {
    @InjectMocks
    private MySolidarityService service;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private MySolidarityResponseConverter mySolidarityResponseConverter;
    @Mock
    private DigitalProxyModuSignService digitalProxyModuSignService;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private StockCodeService stockCodeService;
    @Mock
    private ActionLinkService actionLinkService;
    @Mock
    private User user;
    private Long userId;
    @Mock
    private List<MySolidarityDto> mySolidarityDtoList;
    @Mock
    private List<String> stockCodes;
    @Mock
    private Set<String> inProgressDigitalProxiesStockCodes;
    @Mock
    private Map<String, List<DigitalDocumentUser>> inProgressStockCodeDigitalDocumentsUsersMap;
    @Mock
    private List<MySolidarityDto> mySolidarityDtoListWithInProgressActionUserStatus;
    @Mock
    private List<MySolidarityResponse> mySolidarityResponses;

    @Nested
    class WhenGetSolidarityResponsesIncludingLinks {
        private List<MySolidarityResponse> actual;

        @BeforeEach
        void setUp() {
            // Given
            userId = someLong();
            given(user.getId()).willReturn(userId);
            given(userHoldingStockService.getTop20SortedMySolidarityList(userId))
                .willReturn(mySolidarityDtoList);
            given(stockCodeService.getStockCodes(mySolidarityDtoList))
                .willReturn(stockCodes);
            given(digitalProxyModuSignService.getInProgressDigitalProxiesStockCodes(stockCodes))
                .willReturn(inProgressDigitalProxiesStockCodes);
            given(digitalProxyModuSignService.getInProgressDigitalProxiesStockCodes(stockCodes))
                .willReturn(inProgressDigitalProxiesStockCodes);
            given(digitalDocumentService.getInProgressStockCodeDigitalDocumentsUsersMap(userId, stockCodes))
                .willReturn(inProgressStockCodeDigitalDocumentsUsersMap);
            given(actionLinkService.determineMySolidaritiesActionStatus(
                mySolidarityDtoList,
                inProgressDigitalProxiesStockCodes,
                inProgressStockCodeDigitalDocumentsUsersMap
            )).willReturn(mySolidarityDtoListWithInProgressActionUserStatus);
            given(mySolidarityResponseConverter.convert(any(MySolidarityDto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            actual = service.getSolidarityResponsesIncludingLinks(user);
        }

        @Test
        void shouldReturnSolidarityResponses() {
            assertThat(actual.size(), is(mySolidarityResponses.size()));
        }

        @Test
        void shouldGetTop20SortedMySolidarities() {
            then(userHoldingStockService).should().getTop20SortedMySolidarityList(userId);
        }

        @Test
        void shouldGetStockCodes() {
            then(stockCodeService).should().getStockCodes(mySolidarityDtoList);
        }

        @Test
        void shouldGetInProgressDigitalProxiesStockCodes() {
            then(digitalProxyModuSignService).should().getInProgressDigitalProxiesStockCodes(stockCodes);
        }

        @Test
        void shouldGetInProgressStockCodeDigitalDocumentsUsersMap() {
            then(digitalDocumentService).should().getInProgressStockCodeDigitalDocumentsUsersMap(userId, stockCodes);
        }

        @Test
        void shouldDetermineMySolidaritiesActionStatus() {
            then(actionLinkService).should().determineMySolidaritiesActionStatus(
                mySolidarityDtoList,
                inProgressDigitalProxiesStockCodes,
                inProgressStockCodeDigitalDocumentsUsersMap
            );
        }
    }
}
