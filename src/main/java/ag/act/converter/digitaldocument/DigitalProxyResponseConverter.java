package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.DigitalProxy;
import org.springframework.stereotype.Component;

@Component
public class DigitalProxyResponseConverter {

    public ag.act.model.DigitalProxyResponse convert(DigitalProxy digitalProxy) {
        if (digitalProxy == null) {
            return null;
        }
        return new ag.act.model.DigitalProxyResponse()
            .id(digitalProxy.getId())
            .templateId(digitalProxy.getTemplateId())
            .templateName(digitalProxy.getTemplateName())
            .templateRole(digitalProxy.getTemplateRole())
            .targetStartDate(DateTimeConverter.convert(digitalProxy.getTargetStartDate()))
            .targetEndDate(DateTimeConverter.convert(digitalProxy.getTargetEndDate()))
            .createdAt(DateTimeConverter.convert(digitalProxy.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(digitalProxy.getUpdatedAt()))
            .status(digitalProxy.getStatus().name());
    }
}
