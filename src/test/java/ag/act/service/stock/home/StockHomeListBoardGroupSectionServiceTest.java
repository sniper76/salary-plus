package ag.act.service.stock.home;

import ag.act.converter.home.StockHomeSectionResponseConverter;
import ag.act.enums.BoardGroup;
import ag.act.model.StockHomeSectionResponse;
import ag.act.util.AppLinkUrlGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;


@MockitoSettings(strictness = Strictness.LENIENT)
class StockHomeListBoardGroupSectionServiceTest {
    @Spy
    private StockHomeSectionResponseConverter stockHomeSectionResponseConverter;
    @Mock
    private StockHomeListItemService stockHomeListItemService;
    @Mock
    private AppLinkUrlGenerator appLinkUrlGenerator;
    @Mock
    private List<ag.act.model.SectionItemResponse> listItems;

    @Test
    void assertStockHomeSectionResponse() {
        // Given
        String stockCode = someStockCode();
        BoardGroup boardGroup = someEnum(BoardGroup.class);
        String expectedUrl = someString(5);

        given(stockHomeListItemService.getListItems(stockCode, boardGroup)).willReturn(listItems);
        given(appLinkUrlGenerator.generateBoardGroupLinkUrl(stockCode, boardGroup)).willReturn(expectedUrl);

        StockHomeListBoardGroupSectionService service = new StockHomeListBoardGroupSectionService(
            List.of(boardGroup),
            stockHomeSectionResponseConverter,
            stockHomeListItemService,
            appLinkUrlGenerator
        );

        // When
        List<StockHomeSectionResponse> actualListResponse = service.getSections(stockCode);

        // Then
        assertThat(actualListResponse.size(), is(1));

        final StockHomeSectionResponse sectionResponse = actualListResponse.get(0);
        assertThat(sectionResponse.getType(), is("list"));
        assertThat(sectionResponse.getHeader().getTitle(), is(boardGroup.getDisplayName()));
        assertThat(sectionResponse.getHeader().getLink(), is(expectedUrl));
        assertThat(sectionResponse.getListItems(), is(listItems));
    }
}
