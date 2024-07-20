package ag.act.service.notification;

import ag.act.dto.CreateLatestPostTimestampDto;
import ag.act.entity.LatestPostTimestamp;
import ag.act.repository.LatestPostTimestampRepository;
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
public class LatestPostTimestampService {
    private final LatestPostTimestampRepository latestPostTimestampRepository;

    @SuppressWarnings("UnusedReturnValue")
    public LatestPostTimestamp createOrUpdate(
        CreateLatestPostTimestampDto createLatestPostTimestampDto
    ) {
        LatestPostTimestamp latestPostTimestamp = getOrInitialize(createLatestPostTimestampDto);
        latestPostTimestamp.setTimestamp(LocalDateTime.now());
        return getLatestPostTimestampAfterSave(latestPostTimestamp);
    }

    private LatestPostTimestamp getLatestPostTimestampAfterSave(LatestPostTimestamp latestPostTimestamp) {
        try {
            return latestPostTimestampRepository.save(latestPostTimestamp);
        } catch (DataIntegrityViolationException dive) {
            log.error(
                "latestPostTimestamp 데이터 생성중 중복 오류가 발생하였습니다 : stockCode={} group={} category={}",
                latestPostTimestamp.getStock().getCode(),
                latestPostTimestamp.getBoardGroup(),
                latestPostTimestamp.getBoardCategory(),
                dive
            );
        }
        return latestPostTimestamp;
    }

    private LatestPostTimestamp getOrInitialize(
        CreateLatestPostTimestampDto createLatestPostTimestampDto
    ) {
        return get(createLatestPostTimestampDto)
            .orElse(initialize(createLatestPostTimestampDto));
    }

    private Optional<LatestPostTimestamp> get(
        CreateLatestPostTimestampDto createLatestPostTimestampDto
    ) {
        return latestPostTimestampRepository.findByStockCodeAndBoardGroupAndBoardCategory(
            createLatestPostTimestampDto.getStock().getCode(),
            createLatestPostTimestampDto.getBoardGroup(),
            createLatestPostTimestampDto.getBoardCategory()
        );
    }

    private LatestPostTimestamp initialize(
        CreateLatestPostTimestampDto createLatestPostTimestampDto
    ) {
        LatestPostTimestamp latestPostTimestamp = new LatestPostTimestamp();
        latestPostTimestamp.setStock(createLatestPostTimestampDto.getStock());
        latestPostTimestamp.setBoardGroup(createLatestPostTimestampDto.getBoardGroup());
        latestPostTimestamp.setBoardCategory(createLatestPostTimestampDto.getBoardCategory());
        return latestPostTimestamp;
    }
}
