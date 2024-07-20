package ag.act.module.dart;

import ag.act.entity.StockDartCorporation;
import ag.act.module.dart.dto.CorpCodeItem;
import ag.act.module.dart.dto.CorpCodeResult;
import ag.act.module.dart.dto.DartCompany;
import ag.act.service.dart.StockDartCorporationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@Component
@RequiredArgsConstructor
public class DartCorpCodeService {
    private final DartCorpCodeInputStreamReader dartCorpCodeInputStreamReader;
    private final DartCorpCodeXmlReader dartCorpCodeXmlConverter;
    private final DartCorpCodeXmlParser dartCorpCodeXmlParser;
    private final StockDartCorporationService stockDartCorporationService;
    private final DartCorpCodeItemConverter dartCorpCodeItemConverter;
    private final DartCompanyService dartCompanyService;
    private final DartCorporationPageableFactory dartCorporationPageableFactory;

    public CorpCodeResult readCorpCodeResult() {
        final InputStream inputStream = dartCorpCodeInputStreamReader.read();
        final String xmlContent = dartCorpCodeXmlConverter.convert(inputStream);

        return dartCorpCodeXmlParser.parseXml(xmlContent);
    }

    public StockDartCorporation upsert(CorpCodeItem corpCodeItem) {
        final StockDartCorporation stockDartCorporation = dartCorpCodeItemConverter.convert(corpCodeItem);

        return stockDartCorporationService.upsert(stockDartCorporation);
    }

    public List<StockDartCorporation> getOldestStockDartCorporations() {
        return stockDartCorporationService.findOldestList(dartCorporationPageableFactory.getPageable());
    }

    public StockDartCorporation updateStockDartCorporations(StockDartCorporation stockDartCorporation) {
        final DartCompany dartCompany = getStockDartCompany(stockDartCorporation);

        stockDartCorporation.setCeoName(dartCompany.getCeoName());
        stockDartCorporation.setCorpClass(dartCompany.getCorpClass());
        stockDartCorporation.setJurisdictionalRegistrationNumber(dartCompany.getJurisdictionalRegistrationNumber());
        stockDartCorporation.setBusinessRegistrationNumber(dartCompany.getBusinessRegistrationNumber());
        stockDartCorporation.setAddress(dartCompany.getAddress());
        stockDartCorporation.setHomepageUrl(dartCompany.getHomepageUrl());
        stockDartCorporation.setIrUrl(dartCompany.getIrUrl());
        stockDartCorporation.setRepresentativePhoneNumber(dartCompany.getRepresentativePhoneNumber());
        stockDartCorporation.setRepresentativeFaxNumber(dartCompany.getRepresentativeFaxNumber());
        stockDartCorporation.setIndustryCode(dartCompany.getIndustryCode());
        stockDartCorporation.setEstablishmentDate(dartCompany.getEstablishmentDate());
        stockDartCorporation.setAccountSettlementMonth(dartCompany.getAccountSettlementMonth());

        return stockDartCorporationService.upsert(stockDartCorporation);
    }

    private DartCompany getStockDartCompany(StockDartCorporation stockDartCorporation) {
        return dartCompanyService.getStockDartCompany(stockDartCorporation.getCorpCode());
    }
}
