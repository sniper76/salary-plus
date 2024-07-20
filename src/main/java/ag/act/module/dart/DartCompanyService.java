package ag.act.module.dart;

import ag.act.module.dart.dto.DartCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DartCompanyService {

    private final DartHttpClientUtil dartHttpClientUtil;

    public DartCompany getStockDartCompany(String corpCode) {
        return dartHttpClientUtil.getStockDartCompany(corpCode);
    }
}
