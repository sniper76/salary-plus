package ag.act.dto.post;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.ActUser;
import ag.act.enums.ClientType;
import ag.act.model.CreatePostRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
// TODO can be converted to record
public class CreatePostRequestDto {
    private String stockCode;
    private String boardGroupName;
    private CreatePostRequest createPostRequest;
    private ActUser actUser;

    private final ClientType clientType = RequestContextHolder.getClientType();

    public GetBoardGroupPostDto getBoardGroupPostDto() {
        return GetBoardGroupPostDto.builder()
            .stockCode(stockCode)
            .boardGroupName(boardGroupName)
            .boardCategories(List.of(createPostRequest.getBoardCategory()))
            .build();
    }

    public CreatePostRequest getCreatePostRequest() {
        setDefaultActiveStartDate();

        return createPostRequest;
    }

    private void setDefaultActiveStartDate() {
        if (createPostRequest == null) {
            return;
        }

        if (createPostRequest.getActiveStartDate() == null) {
            createPostRequest.setActiveStartDate(Instant.now());
        }
    }
}
