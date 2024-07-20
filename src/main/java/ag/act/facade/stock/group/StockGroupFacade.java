package ag.act.facade.stock.group;

import ag.act.converter.stock.SimpleStockGroupResponseConverter;
import ag.act.converter.stock.StockGroupItemResponseConverter;
import ag.act.converter.stock.StockGroupRequestConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.stock.GetStockGroupsSearchDto;
import ag.act.entity.StockGroup;
import ag.act.entity.StockGroupMapping;
import ag.act.exception.NotFoundException;
import ag.act.model.CreateStockGroupRequest;
import ag.act.model.StockGroupResponse;
import ag.act.model.UpdateStockGroupRequest;
import ag.act.service.stock.StockGroupMappingService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import ag.act.validator.StockGroupValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class StockGroupFacade {

    private final StockGroupService stockGroupService;
    private final StockGroupMappingService stockGroupMappingService;
    private final StockGroupItemResponseConverter stockGroupItemResponseConverter;
    private final SimpleStockGroupResponseConverter simpleStockGroupResponseConverter;
    private final StockGroupRequestConverter stockGroupRequestConverter;
    private final StockService stockService;
    private final StockGroupValidator stockGroupValidator;

    public SimplePageDto<StockGroupResponse> getStockGroups(GetStockGroupsSearchDto getStockGroupSearchDto) {
        return new SimplePageDto<>(
            stockGroupService.getStockGroups(getStockGroupSearchDto)
                .map(stockGroupItemResponseConverter::convert)
        );
    }

    public StockGroupResponse createStockGroup(CreateStockGroupRequest createStockGroupRequest) {
        final StockGroup stockGroup = stockGroupService.save(stockGroupRequestConverter.convert(createStockGroupRequest));
        final List<StockGroupMapping> stockGroupMappings = createStockGroupMappings(createStockGroupRequest.getStockCodes(), stockGroup);

        return stockGroupItemResponseConverter.convert(
            StockGroupResultItem.builder()
                .stockGroup(stockGroup)
                .stockCount(stockGroupMappings.size())
                .build()
        );
    }

    public StockGroupResponse updateStockGroup(Long stockGroupId, UpdateStockGroupRequest updateStockGroupRequest) {
        final StockGroup stockGroup = stockGroupService.save(
            stockGroupRequestConverter.map(updateStockGroupRequest, findStockGroupNotNull(stockGroupId))
        );

        stockGroupMappingService.deleteStockGroupMappings(stockGroupId);
        final List<StockGroupMapping> stockGroupMappings = createStockGroupMappings(updateStockGroupRequest.getStockCodes(), stockGroup);

        return stockGroupItemResponseConverter.convert(
            StockGroupResultItem.builder()
                .stockGroup(stockGroup)
                .stockCount(stockGroupMappings.size())
                .build()
        );
    }

    public void deleteStockGroup(Long stockGroupId) {
        StockGroup stockGroup = findStockGroupNotNull(stockGroupId);

        stockGroupService.deleteStockGroup(stockGroup);
    }

    public ag.act.model.StockGroupDetailsResponse getStockGroupDetails(Long stockGroupId) {
        return stockGroupItemResponseConverter.convertToDetails(
            findStockGroupNotNull(stockGroupId),
            stockService.findAllByStockGroupId(stockGroupId)
        );
    }

    private List<StockGroupMapping> createStockGroupMappings(List<String> stockCodes, StockGroup stockGroup) {
        if (CollectionUtils.isEmpty(stockCodes)) {
            return Collections.emptyList();
        }

        stockGroupValidator.validateStockCodes(stockCodes, stockService.findAllByCodes(stockCodes));

        return stockGroupMappingService.createMappings(stockGroup.getId(), stockCodes);
    }

    private StockGroup findStockGroupNotNull(Long stockGroupId) {
        return stockGroupService.findById(stockGroupId)
            .orElseThrow(() -> new NotFoundException("종목그룹을 찾을 수 없습니다."));
    }

    public List<ag.act.model.StockGroupResponse> getStockGroupsAutoComplete(String searchKeyword) {
        return stockGroupService.getMostRelatedTopTenStockGroupsBySearchKeyword(searchKeyword)
            .stream()
            .map(stockGroupItemResponseConverter::convert)
            .toList();
    }

    public List<ag.act.model.SimpleStockGroupResponse> getSimpleStockGroups() {
        return stockGroupService.getSimpleStockGroups()
            .stream()
            .map(simpleStockGroupResponseConverter)
            .toList();
    }
}
