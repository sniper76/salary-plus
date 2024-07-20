package ag.act.service.digitaldocument.modusign;

import ag.act.converter.digitaldocument.DigitalProxyModuSignConverter;
import ag.act.entity.DigitalProxy;
import ag.act.model.CreatePostRequest;
import ag.act.model.Status;
import ag.act.repository.DigitalProxyRepository;
import ag.act.validator.document.DigitalProxyModuSignValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class DigitalProxyModuSignService {
    public final DigitalProxyModuSignConverter digitalProxyModuSignConverter;
    public final DigitalProxyModuSignValidator digitalProxyModuSignValidator;
    private final DigitalProxyRepository digitalProxyRepository;

    public DigitalProxy makeDigitalProxy(CreatePostRequest createPostRequest) {
        digitalProxyModuSignValidator.validate(createPostRequest.getDigitalProxy());
        return digitalProxyModuSignConverter.convert(createPostRequest.getDigitalProxy());
    }

    public Set<String> getInProgressDigitalProxiesStockCodes(List<String> stockCodes) {
        return digitalProxyRepository.findAllInProgressByStockCodeIn(stockCodes);
    }

    public void deleteDigitalProxyModuSign(DigitalProxy digitalProxy, Status status, LocalDateTime deleteTime) {
        if (digitalProxy == null) {
            return;
        }

        digitalProxy.setStatus(status);
        digitalProxy.setDeletedAt(deleteTime);
        digitalProxyRepository.save(digitalProxy);
    }
}
