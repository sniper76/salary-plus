package ag.act.service.digitaldocument.modusign;

import ag.act.entity.DigitalProxy;
import ag.act.entity.DigitalProxyApproval;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.service.post.PostService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DigitalProxyApprovalService {
    private final PostService postService;

    public DigitalProxyApprovalService(PostService postService) {
        this.postService = postService;
    }

    public Optional<DigitalProxyApproval> findMyDigitalProxyApproval(List<DigitalProxyApproval> digitalProxyApprovalList, Long userId) {
        return Optional.ofNullable(digitalProxyApprovalList)
            .flatMap(digitalProxyApprovals ->
                digitalProxyApprovals.stream()
                    .filter(digitalProxyApproval -> digitalProxyApproval.getUserId().equals(userId))
                    .findFirst()
            );
    }

    public void addDigitalProxyApproval(Post post, DigitalProxy digitalProxy, User user, ModuSignDocument document) {

        if (CollectionUtils.isEmpty(digitalProxy.getDigitalProxyApprovalList())) {
            digitalProxy.setDigitalProxyApprovalList(new ArrayList<>());
        }

        DigitalProxyApproval approval = new DigitalProxyApproval();
        approval.setUserId(user.getId());
        approval.setStatus(ag.act.model.Status.ACTIVE);
        approval.setDocumentId(document.getId());
        approval.setParticipantId(document.getParticipantId());
        digitalProxy.getDigitalProxyApprovalList().add(approval);

        postService.savePost(post);
    }
}
