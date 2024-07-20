package ag.act.service.virtualboard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.Status;
import ag.act.service.post.BestPostService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VirtualBoardGroupPostService {
    private final BlockedUserService blockedUserService;
    private final BestPostService bestPostService;
    private final VirtualBoardService virtualBoardService;
    private final UserHoldingStockService userHoldingStockService;

    public Page<Post> getBestPosts(GetBoardGroupPostDto getBoardGroupPostDto, PageRequest pageRequest) {

        final List<BoardCategory> categories = getTargetCategories(getBoardGroupPostDto);
        final List<Long> blockedUserIdList = blockedUserService.getBlockUserIdListOfMine();
        final List<Status> statuses = StatusUtil.getStatusesForPostList(getBoardGroupPostDto.getIsNotDeleted());

        if (getBoardGroupPostDto.isExclusiveToHolders()) {
            return bestPostService.getBestPostsForOnlyExclusiveToHolders(
                categories,
                statuses,
                getStockHoldingStockCodes(),
                blockedUserIdList,
                pageRequest
            );
        }

        if (getBoardGroupPostDto.getIsExclusiveToPublic()) {
            return bestPostService.getBestPostsForOnlyExclusiveToPublic(
                categories,
                statuses,
                blockedUserIdList,
                pageRequest
            );
        }

        return bestPostService.getBestPosts(
            categories,
            statuses,
            blockedUserIdList,
            pageRequest
        );
    }

    private List<String> getStockHoldingStockCodes() {
        return userHoldingStockService.getAllUserHoldingStockCodes(ActUserProvider.getNoneNull().getId());
    }

    private List<BoardCategory> getTargetCategories(GetBoardGroupPostDto getBoardGroupPostDto) {
        List<BoardCategory> categoryList = VirtualBoardCategory.getBoardCategories(getBoardGroupPostDto.getBoardCategoryNames());

        if (!categoryList.isEmpty()) {
            return categoryList;
        }

        return getTargetCategoriesByVirtualBoardGroup(getBoardGroupPostDto.getBoardGroupName());
    }

    private List<BoardCategory> getTargetCategoriesByVirtualBoardGroup(String boardGroupName) {
        VirtualBoardGroup virtualBoardGroup = VirtualBoardGroup.fromValue(boardGroupName);
        return virtualBoardService.getAllCategoriesUnderVirtualBoardGroup(virtualBoardGroup);
    }
}
