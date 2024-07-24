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
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class LatestUserPostsViewService {
    private final LatestUserPostsViewRepository latestUserPostsViewRepository;

    public void createOrUpdate(
        CreateLatestUserPostsViewDto requestDto
    ) {
        final LocalDateTime currentDateTime = LocalDateTime.now();
        LatestUserPostsView latestUserPostsView = getOrInitialize(requestDto);
        latestUserPostsView.setTimestamp(currentDateTime);

        try {
            save(latestUserPostsView);
        } catch (DataIntegrityViolationException dive) {
            updateLatestUserPostsViewWhenError(
                requestDto,
                currentDateTime,
                new LatestUserPostsViewError(latestUserPostsView, dive)
            );
        }
    }

    record LatestUserPostsViewError(LatestUserPostsView latestUserPostsView, DataIntegrityViolationException exception) {
    }

    private void updateLatestUserPostsViewWhenError(
        CreateLatestUserPostsViewDto requestDto,
        LocalDateTime currentDateTime,
        LatestUserPostsViewError error
    ) {
        final Optional<LatestUserPostsView> latestUserPostsViewOptional = get(requestDto);

        if (latestUserPostsViewOptional.isPresent()) {
            final LatestUserPostsView latestUserPostsView = latestUserPostsViewOptional.get();
            latestUserPostsView.setTimestamp(currentDateTime);
            save(latestUserPostsView);
        } else {
            errorLog(error);
        }
    }

    private LatestUserPostsView save(LatestUserPostsView latestUserPostsView) {
        return latestUserPostsViewRepository.save(latestUserPostsView);
    }

    private void errorLog(LatestUserPostsViewError latestUserPostsViewError) {
        final LatestUserPostsView latestUserPostsView = latestUserPostsViewError.latestUserPostsView();
        log.warn(
            "latestUserPostsView 데이터 생성중 중복 오류가 발생하였습니다 : userId={} stockCode={} group={} category={} postViewType={}, timestamp={}",
            latestUserPostsView.getUser().getId(),
            latestUserPostsView.getStock().getCode(),
            latestUserPostsView.getBoardGroup(),
            latestUserPostsView.getBoardCategory(),
            latestUserPostsView.getPostsViewType(),
            latestUserPostsView.getTimestamp(),
            latestUserPostsViewError.exception()
        );
    }

    private LatestUserPostsView getOrInitialize(CreateLatestUserPostsViewDto requestDto) {
        return get(requestDto)
            .orElse(initialize(requestDto));
    }

    private Optional<LatestUserPostsView> get(CreateLatestUserPostsViewDto requestDto) {
        final Stock stock = requestDto.getStock();
        final User user = requestDto.getUser();
        final BoardGroup boardGroup = requestDto.getBoardGroup();
        final BoardCategory boardCategory = requestDto.getBoardCategory();
        final PostsViewType postsViewType = requestDto.getPostsViewType();

        return latestUserPostsViewRepository.findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
            stock.getCode(),
            user.getId(),
            boardGroup,
            boardCategory,
            postsViewType
        );
    }

    private LatestUserPostsView initialize(
        CreateLatestUserPostsViewDto requestDto
    ) {
        LatestUserPostsView newLatestUserPostsView = new LatestUserPostsView();
        newLatestUserPostsView.setStock(requestDto.getStock());
        newLatestUserPostsView.setUser(requestDto.getUser());
        newLatestUserPostsView.setBoardGroup(requestDto.getBoardGroup());
        newLatestUserPostsView.setBoardCategory(requestDto.getBoardCategory());
        newLatestUserPostsView.setPostsViewType(requestDto.getPostsViewType());
        return newLatestUserPostsView;
    }
}
