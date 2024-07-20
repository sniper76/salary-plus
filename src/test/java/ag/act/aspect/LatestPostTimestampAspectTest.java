package ag.act.aspect;


import ag.act.core.aop.LatestPostTimestampAspect;
import ag.act.dto.CreateLatestPostTimestampDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.service.notification.LatestPostTimestampService;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.TestUtil.someStockCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class LatestPostTimestampAspectTest {
    @InjectMocks
    private LatestPostTimestampAspect latestPostTimestampAspect;
    @Mock
    private StockService stockService;
    @Mock
    private LatestPostTimestampService latestPostTimestampService;

    @Nested
    class CreateLatestPostTimestamp {
        @Mock
        private CreatePostRequestDto createPostRequestDto;
        @Mock
        private ag.act.model.CreatePostRequest createPostRequest;
        @Mock
        private Stock stock;
        private BoardGroup boardGroup;

        @BeforeEach
        void setUp() {
            // Given
            String stockCode = someStockCode();
            boardGroup = someBoardGroupForStock();
            given(createPostRequestDto.getStockCode()).willReturn(stockCode);
            given(createPostRequestDto.getBoardGroupName()).willReturn(boardGroup.name());
            given(createPostRequestDto.getCreatePostRequest()).willReturn(createPostRequest);
            given(stockService.getStock(stockCode)).willReturn(stock);
        }

        @Test
        void whenInvalidBoardCategoryName() {
            // Given
            given(createPostRequest.getBoardCategory()).willReturn(someString(10));

            // When
            latestPostTimestampAspect.createLatestPostTimestamp(createPostRequestDto);

            // Then
            then(latestPostTimestampService).should(never()).createOrUpdate(any(CreateLatestPostTimestampDto.class));
        }

        @Test
        void whenSuccess() {
            // Given
            BoardCategory boardCategory = someBoardCategory(boardGroup);
            given(createPostRequest.getBoardCategory()).willReturn(boardCategory.name());

            // When
            latestPostTimestampAspect.createLatestPostTimestamp(createPostRequestDto);

            // Then
            then(latestPostTimestampService).should().createOrUpdate(
                CreateLatestPostTimestampDto.builder()
                    .stock(stock)
                    .boardGroup(boardGroup)
                    .boardCategory(boardCategory)
                    .build()
            );

        }
    }
}
