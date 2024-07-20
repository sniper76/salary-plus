package ag.act.service;

import ag.act.converter.stock.SolidarityResponseConverter;
import ag.act.entity.Solidarity;
import ag.act.model.SimpleStringResponse;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.stock.StockSolidarityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockSolidarityServiceTest {
    @InjectMocks
    private StockSolidarityService service;
    @Mock
    private SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    @Mock
    private SolidarityResponseConverter solidarityResponseConverter;
    @Mock
    private SolidarityService solidarityService;
    private String stockCode;

    @BeforeEach
    void setUp() {
        stockCode = someStockCode();
    }

    @Nested
    class WhenGetSolidarity {
        @Mock
        private ag.act.model.SolidarityResponse solidarityResponse;
        @Mock
        private Solidarity solidarity;

        @Test
        void shouldGetSolidarity() {
            // Given
            given(solidarityService.getSolidarityByStockCode(stockCode))
                .willReturn(solidarity);
            given(solidarityResponseConverter.convert(solidarity))
                .willReturn(solidarityResponse);

            // When
            ag.act.model.SolidarityDataResponse actual = service.getSolidarity(stockCode);

            // Then
            assertThat(actual.getData(), is(solidarityResponse));
            then(solidarityService).should().getSolidarityByStockCode(stockCode);
            then(solidarityResponseConverter).should().convert(solidarity);
        }
    }

    @Nested
    class WhenApplySolidarityLeader {
        @Test
        void shouldApplySolidarityLeader() {
            // When
            SimpleStringResponse actual = service.applySolidarityLeader(stockCode);

            // Then
            assertThat(actual.getStatus(), is("ok"));
            then(solidarityLeaderApplicantService).should().applyForLeader(stockCode);
        }
    }

    @Nested
    class WhenCancelSolidarityLeaderApplication {
        @Test
        void shouldCancelSolidarityLeaderApplication() {
            // When
            SimpleStringResponse actual = service.cancelSolidarityLeaderApplication(stockCode);

            // Then
            assertThat(actual.getStatus(), is("ok"));
            then(solidarityLeaderApplicantService).should().cancelLeaderApplication(stockCode);
        }
    }
}
