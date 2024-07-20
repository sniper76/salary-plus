package ag.act.service;

import ag.act.dto.CreateLatestPostTimestampDto;
import ag.act.entity.LatestPostTimestamp;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.repository.LatestPostTimestampRepository;
import ag.act.service.notification.LatestPostTimestampService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class LatestPostTimestampServiceTest {
    @InjectMocks
    private LatestPostTimestampService latestPostTimestampService;
    @Mock
    private LatestPostTimestampRepository latestPostTimestampRepository;

    @Nested
    class WhenCreateOrUpdate {
        @Mock
        private CreateLatestPostTimestampDto createLatestPostTimestampDto;
        @Mock
        private Stock stock;
        @Mock
        private LatestPostTimestamp latestPostTimestamp;
        private final BoardGroup boardGroup = someBoardGroupForStock();
        private final BoardCategory boardCategory = someBoardCategory(boardGroup);

        @BeforeEach
        void setUp() {
            given(createLatestPostTimestampDto.getStock()).willReturn(stock);
            given(createLatestPostTimestampDto.getBoardGroup()).willReturn(boardGroup);
            given(createLatestPostTimestampDto.getBoardCategory()).willReturn(boardCategory);
        }

        @Nested
        class WhenAlreadyExist {
            @Test
            void shouldGet() {
                // Given
                given(stock.getCode()).willReturn(someString(5));
                given(latestPostTimestampRepository.findByStockCodeAndBoardGroupAndBoardCategory(
                    stock.getCode(),
                    boardGroup,
                    boardCategory
                )).willReturn(Optional.of(latestPostTimestamp));

                // When
                latestPostTimestampService.createOrUpdate(createLatestPostTimestampDto);

                // Then
                then(latestPostTimestamp).should().setTimestamp(any(LocalDateTime.class));
                then(latestPostTimestampRepository).should().save(latestPostTimestamp);
            }
        }

        @Nested
        class WhenNotExist {
            @Test
            void shouldInitialize() {
                // Given
                given(stock.getCode()).willReturn(someString(5));
                given(latestPostTimestampRepository.findByStockCodeAndBoardGroupAndBoardCategory(
                    stock.getCode(),
                    boardGroup,
                    boardCategory
                )).willReturn(Optional.empty());

                // ArgumentCaptor to capture the argument passed to save method
                ArgumentCaptor<LatestPostTimestamp> captor = ArgumentCaptor.forClass(LatestPostTimestamp.class);

                // When
                latestPostTimestampService.createOrUpdate(createLatestPostTimestampDto);

                // Then
                then(latestPostTimestampRepository).should().save(captor.capture());

                LatestPostTimestamp saved = captor.getValue();
                assertThat(saved.getStock(), is(stock));
                assertThat(saved.getBoardCategory(), is(boardCategory));
                assertThat(saved.getBoardGroup(), is(boardGroup));
                assertThat(saved.getTimestamp(), notNullValue());
            }
        }
    }
}
