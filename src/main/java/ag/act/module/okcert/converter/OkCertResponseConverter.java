package ag.act.module.okcert.converter;

import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class OkCertResponseConverter {
    private final ObjectMapperUtil objectMapperUtil;
    private final CamelCaseToScreamingSnakeCaseConverter camelCaseToScreamingSnakeCaseConverter;

    public OkCertResponseConverter(
        ObjectMapperUtil objectMapperUtil,
        CamelCaseToScreamingSnakeCaseConverter camelCaseToScreamingSnakeCaseConverter
    ) {
        this.objectMapperUtil = objectMapperUtil;
        this.camelCaseToScreamingSnakeCaseConverter = camelCaseToScreamingSnakeCaseConverter;
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(String response, Class<T> target)
        throws JsonProcessingException, ReflectiveOperationException {
        final Map<String, String> map = objectMapperUtil.toResponse(response, Map.class);

        final Constructor<T> constructor = target.getDeclaredConstructor();
        final T targetInstance = constructor.newInstance();
        final Field[] fields = target.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            field.set(targetInstance, map.get(camelCaseToScreamingSnakeCaseConverter.convert(field.getName())));
        }

        return targetInstance;
    }
}
