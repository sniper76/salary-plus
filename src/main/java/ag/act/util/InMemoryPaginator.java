package ag.act.util;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InMemoryPaginator {

    public <T> Page<T> paginate(List<T> data, PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(data)) {
            return Page.empty(pageRequest);
        }

        int start = (int) pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), data.size());
        List<T> pageContent = data.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, data.size());
    }
}
