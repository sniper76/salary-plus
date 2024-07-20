package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.DigitalProxy;
import ag.act.model.CreateDigitalProxyRequest;
import ag.act.model.DigitalProxyResponse;
import ag.act.model.Status;
import org.springframework.stereotype.Component;

@Component
public class DigitalProxyModuSignConverter {

    public DigitalProxy convert(CreateDigitalProxyRequest createDigitalProxyRequest) {
        DigitalProxy digitalProxy = new DigitalProxy();

        digitalProxy.setTemplateId(createDigitalProxyRequest.getTemplateId());
        digitalProxy.setTemplateName(createDigitalProxyRequest.getTemplateName());
        digitalProxy.setTemplateRole(createDigitalProxyRequest.getTemplateRole());
        digitalProxy.setTargetStartDate(DateTimeConverter.convert(createDigitalProxyRequest.getTargetStartDate()));
        digitalProxy.setTargetEndDate(DateTimeConverter.convert(createDigitalProxyRequest.getTargetEndDate()));
        digitalProxy.setStatus(Status.fromValue(createDigitalProxyRequest.getStatus()));

        return digitalProxy;
    }

    public DigitalProxyResponse convert(DigitalProxy digitalProxy) {
        if (digitalProxy == null) {
            return null;
        }
        return new DigitalProxyResponse()
            .id(digitalProxy.getId())
            .templateId(digitalProxy.getTemplateId())
            .templateName(digitalProxy.getTemplateName())
            .templateRole(digitalProxy.getTemplateRole())
            .targetStartDate(DateTimeConverter.convert(digitalProxy.getTargetStartDate()))
            .targetEndDate(DateTimeConverter.convert(digitalProxy.getTargetEndDate()))
            .status(digitalProxy.getStatus().name());
    }
}
