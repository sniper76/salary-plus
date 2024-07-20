package ag.act.converter;

import org.springframework.stereotype.Component;

@Component
public class RequestValueConverter {

    public String convert(String value) {
        return value == null ? "" : value.trim();
    }

    public Boolean convertBoolean(String value) {
        return value != null && Boolean.parseBoolean(value.trim());
    }
}
