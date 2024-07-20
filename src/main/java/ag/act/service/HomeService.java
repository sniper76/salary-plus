package ag.act.service;

import ag.act.dto.MySolidarityDto;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.BadRequestException;
import ag.act.exception.ForbiddenException;
import ag.act.model.Status;
import ag.act.module.solidarity.MySolidarityPageableFactory;
import ag.act.service.user.UserHoldingStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {
    private final MySolidarityPageableFactory mySolidarityPageableFactory;
    private final UserHoldingStockService userHoldingStockService;

    private List<String> getUserHoldingStockCodes(List<MySolidarityDto> mySolidarityDtoList) {
        return mySolidarityDtoList
            .stream()
            .map(this::getStockCode)
            .toList();
    }

    private String getStockCode(MySolidarityDto mySolidarityDto) {
        return mySolidarityDto.getUserHoldingStock().getStockCode();
    }

    private boolean isSolidarityActive(MySolidarityDto mySolidarityDto) {
        return mySolidarityDto.getSolidarity().getStatus() == Status.ACTIVE;
    }

    private List<UserHoldingStock> validateAndGetUserHoldingStocks(User user, List<String> stockCodes) {
        if (stockCodes.stream().distinct().count() != stockCodes.size()) {
            log.error("validateUserAndGetUserHoldingStocks err: userId = {}, stockCodes = {}", user.getId(), stockCodes);
            throw new BadRequestException("중복된 종목 코드가 존재합니다.");
        }
        List<MySolidarityDto> mySolidarityDtoList = userHoldingStockService.getAllActiveSortedMySolidarityList(
            user.getId(), mySolidarityPageableFactory.getPageable()
        );

        validateUserHoldingStocks(stockCodes, mySolidarityDtoList);

        final Map<String, MySolidarityDto> mySolidarityMap = mySolidarityDtoList
            .stream()
            .collect(Collectors.toMap(this::getStockCode, Function.identity()));

        return stockCodes.stream()
            .map(mySolidarityMap::get)
            .filter(this::isSolidarityActive)
            .map(MySolidarityDto::getUserHoldingStock)
            .toList();
    }

    private void validateUserHoldingStocks(List<String> stockCodes, List<MySolidarityDto> mySolidarityDtoList) {
        List<String> userHoldingStockCodes = getUserHoldingStockCodes(mySolidarityDtoList);

        validateStockCount(stockCodes, userHoldingStockCodes);
        validateUserHasThatStocks(stockCodes, userHoldingStockCodes);
    }

    private void validateUserHasThatStocks(List<String> stockCodes, List<String> userHoldingStockCodes) {
        stockCodes.forEach(
            stockCode -> validateUserHoldingStocksIncludeTheStockCode(userHoldingStockCodes, stockCode)
        );
    }

    private void validateUserHoldingStocksIncludeTheStockCode(List<String> userHoldingStockCodes, String stockCode) {
        if (!userHoldingStockCodes.contains(stockCode)) {
            log.error("Not contains: userHoldingStockCodes = {}, stockCode = {}", userHoldingStockCodes, stockCode);
            throw new BadRequestException(String.format("처리할 수 없는 종목 코드가 존재합니다. (%s)", stockCode));
        }
    }

    private void validateStockCount(List<String> stockCodes, List<String> userHoldingStockCodes) {
        if (userHoldingStockCodes.size() != stockCodes.size()) {
            log.error("Not matched: userHoldingStockCodes = {}, stockCodes = {}", userHoldingStockCodes, stockCodes);
            throw new BadRequestException("활성화된 모든 종목의 코드를 입력해주세요.");
        }
    }

    public Page<MySolidarityDto> updateUserHoldingStocks(User user, List<String> stockCodes, Pageable pageable) throws ForbiddenException {
        List<UserHoldingStock> holdingStocksToUpdate = validateAndGetUserHoldingStocks(user, stockCodes);

        for (int i = 0; i < holdingStocksToUpdate.size(); i++) {
            holdingStocksToUpdate.get(i).setDisplayOrder(i);
        }

        return userHoldingStockService.getAllSortedMySolidarityList(user.getId(), pageable);
    }
}
