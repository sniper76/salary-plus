package ag.act.itutil.holder;

import ag.act.entity.ActEntity;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"checkstyle:ClassTypeParameterName", "unchecked"})
public class ActEntityTestHolder<T extends ActEntity, IdType> {

    private boolean isInitialized = false;
    private final List<T> items = new ArrayList<>();

    public void initialize(List<T> initialItems) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        items.addAll(initialItems);
    }

    public T addOrSet(@NotNull T item) {

        items.stream()
            .filter(it -> getId(it).equals(getId(item)))
            .findFirst()
            .ifPresentOrElse(
                p -> items.set(items.indexOf(p), item),
                () -> items.add(item)
            );

        return item;
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public IdType getId(T item) {
        return (IdType) item.getId();
    }

}
