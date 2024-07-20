package ag.act.converter.post.createpostrequestdto;

import ag.act.dto.post.CreatePostRequestDto;
import ag.act.model.CreatePostRequest;

interface ICreatePostRequestDtoConverter {

    CreatePostRequestDto convert(
        String stockCode,
        String boardGroupName,
        CreatePostRequest createPostRequest
    );

    boolean supports(CreatePostRequest createPostRequest);
}
