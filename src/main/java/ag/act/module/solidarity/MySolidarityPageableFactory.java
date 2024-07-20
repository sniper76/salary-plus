package ag.act.module.solidarity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class MySolidarityPageableFactory {

    private static final int PAGE_SIZE = 10000;

    public Pageable getPageable() {
        return PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    public Integer getDefaultSize(Integer size) {
        if (size == null) {
            return PAGE_SIZE;
        }
        return size;
    }
}
