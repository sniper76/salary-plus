package ag.act.service.digitaldocument.documenttype;

import ag.act.enums.DigitalDocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
@Service
public class DigitalDocumentTypeServiceResolver {

    private final List<DigitalDocumentTypeService> digitalDocumentTypeServices;
    private final JointOwnershipDocumentService jointOwnershipDocumentService;

    public DigitalDocumentTypeService getService(DigitalDocumentType type) {
        return digitalDocumentTypeServices.stream()
            .filter(service -> service.supports(type))
            .findFirst()
            .orElse(jointOwnershipDocumentService);
    }
}
