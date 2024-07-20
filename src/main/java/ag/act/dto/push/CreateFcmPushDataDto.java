package ag.act.dto.push;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CreateFcmPushDataDto {
    private String title;
    private String content;
    private String linkUrl;

    public static CreateFcmPushDataDto newInstance(String pushTitle, String pushMessage, String linkUrl) {
        return new CreateFcmPushDataDto(pushTitle, pushMessage, linkUrl);
    }
}
