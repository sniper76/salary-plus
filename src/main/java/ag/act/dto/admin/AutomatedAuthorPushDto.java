package ag.act.dto.admin;

import ag.act.entity.Push;
import ag.act.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutomatedAuthorPushDto {
    private Push push;
    private User user;
}
