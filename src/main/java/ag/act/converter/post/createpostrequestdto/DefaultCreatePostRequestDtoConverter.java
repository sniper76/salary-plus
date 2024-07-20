package ag.act.converter.post.createpostrequestdto;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.model.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class DefaultCreatePostRequestDtoConverter implements ICreatePostRequestDtoConverter {

    @Override
    public boolean supports(CreatePostRequest createPostRequest) {
        return true;
    }

    public CreatePostRequestDto convert(
        String stockCode,
        String boardGroupName,
        CreatePostRequest createPostRequest
    ) {
        return new CreatePostRequestDto(
            stockCode,
            boardGroupName,
            createPostRequest,
            ActUserProvider.getNoneNull()
        );
    }
}
