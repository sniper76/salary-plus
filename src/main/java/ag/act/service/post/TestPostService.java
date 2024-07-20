package ag.act.service.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.post.PostResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.repository.posts.test.TestPostRepository;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TestPostService {
    private final TestPostRepository testPostRepository;
    private final PostResponseConverter postResponseConverter;

    public List<ag.act.model.PostResponse> getTestPostList(GetBoardGroupPostDto getBoardGroupPostDto) {
        final User user = ActUserProvider.getNoneNull();
        final LocalDateTime targetEndDate = KoreanDateTimeUtil.getNowInKoreanTime().toLocalDateTime();
        final List<Post> postDataList = testPostRepository.findAllByUserIdAndTargetEndDateAndCategoryAndStockCode(
            user.getId(), targetEndDate, getBoardGroupPostDto.getFirstBoardCategory(), getBoardGroupPostDto.getStockCode()
        );
        return postDataList.stream().map(postResponseConverter::convert).toList();
    }
}
