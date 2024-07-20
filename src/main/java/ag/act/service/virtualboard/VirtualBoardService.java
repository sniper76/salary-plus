package ag.act.service.virtualboard;

import ag.act.converter.virtualboard.VirtualBoardCategoryResponseConverter;
import ag.act.enums.BoardCategory;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VirtualBoardService {
    private final VirtualBoardCategoryResponseConverter virtualBoardCategoryResponseConverter;

    public ag.act.model.BoardCategoryDataArrayResponse getVirtualBoardGroupCategories(VirtualBoardGroup virtualBoardGroup) {
        final List<VirtualBoardCategory> virtualBoardCategories = virtualBoardGroup.getVirtualCategories();

        return virtualBoardCategoryResponseConverter.convertToDataResponse(virtualBoardCategories);
    }

    public List<BoardCategory> getAllCategoriesUnderVirtualBoardGroup(VirtualBoardGroup virtualBoardGroup) {
        return virtualBoardGroup.getVirtualCategories()
            .stream()
            .map(VirtualBoardCategory::getSubCategories)
            .flatMap(List::stream)
            .toList();
    }
}
