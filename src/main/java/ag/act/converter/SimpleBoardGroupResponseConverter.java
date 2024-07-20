package ag.act.converter;

import ag.act.converter.post.BoardCategoryResponseConverter;
import ag.act.enums.BoardGroup;
import ag.act.model.SimpleBoardGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleBoardGroupResponseConverter implements Converter<BoardGroup, SimpleBoardGroupResponse> {

    private final BoardCategoryResponseConverter boardCategoryResponseConverter;

    private ag.act.model.SimpleBoardGroupResponse convert(BoardGroup boardGroup) {
        return new SimpleBoardGroupResponse()
            .name(boardGroup.name())
            .displayName(boardGroup.getDisplayName())
            .categories(boardGroup.getCategories().stream()
                .map(boardCategoryResponseConverter::convert).toList()
            );
    }

    @Override
    public SimpleBoardGroupResponse apply(BoardGroup boardGroup) {
        return convert(boardGroup);
    }
}
