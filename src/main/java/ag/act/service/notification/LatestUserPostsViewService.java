package ag.act.service.notification;

import ag.act.dto.CreateLatestUserPostsViewDto;
import ag.act.entity.LatestUserPostsView;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.repository.LatestUserPostsViewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class LatestUserPostsViewService {
    private final LatestUserPostsViewRepository latestUserPostsViewRepository;

    @SuppressWarnings("UnusedReturnValue")
    public LatestUserPostsView createOrUpdate(
        CreateLatestUserPostsViewDto dto
    ) {
        LatestUserPostsView latestUserPostsView = getOrInitialize(dto);
        latestUserPostsView.setTimestamp(LocalDateTime.now());
        return getLatestUserPostsViewAfterSave(latestUserPostsView);
    }

    private LatestUserPostsView getLatestUserPostsViewAfterSave(LatestUserPostsView latestUserPostsView) {
        try {
            return latestUserPostsViewRepository.save(latestUserPostsView);
        } catch (DataIntegrityViolationException dive) {
            log.error(
                "latestUserPostsView 데이터 생성중 중복 오류가 발생하였습니다 : userId={} stockCode={} group={} category={} postViewType={}",
                latestUserPostsView.getUser().getId(),
                latestUserPostsView.getStock().getCode(),
                latestUserPostsView.getBoardGroup(),
                latestUserPostsView.getBoardCategory(),
                latestUserPostsView.getPostsViewType(),
                dive
            );
        }
        return latestUserPostsView;
    }

    private LatestUserPostsView getOrInitialize(CreateLatestUserPostsViewDto dto) {
        return get(dto)
            .orElse(initialize(dto));
    }

    private Optional<LatestUserPostsView> get(CreateLatestUserPostsViewDto dto) {
        Stock stock = dto.getStock();
        User user = dto.getUser();
        BoardGroup boardGroup = dto.getBoardGroup();
        BoardCategory boardCategory = dto.getBoardCategory();
        final PostsViewType postsViewType = dto.getPostsViewType();

        return latestUserPostsViewRepository.findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
            stock.getCode(),
            user.getId(),
            boardGroup,
            boardCategory,
            postsViewType
        );
    }

    private LatestUserPostsView initialize(
        CreateLatestUserPostsViewDto dto
    ) {
        LatestUserPostsView newLatestUserPostsView = new LatestUserPostsView();
        newLatestUserPostsView.setStock(dto.getStock());
        newLatestUserPostsView.setUser(dto.getUser());
        newLatestUserPostsView.setBoardGroup(dto.getBoardGroup());
        newLatestUserPostsView.setBoardCategory(dto.getBoardCategory());
        newLatestUserPostsView.setPostsViewType(dto.getPostsViewType());
        newLatestUserPostsView.setUniqueCombinedId(newLatestUserPostsView.getUniqueCombinedId());
        return newLatestUserPostsView;
    }
}
