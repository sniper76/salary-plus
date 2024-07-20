package ag.act.converter;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Converter<I, O> extends Function<I, O> {

    default List<O> convertList(final List<I> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream()
            .map(this)
            .collect(Collectors.toList());
    }
}
