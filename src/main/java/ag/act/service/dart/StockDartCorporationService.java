package ag.act.service.dart;

import ag.act.entity.StockDartCorporation;
import ag.act.repository.StockDartCorporationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockDartCorporationService {
    private final StockDartCorporationRepository stockDartCorporationRepository;

    public StockDartCorporation upsert(StockDartCorporation stockDartCorporation) {
        return findStockDartCorporation(stockDartCorporation.getCorpCode())
            .map(stockDartCorporationFromDatabase -> update(stockDartCorporationFromDatabase, stockDartCorporation))
            .orElseGet(() -> save(stockDartCorporation));
    }

    private StockDartCorporation update(StockDartCorporation target, StockDartCorporation source) {
        target.setStockCode(source.getStockCode());
        target.setCorpName(source.getCorpName());
        target.setModifyDate(source.getModifyDate());
        target.setCorpCode(source.getCorpCode());
        target.setCeoName(source.getCeoName());
        target.setCorpClass(source.getCorpClass());
        target.setJurisdictionalRegistrationNumber(source.getJurisdictionalRegistrationNumber());
        target.setBusinessRegistrationNumber(source.getBusinessRegistrationNumber());
        target.setAddress(source.getAddress());
        target.setHomepageUrl(source.getHomepageUrl());
        target.setIrUrl(source.getIrUrl());
        target.setRepresentativePhoneNumber(source.getRepresentativePhoneNumber());
        target.setRepresentativeFaxNumber(source.getRepresentativeFaxNumber());
        target.setIndustryCode(source.getIndustryCode());
        target.setEstablishmentDate(source.getEstablishmentDate());
        target.setAccountSettlementMonth(source.getAccountSettlementMonth());

        return save(target);
    }

    private StockDartCorporation save(StockDartCorporation stockDartCorporation) {
        stockDartCorporation.increaseVersion();
        return stockDartCorporationRepository.save(stockDartCorporation);
    }

    private Optional<StockDartCorporation> findStockDartCorporation(String corpCode) {
        return stockDartCorporationRepository.findByCorpCode(corpCode);
    }

    public List<StockDartCorporation> getAllDartCorporationsWithStock() {
        return stockDartCorporationRepository.getAllDartCorporationsWithStock();
    }

    public List<StockDartCorporation> findOldestList(Pageable pageable) {
        return stockDartCorporationRepository.findAll(pageable).stream().toList();
    }
}
