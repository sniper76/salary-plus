package ag.act.module.modusign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuSignApiRequest<T> {
    private boolean isGetType;
    private Map<String, Object> request;
    private Class<T> responseType;
    private String url;
}
