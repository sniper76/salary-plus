package ag.act.handler.stockboardgrouppost;

import ag.act.converter.PageDataConverter;
import ag.act.model.GetBoardGroupPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StockBoardGroupPostCustomSortWrapper {
    private static final String CUSTOM_SORT_COLUMN = "isPinned";
    private static final String IS_PINNED_SORT = "%s:DESC".formatted(CUSTOM_SORT_COLUMN);
    private final PageDataConverter pageDataConverter;

    public GetBoardGroupPostResponse runWith(
        Integer page,
        Integer size,
        List<String> sorts,
        Function<PageRequest, GetBoardGroupPostResponse> function
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, getSortsWithPinned(sorts));
        final GetBoardGroupPostResponse response = function.apply(pageRequest);
        final List<String> originalSorts = getOriginalSorts(response);

        response.getPaging().setSorts(originalSorts);

        return response;
    }

    private List<String> getOriginalSorts(ag.act.model.GetBoardGroupPostResponse response) {
        return response.getPaging()
            .getSorts()
            .stream()
            .filter(sort -> !sort.startsWith(CUSTOM_SORT_COLUMN))
            .toList();
    }

    private List<String> getSortsWithPinned(List<String> sorts) {
        return Stream.concat(
            Stream.of(IS_PINNED_SORT),
            sorts.stream()
        ).toList();
    }
}
