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
public class GetNotificationSearchDto {

    @Getter(AccessLevel.PRIVATE)
    private final Optional<NotificationCategory> category;
    private final Long userId;
    private final PageRequest pageRequest;

    public GetNotificationSearchDto(String categoryName, Long userId, PageRequest pageRequest) {
        this.category = NotificationCategory.fromValue(categoryName);
        this.userId = userId;
        this.pageRequest = pageRequest;
    }

    public String getNullableCategoryName() {
        return category.map(Enum::name).orElse(null);
    }
}
