package ag.act.core.annotation;

import jakarta.annotation.Nullable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class AnnotationOrderSorter {

    public <T> List<T> sort(List<T> sources) {
        return sources
            .stream()
            .sorted(Comparator.comparing(this::getOrder))
            .toList();
    }

    private <T> Integer getOrder(T source) {
        return Optional.ofNullable(getOrderAnnotation(source))
            .map(Order::value)
            .orElse(Integer.MAX_VALUE);
    }

    @Nullable
    private <T> Order getOrderAnnotation(T source) {
        return AnnotationUtils.findAnnotation(source.getClass(), Order.class);
    }
}
