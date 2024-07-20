package ag.act.service.stock.home;

import ag.act.converter.home.SectionItemResponseConverter;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.model.SectionItemResponse;
import ag.act.service.post.MostRecentThreePostQueryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomThings.someThing;


@MockitoSettings(strictness = Strictness.LENIENT)
class StockHomeListItemServiceTest {
    @InjectMocks
    private StockHomeListItemService service;
    @Mock
    private MostRecentThreePostQueryService mostRecentThreePostQueryService;
    @Mock
    private Post post1;
    @Mock
    private Post post2;
    @Mock
    private SectionItemResponseConverter sectionItemResponseConverter;
    @Mock
    private ag.act.model.SectionItemResponse sectionItemResponse1;
    @Mock
    private ag.act.model.SectionItemResponse sectionItemResponse2;


    @Test
    void shouldGenerateListItems() {

        // Given
        final String stockCode = someStockCode();
        final BoardCategory boardCategory = someThing(BoardCategory.activeBoardCategoriesForStocks());
        given(mostRecentThreePostQueryService.getPostsByStockCodeAndBoardCategory(stockCode, boardCategory))
            .willReturn(List.of(post1, post2));
        given(sectionItemResponseConverter.convert(post1))
            .willReturn(sectionItemResponse1);
        given(sectionItemResponseConverter.convert(post2))
            .willReturn(sectionItemResponse2);


        // When
        final List<SectionItemResponse> actual = service.getListItems(stockCode, boardCategory);

        // Then
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0), is(sectionItemResponse1));
        assertThat(actual.get(1), is(sectionItemResponse2));
        then(mostRecentThreePostQueryService).should().getPostsByStockCodeAndBoardCategory(stockCode, boardCategory);
        then(sectionItemResponseConverter).should().convert(post1);
        then(sectionItemResponseConverter).should().convert(post2);
    }
}






