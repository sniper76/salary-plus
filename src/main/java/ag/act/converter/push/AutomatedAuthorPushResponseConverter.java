package ag.act.converter.push;

import ag.act.converter.DateTimeConverter;
import ag.act.dto.admin.AutomatedAuthorPushDto;
import ag.act.entity.Push;
import ag.act.entity.User;
import ag.act.model.PushDetailsResponse;
import ag.act.model.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutomatedAuthorPushResponseConverter {
    public List<PushDetailsResponse> convert(List<AutomatedAuthorPushDto> pushList) {
        return pushList.stream().map(this::convert).toList();
    }

    public PushDetailsResponse convert(AutomatedAuthorPushDto authorPushDto) {
        final Push push = authorPushDto.getPush();
        final User user = authorPushDto.getUser();
        return new PushDetailsResponse()
            .id(push.getId())
            .user(convertUserResponse(user))
            .title(push.getTitle())
            .content(push.getContent())
            .targetDatetime(DateTimeConverter.convert(push.getTargetDatetime()))
            .sentStartDatetime(DateTimeConverter.convert(push.getSentStartDatetime()))
            .sentEndDatetime(DateTimeConverter.convert(push.getSentEndDatetime()))
            .createdAt(DateTimeConverter.convert(push.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(push.getUpdatedAt()));
    }

    private UserResponse convertUserResponse(User user) {
        return new UserResponse().name(user.getName()).nickname(user.getNickname());
    }
}
