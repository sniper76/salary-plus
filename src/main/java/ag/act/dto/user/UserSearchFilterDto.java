package ag.act.dto.user;

import ag.act.enums.admin.UserFilterType;
import ag.act.enums.admin.UserSearchType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class UserSearchFilterDto {
    private UserFilterType userFilterType;
    private UserSearchType userSearchType;
    private String keyword;

    public UserSearchFilterDto(String userFilterType, String userSearchTypeName, String keyword) {
        this.userFilterType = UserFilterType.fromValue(userFilterType);
        this.userSearchType = UserSearchType.fromValue(userSearchTypeName);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return Optional.ofNullable(keyword).map(String::trim).orElse(null);
    }
}