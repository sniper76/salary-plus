package ag.act.facade;

import ag.act.converter.stock.SolidarityResponseConverter;
import ag.act.entity.Solidarity;
import ag.act.facade.solidarity.SolidarityFacade;
import ag.act.model.SolidarityDataResponse;
import ag.act.model.Status;
import ag.act.service.solidarity.SolidarityService;
import ag.act.validator.solidarity.SolidarityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityFacadeTest {
    @InjectMocks
    private SolidarityFacade facade;
    @Mock
    private SolidarityService solidarityService;
    @Mock
    private SolidarityValidator solidarityValidator;
    @Mock
    private SolidarityResponseConverter solidarityResponseConverter;

    @Nested
    class WhenUpdateSolidarityToActive {
        private Long solidarityId;
        @Mock
        private Solidarity solidarity;
        @Mock
        private Solidarity savedSolidarity;
        @Mock
        private ag.act.model.SolidarityResponse solidarityResponse;

        @BeforeEach
        void setUp() {
            solidarityId = somePositiveLong();
        }

        @Test
        void shouldUpdateSolidarityToActive() {

            // Given
            given(solidarityService.getSolidarity(solidarityId))
                .willReturn(solidarity);
            willDoNothing().given(solidarityValidator).validateUpdateSolidarityToActive(solidarity);
            given(solidarityService.saveSolidarity(solidarity))
                .willReturn(savedSolidarity);
            given(solidarityResponseConverter.convert(savedSolidarity))
                .willReturn(solidarityResponse);

            // When
            SolidarityDataResponse actual = facade.updateSolidarityToActive(solidarityId);

            // Then
            assertThat(actual.getData(), is(solidarityResponse));

            then(solidarityValidator).should().validateUpdateSolidarityToActive(solidarity);
            then(solidarity).should().setStatus(Status.ACTIVE);
            then(solidarityService).should().saveSolidarity(solidarity);
            then(solidarityResponseConverter).should().convert(savedSolidarity);
        }
    }
}
