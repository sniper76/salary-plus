package ag.act.service.solidarity.election;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.stock.SimpleStockDto;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.exception.UnauthorizedException;
import ag.act.model.CreateSolidarityLeaderForCorporateUserRequest;
import ag.act.model.UpdateSolidarityLeaderMessageRequest;
import ag.act.repository.SolidarityLeaderRepository;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.service.admin.CorporateUserService;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.notifier.SolidarityLeaderChangeNotifier;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SolidarityLeaderService {
    private final SolidarityService solidarityService;
    private final SolidarityLeaderRepository solidarityLeaderRepository;
    private final UserService userService;
    private final SolidarityLeaderChangeNotifier solidarityLeaderChangeNotifier;
    private final CorporateUserService corporateUserService;

    public Optional<SolidarityLeader> findLeader(String stockCode) {
        return solidarityService.findSolidarity(stockCode)
            .map(Solidarity::getSolidarityLeader);
    }

    public SolidarityLeader getLeader(String stockCode) {
        return findLeader(stockCode)
            .orElseThrow(() -> new NotFoundException("주주대표가 존재하지 않습니다."));
    }

    public User getLeaderUser(String stockCode) {
        return userService.getUser(getLeader(stockCode).getUserId());
    }

    public boolean isLeader(Long userId) {
        return solidarityLeaderRepository.existsByUserId(userId);
    }

    public boolean isLeader(Long userId, String stockCode) {
        try {
            final SolidarityLeader solidarityLeader = findLeader(stockCode).orElseGet(SolidarityLeader::new);
            return Objects.equals(userId, solidarityLeader.getUserId());
        } catch (InternalServerException e) {
            // ignore not found solidarity error
            return false;
        }
    }

    public SolidarityLeader save(SolidarityLeader solidarityLeader) {
        return solidarityLeaderRepository.save(solidarityLeader);
    }

    public void updateSolidarityLeaderMessage(Long solidarityId, UpdateSolidarityLeaderMessageRequest updateSolidarityLeaderMessageRequest) {
        SolidarityLeader solidarityLeader = getSolidarityLeaderBySolidarityId(solidarityId);
        validateSolidarityLeader(solidarityLeader);

        solidarityLeader.setMessage(updateSolidarityLeaderMessageRequest.getMessage().trim());
        solidarityLeaderRepository.save(solidarityLeader);

        notifyUpdatedLeaderMessage(updateSolidarityLeaderMessageRequest.getMessage(), solidarityLeader.getSolidarity());
    }

    private void validateSolidarityLeader(SolidarityLeader solidarityLeader) {
        if (corporateUserService.findCorporateUserByUserId(solidarityLeader.getUserId()).isPresent()) {
            throw new BadRequestException("법인사업자는 주주대표 한마디를 등록할 수 없습니다.");
        }
        User user = ActUserProvider.getNoneNull();

        if (!Objects.equals(solidarityLeader.getUserId(), user.getId()) && !user.isAdmin()) {
            throw new UnauthorizedException("주주대표가 아니면 변경할 수 없습니다.");
        }
    }

    public SolidarityLeader createSolidarityLeader(SolidarityLeaderApplicant applicant) {
        Solidarity solidarity = solidarityService.getSolidarity(applicant.getSolidarityId());
        return assignSolidarityLeader(solidarity, applicant.getUserId());
    }

    public void dismissSolidarityLeader(Long solidarityLeaderId) {
        SolidarityLeader solidarityLeader = getSolidarityLeaderBySolidarityLeaderId(solidarityLeaderId);
        Solidarity solidarity = solidarityLeader.getSolidarity();
        User leader = userService.getUserForSolidarityLeader(solidarity.getStockCode());

        deleteSolidarityLeader(solidarity, solidarityLeader);
        notifyDismissedLeader(solidarity.getStock().getName(), leader.getName());
    }

    private SolidarityLeader getSolidarityLeaderBySolidarityId(Long solidarityId) {
        return solidarityLeaderRepository.findBySolidarityId(solidarityId)
            .orElseThrow(this::getNotFoundExceptionSupplier);
    }

    private SolidarityLeader getSolidarityLeaderBySolidarityLeaderId(Long solidarityLeaderId) {
        return solidarityLeaderRepository.findById(solidarityLeaderId)
            .orElseThrow(this::getNotFoundExceptionSupplier);
    }

    private NotFoundException getNotFoundExceptionSupplier() {
        return new NotFoundException("주주대표를 찾을 수 없습니다.");
    }

    private void deleteSolidarityLeader(Solidarity solidarity, SolidarityLeader solidarityLeader) {
        solidarity.setSolidarityLeader(null);
        solidarityService.saveSolidarity(solidarity);
        solidarityLeaderRepository.deleteById(solidarityLeader.getId());
    }

    public SolidarityLeader createSolidarityLeaderForCorporateUser(
        Long solidarityId,
        CreateSolidarityLeaderForCorporateUserRequest createSolidarityLeaderForCorporateUserRequest
    ) {
        if (solidarityLeaderRepository.findBySolidarityId(solidarityId).isPresent()) {
            throw new BadRequestException("이미 주주대표가 선정되어 있는 연대입니다.");
        }

        Solidarity solidarity = solidarityService.getSolidarity(solidarityId);

        return assignSolidarityLeader(solidarity, createSolidarityLeaderForCorporateUserRequest.getUserId());
    }

    public List<SimpleStock> findAllLeadingSimpleStocks(Long userId) {
        return solidarityLeaderRepository.findAllLeadingSimpleStocks(userId)
            .stream()
            .map(SimpleStockDto::from)
            .map(SimpleStock.class::cast)
            .toList();
    }

    public List<String> findAllLeadingStockNames(Long userId) {
        return solidarityLeaderRepository.findAllLeadingStockNames(userId);
    }

    private SolidarityLeader assignSolidarityLeader(Solidarity solidarity, Long userId) {
        SolidarityLeader solidarityLeader = new SolidarityLeader();
        solidarityLeader.setSolidarity(solidarity);
        solidarityLeader.setUserId(userId);

        notifyElectedLeader(solidarity.getStock().getName(), userId);

        final SolidarityLeader savedSolidarityLeader = solidarityLeaderRepository.save(solidarityLeader);

        solidarity.setHasEverHadLeader(Boolean.TRUE);
        solidarityService.saveSolidarity(solidarity);

        return savedSolidarityLeader;
    }

    private void notifyUpdatedLeaderMessage(String message, Solidarity solidarity) {
        User leader = userService.getUserForSolidarityLeader(solidarity.getStockCode());

        solidarityLeaderChangeNotifier.notifyUpdatedLeaderMessage(
            solidarity.getStock().getName(),
            leader.getName(),
            message
        );
    }

    private void notifyDismissedLeader(String stockName, String leaderName) {
        solidarityLeaderChangeNotifier.notifyDismissedLeader(
            stockName,
            leaderName,
            ActUserProvider.getNoneNull().getName()
        );
    }

    private void notifyElectedLeader(String stockName, Long leaderId) {
        if (ActUserProvider.get().isEmpty()) {
            return;
        }

        User leader = userService.getUser(leaderId);

        solidarityLeaderChangeNotifier.notifyElectedLeader(
            stockName,
            leader.getName(),
            ActUserProvider.getNoneNull().getName()
        );
    }
}
