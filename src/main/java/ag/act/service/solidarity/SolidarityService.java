package ag.act.service.solidarity;

import ag.act.entity.Solidarity;
import ag.act.exception.NotFoundException;
import ag.act.repository.SolidarityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SolidarityService {
    private final SolidarityRepository solidarityRepository;

    public Optional<Solidarity> findSolidarity(String stockCode) {
        return this.solidarityRepository.findByStockCode(stockCode);
    }

    public Solidarity getSolidarityByStockCode(String stockCode) {
        return findSolidarity(stockCode)
            .orElseThrow(() -> new NotFoundException("%s 종목의 연대 정보를 찾을 수 없습니다.".formatted(stockCode)));
    }

    public Solidarity getSolidarity(Long solidarityId) {
        return findSolidarityById(solidarityId)
            .orElseThrow(() -> new NotFoundException("%s id의 연대 정보를 찾을 수 없습니다.".formatted(solidarityId)));
    }

    @NotNull
    private Optional<Solidarity> findSolidarityById(Long solidarityId) {
        return solidarityRepository.findById(solidarityId);
    }

    public List<Solidarity> getAllSolidarities() {
        return this.solidarityRepository.getAllSolidarities();
    }

    public Solidarity saveSolidarity(Solidarity solidarity) {
        return solidarityRepository.save(solidarity);
    }
}
