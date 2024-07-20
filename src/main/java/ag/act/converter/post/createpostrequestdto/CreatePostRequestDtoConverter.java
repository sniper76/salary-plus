package ag.act.converter.post.createpostrequestdto;

import ag.act.dto.post.CreatePostRequestDto;
import ag.act.model.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CreatePostRequestDtoConverter {

    private final DefaultCreatePostRequestDtoConverter defaultCreatePostRequestDtoConverter;
    private final List<ICreatePostRequestDtoConverter> createPostRequestDtoConverters;

    public CreatePostRequestDto convert(
        String stockCode,
        String boardGroupName,
        CreatePostRequest createPostRequest
    ) {
        return createPostRequestDtoConverters.stream()
            .filter(converter -> converter.supports(createPostRequest))
            .findFirst()
            .orElse(defaultCreatePostRequestDtoConverter)
            .convert(stockCode, boardGroupName, createPostRequest);
    }
}
