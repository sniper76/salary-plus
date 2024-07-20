package ag.act.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeletePostRequestDto implements BasePostRequest {
    private String stockCode;
    private String boardGroupName;
    private Long postId;
}
