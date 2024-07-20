package ag.act.service.user;

import ag.act.dto.MySolidarityDto;
import ag.act.dto.admin.UserDummyStockDto;
import ag.act.entity.Solidarity;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.module.solidarity.MySolidarityPageableFactory;
import ag.act.repository.UserHoldingStockRepository;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHoldingStockService {
    private final MySolidarityPageableFactory mySolidarityPageableFactory;
    private final UserHoldingStockRepository userHoldingStockRepository;

    private static final Long ZERO_ASSET_AMOUNT = 0L;

    private long calculatePurchasedAmount(UserHoldingStock userHoldingStock) {
        return userHoldingStock.getQuantity() * getStockClosingPrice(userHoldingStock);
    }

    public boolean hasUserHoldingStock(Long userId, String stockCode) {
        return findUserHoldingStock(userId, stockCode).isPresent();
    }

    public Optional<UserHoldingStock> findUserHoldingStock(Long userId, String stockCode) {
        return userHoldingStockRepository.findFirstByUserIdAndStockCode(userId, stockCode);
    }

    public UserHoldingStock getUserHoldingStock(Long userId, String stockCode) {
        return findUserHoldingStock(userId, stockCode)
            .orElseThrow(() -> new BadRequestException("보유 주식 정보가 없습니다."));
    }

    public List<UserHoldingStock> saveAll(List<UserHoldingStock> userHoldingStocks) {
        return userHoldingStockRepository.saveAll(userHoldingStocks);
    }

    public UserHoldingStock save(UserHoldingStock userHoldingStock) {
        return userHoldingStockRepository.save(userHoldingStock);
    }

    public Long sumStockQuantityOfSolidarityMembers(Solidarity solidarity) {
        return userHoldingStockRepository.sumQuantityOfActiveUserHoldingStockByStockCode(solidarity.getStockCode())
            .orElse(0L);
    }

    public Integer getActiveCountByStockCode(String stockCode) {
        return userHoldingStockRepository.countByStockCodeAndStatus(stockCode, Status.ACTIVE);
    }

    public void setActiveStocksDisplayOrderByPurchasedAmountDesc(User user) {
        List<UserHoldingStock> sortedActiveUserHoldingStocks = getAllActiveSortedMySolidarityList(
            user.getId(), mySolidarityPageableFactory.getPageable()
        )
            .stream()
            .map(MySolidarityDto::getUserHoldingStock)
            .sorted(Comparator.comparing(this::calculatePurchasedAmount).reversed())
            .toList();

        for (int i = 0; i < sortedActiveUserHoldingStocks.size(); i++) {
            sortedActiveUserHoldingStocks.get(i).setDisplayOrder(i);
        }
    }

    public void deleteAllByUserId(Long userId) {
        userHoldingStockRepository.deleteAllByUserId(userId);
    }

    public void deleteDummyUserHoldingStock(Long userId, String stockCode) {
        userHoldingStockRepository.deleteAllByUserIdAndStockCodeAndStatusIsNull(userId, stockCode);
    }

    public void deleteAll(List<UserHoldingStock> userHoldingStocks) {
        userHoldingStockRepository.deleteAll(userHoldingStocks);
    }

    public Page<MySolidarityDto> getAllSortedMySolidarityList(Long userId, Pageable pageable) {
        return userHoldingStockRepository.findSortedMySolidarityList(
            userId, List.of(Status.ACTIVE, Status.INACTIVE_BY_ADMIN), pageable
        );
    }

    public List<MySolidarityDto> getAllActiveSortedMySolidarityList(Long userId, Pageable pageable) {
        return getPagingActiveSortedMySolidarityList(
            userId, pageable
        )
            .getContent();
    }

    public Page<MySolidarityDto> getPagingActiveSortedMySolidarityList(Long userId, Pageable pageable) {
        return userHoldingStockRepository.findSortedMySolidarityList(
            userId, List.of(Status.ACTIVE), pageable
        );
    }

    public List<MySolidarityDto> getTop20SortedMySolidarityList(Long userId) {
        return userHoldingStockRepository.findSortedMySolidarityListByDate(
                userId,
                List.of(Status.ACTIVE, Status.INACTIVE_BY_ADMIN),
                KoreanDateTimeUtil.getYesterdayLocalDate(),
                PageRequest.of(0, 20)
            )
            .stream()
            .toList();

    }

    public boolean hasAnyUserHoldingStock(Long userId) {
        return userHoldingStockRepository.existsByUserId(userId);
    }

    public boolean hasDummyUserHoldingStock(Long userId, String stockCode) {
        return userHoldingStockRepository.existsByUserIdAndStockCodeAndStatus(userId, stockCode, null);
    }

    public UserHoldingStock findQueriedUserHoldingStock(String stockCode, List<UserHoldingStock> userHoldingStocks) {
        return userHoldingStocks.stream()
            .filter(userHoldingStock -> userHoldingStock.getStockCode().equals(stockCode))
            .findFirst()
            .orElse(null);
    }

    public List<String> getLeadingSolidarityStockCodes(Long userId) {
        return userHoldingStockRepository.findLeadingSolidarityStockCodes(userId);
    }

    public Long getTotalAssetAmount(Long userId) {
        return userHoldingStockRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE)
            .stream()
            .mapToLong(it -> it.getQuantity() * getStockClosingPrice(it))
            .reduce(ZERO_ASSET_AMOUNT, Long::sum);
    }

    public List<UserHoldingStock> findAllByUserIdAndStatusActive(Long userId) {
        return userHoldingStockRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE);
    }

    public List<SimpleStock> findAllSimpleStocksByUserId(Long userId) {
        return userHoldingStockRepository.findAllUserHoldingSimpleStocksByUserId(userId);
    }

    public List<Long> findAllIds() {
        return userHoldingStockRepository.findAllIds();
    }

    public Page<UserDummyStockDto> getUserDummyStocks(Long userId, Pageable pageable) {
        return userHoldingStockRepository.findAllByUserIdInAndStatusIsNull(userId, pageable);
    }

    private Integer getStockClosingPrice(UserHoldingStock it) {
        if (it.getStock() == null) {
            return 0;
        }

        return it.getStock().getClosingPrice();
    }

    public List<String> getAllUserHoldingStockCodes(Long userId) {
        return userHoldingStockRepository.findAllUserHoldingStockCodesByUserId(userId);
    }

    public List<UserHoldingStock> findAllByIds(List<Long> ids) {
        return userHoldingStockRepository.findAllById(ids);
    }
}
