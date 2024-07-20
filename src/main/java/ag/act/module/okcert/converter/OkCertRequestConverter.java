package ag.act.module.okcert.converter;

import ag.act.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OkCertRequestConverter {
    private final ObjectMapperUtil objectMapperUtil;

    public OkCertRequestConverter(ObjectMapperUtil objectMapperUtil) {
        this.objectMapperUtil = objectMapperUtil;
    }

    public <T> String convert(T dto) {
        log.debug(">> okCertParamConverter: {}", dto);
        return objectMapperUtil.toJsonInUpperCase(dto);
    }
}
