package ag.act.converter;

import ag.act.dto.SimplePageDto;
import ag.act.exception.InternalServerException;
import ag.act.model.Paging;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PageDataConverter {

    private static final String DEFAULT_SORT_FILED = "createdAt";
    private static final int DEFAULT_PAGE_NUMBER = 1;

    public PageRequest convert(
        Integer size,
        List<String> sorts
    ) {
        return convert(DEFAULT_PAGE_NUMBER, size, sorts);
    }

    public PageRequest convert(
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        if (page == null || page < DEFAULT_PAGE_NUMBER) {
            page = DEFAULT_PAGE_NUMBER;
        }
        return PageRequest.of(page - 1, size, Sort.by(generateSortOrders(sorts)));
    }

    public PageRequest convert(
        @NotNull Integer page,
        @NotNull Integer size,
        @NotNull String sort
    ) {
        return PageRequest.of(page - 1, size, Sort.by(toSortOrder(sort)));
    }

    @SuppressWarnings("unchecked")
    public <DataType, T> T convert(SimplePageDto<DataType> source, Class<T> target) {
        try {
            Constructor<T> constructor = target.getDeclaredConstructor();
            T targetInstance = constructor.newInstance();

            final Paging paging = getPaging(source);
            final DataType data = (DataType) source.getClass().getMethod("getContent").invoke(source);

            setField(target.getDeclaredField("paging"), targetInstance, paging);
            setField(target.getDeclaredField("data"), targetInstance, data);

            return targetInstance;

        } catch (Exception e) {
            log.error("convert error - {}", e.getMessage(), e);
            throw new InternalServerException("응답을 처리하던 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 이용해 주세요.", e);
        }
    }

    private List<Sort.Order> generateSortOrders(List<String> sorts) {

        final List<Sort.Order> sortOrders = sorts.stream()
            .map(this::toSortOrder)
            .filter(Objects::nonNull)
            .toList();

        if (sortOrders.isEmpty()) {
            return List.of(Sort.Order.desc(DEFAULT_SORT_FILED));
        }

        return sortOrders;
    }

    private Sort.Order toSortOrder(String sort) {
        if (StringUtils.isBlank(sort)) {
            return null;
        }
        final String[] split = sort.split(":");
        if (split.length != 2) {
            return null;
        }

        final String field = split[0];
        final String order = split[1].toLowerCase();

        if (order.equals("asc")) {
            return Sort.Order.asc(field);
        }
        return Sort.Order.desc(field);
    }

    @SuppressWarnings("unchecked")
    private <DataType> Paging getPaging(SimplePageDto<DataType> source)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        final Integer page = (Integer) source.getClass().getMethod("getPage").invoke(source);
        final Integer size = (Integer) source.getClass().getMethod("getSize").invoke(source);
        final Integer totalPages = (Integer) source.getClass().getMethod("getTotalPages").invoke(source);
        final Long totalElements = (Long) source.getClass().getMethod("getTotalElements").invoke(source);
        final List<String> sorts = (List<String>) source.getClass().getMethod("getSorts").invoke(source);

        return new Paging()
            .page(page + 1)
            .size(size)
            .totalPages(totalPages)
            .totalElements(totalElements)
            .sorts(sorts.stream()
                .map(str -> str.replace(",", ":"))
                .collect(Collectors.toList()));
    }

    private static <T> void setField(Field pagingField, T targetInstance, Object value) throws IllegalAccessException {
        pagingField.setAccessible(true);
        pagingField.set(targetInstance, value);
    }

}
