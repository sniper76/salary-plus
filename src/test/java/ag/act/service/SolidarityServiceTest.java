package ag.act.service;

import ag.act.entity.Solidarity;
import ag.act.repository.SolidarityRepository;
import ag.act.service.solidarity.SolidarityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class SolidarityServiceTest {
    @InjectMocks
    private SolidarityService service;
    @Mock
    private SolidarityRepository solidarityRepository;
    @Mock
    private Solidarity solidarity;
    private String stockCode;

    @Nested
    class WhenGetSolidarity {
        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
        }

        @Test
        void shouldGetSolidarity() {
            // Given
            given(solidarityRepository.findByStockCode(stockCode))
                .willReturn(Optional.of(solidarity));

            // When
            Optional<Solidarity> actual = service.findSolidarity(stockCode);

            // Then
            assertThat(actual.isPresent(), is(true));
            assertThat(actual.get(), is(solidarity));
            then(solidarityRepository).should().findByStockCode(stockCode);
        }

        @Test
        void shouldReturnNull() {
            // Given
            given(solidarityRepository.findByStockCode(stockCode))
                .willReturn(Optional.empty());

            // When
            Optional<Solidarity> actual = service.findSolidarity(stockCode);

            // Then
            assertThat(actual.isEmpty(), is(true));
            then(solidarityRepository).should().findByStockCode(stockCode);
        }
    }
}
