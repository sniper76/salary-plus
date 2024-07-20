package ag.act.converter.stock;

import ag.act.entity.StockGroup;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockGroupRequestConverterTest {
    @InjectMocks
    private StockGroupRequestConverter converter;

    @Nested
    class Map {

        @Mock
        private ag.act.model.UpdateStockGroupRequest updateStockGroupRequest;
        @Mock
        private StockGroup stockGroup;

        @Nested
        class Name {
            @Nested
            class WhenNameIsBlank {
                @Test
                void shouldNotUpdateStockName() {

                    // Given
                    given(updateStockGroupRequest.getName()).willReturn("");

                    // When
                    converter.map(updateStockGroupRequest, stockGroup);

                    // Then
                    then(stockGroup).should(never()).setName(anyString());
                }
            }

            @Nested
            class WhenNameIsNotBlank {
                @Test
                void shouldUpdateStockName() {

                    // Given
                    final String name = someAlphanumericString(10);
                    given(updateStockGroupRequest.getName()).willReturn(name);

                    // When
                    converter.map(updateStockGroupRequest, stockGroup);

                    // Then
                    then(stockGroup).should().setName(name);
                }
            }
        }

        @Nested
        class Description {
            @Nested
            class WhenDescriptionIsBlank {
                @Test
                void shouldUpdateDescription() {

                    // Given
                    given(updateStockGroupRequest.getDescription()).willReturn("");

                    // When
                    converter.map(updateStockGroupRequest, stockGroup);

                    // Then
                    then(stockGroup).should().setDescription("");
                }
            }

            @Nested
            class WhenDescriptionIsNull {
                @Test
                void shouldUpdateDescription() {

                    // Given
                    given(updateStockGroupRequest.getDescription()).willReturn(null);

                    // When
                    converter.map(updateStockGroupRequest, stockGroup);

                    // Then
                    then(stockGroup).should().setDescription(null);
                }
            }
        }
    }
}