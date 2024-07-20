package ag.act.dto.notification;

import ag.act.enums.notification.NotificationCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@AllArgsConstructor
@Getter
public class NotificationSearchDto {

    @Getter(AccessLevel.PRIVATE)
    private final Optional<NotificationCategory> category;
    private final PageRequest pageRequest;
    private final String postTitle;

    public NotificationSearchDto(String categoryName, PageRequest pageRequest, String postTitle) {
        this.category = NotificationCategory.fromValue(categoryName);
        this.pageRequest = pageRequest;
        this.postTitle = postTitle;
    }

    public String getNullableCategoryName() {
        return category.map(Enum::name).orElse(null);
    }
}
