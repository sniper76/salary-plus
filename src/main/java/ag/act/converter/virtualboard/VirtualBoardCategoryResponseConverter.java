package ag.act.converter.virtualboard;

import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.model.BoardCategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VirtualBoardCategoryResponseConverter {
    public BoardCategoryDataArrayResponse convertToDataResponse(
        List<VirtualBoardCategory> virtualBoardCategoryList
    ) {
        return new BoardCategoryDataArrayResponse().data(
            virtualBoardCategoryList.stream()
                .map(this::convert)
                .toList()
        );
    }

    public BoardCategoryResponse convert(VirtualBoardCategory virtualBoardCategory) {
        return new BoardCategoryResponse()
            .name(virtualBoardCategory.name())
            .isExclusiveToHolders(virtualBoardCategory.supportExclusiveToHolders())
            .displayName(virtualBoardCategory.getDisplayName());
    }

}
