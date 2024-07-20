package ag.act.dto.user;

import java.time.LocalDateTime;

public interface BlockedUserDto {

    Long getId();

    Long getBlockedUserId();

    String getNickname();

    String getProfileImageUrl();

    LocalDateTime getCreatedAt();
}
