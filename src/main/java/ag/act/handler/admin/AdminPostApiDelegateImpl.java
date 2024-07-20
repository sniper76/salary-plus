package ag.act.handler.admin;

import ag.act.api.AdminPostApiDelegate;
import ag.act.facade.admin.post.AdminPostFacade;
import ag.act.facade.post.PostFacade;
import ag.act.model.PostCopyRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.util.DownloadFileUtil;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminPostApiDelegateImpl implements AdminPostApiDelegate {

    private final PostFacade postFacade;
    private final AdminPostFacade adminPostFacade;

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadPostPollResultsInCsv(Long postId) {
        return DownloadFileUtil.ok(adminPostFacade.downloadPostPollResultsInCsv(postId));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> duplicatePosts(Long postId, Long stockGroupId) {

        final long startTime = System.currentTimeMillis();
        final int duplicatePostCount = postFacade.duplicatePosts(postId, stockGroupId);
        final long endTime = System.currentTimeMillis();

        final double executionTimeInSecond = (endTime - startTime) / 1000.0;
        final String returnMessage = "%s초 동안 %s건의 게시글을 복제하였습니다.".formatted(executionTimeInSecond, duplicatePostCount);
        log.info(returnMessage);

        return SimpleStringResponseUtil.okResponse(returnMessage);
    }

    @Override
    public ResponseEntity<PostDetailsDataResponse> duplicatePost(Long postId, PostCopyRequest postCopyRequest) {
        return ResponseEntity.ok(postFacade.duplicatePost(postId, postCopyRequest));
    }
}
