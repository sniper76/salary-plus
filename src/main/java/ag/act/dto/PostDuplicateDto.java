package ag.act.dto;

import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDuplicateDto {
    final List<String> stockCodes;
    final Post post;
    final Board board;
    final PostUserProfile postUserProfile;
    final List<PostImage> postImages;
}
