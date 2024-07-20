package ag.act.service.stock;

import ag.act.dto.stock.GetStockGroupsSearchDto;
import ag.act.dto.stock.SimpleStockGroupDto;
import ag.act.entity.StockGroup;
import ag.act.exception.BadRequestException;
import ag.act.repository.StockGroupRepository;
import ag.act.repository.interfaces.StockGroupSearchResultItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockGroupService {
    private final StockGroupRepository stockGroupRepository;

    public StockGroupService(StockGroupRepository stockGroupRepository) {
        this.stockGroupRepository = stockGroupRepository;
    }

    public Optional<StockGroup> findById(Long stockGroupId) {
        return stockGroupRepository.findById(stockGroupId);
    }

    public StockGroup findByIdNoneNull(Long stockGroupId) {
        return findById(stockGroupId).orElseThrow(() -> new BadRequestException("대상 종목그룹이 존재하지 않습니다."));
    }

    public Page<StockGroupSearchResultItem> getStockGroups(GetStockGroupsSearchDto getStockGroupSearchDto) {
        return stockGroupRepository.findAllByStockGroupIdIn(
            getStockGroupSearchDto.getStockGroupIds(),
            getStockGroupSearchDto.getPageRequest()
        );
    }

    public StockGroup save(StockGroup stockGroup) {
        try {
            return stockGroupRepository.saveAndFlush(stockGroup);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("동일한 종목그룹이 존재합니다.", e);
        }
    }

    public void deleteStockGroup(StockGroup stockGroup) {
        LocalDateTime deleteTime = LocalDateTime.now();

        stockGroup.setDeletedAt(deleteTime);
        stockGroup.setStatus(ag.act.model.Status.DELETED);
        save(stockGroup);
    }

    public List<StockGroup> getMostRelatedTopTenStockGroupsBySearchKeyword(String searchKeyword) {
        if (StringUtils.isBlank(searchKeyword)) {
            return stockGroupRepository.findTop10ByStatusInOrderByNameAsc(List.of(ag.act.model.Status.ACTIVE));
        }

        return stockGroupRepository.findTop10ByNameContainingAndStatusInOrderByNameAsc(
            searchKeyword, List.of(ag.act.model.Status.ACTIVE)
        );
    }

    public List<SimpleStockGroupDto> getSimpleStockGroups() {
        return stockGroupRepository.findAllSimpleStockGroups(Sort.by(Sort.Direction.ASC, "name"));
    }
}
