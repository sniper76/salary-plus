package ag.act.service.stockboardgrouppost.postpush;

public interface PostPushUpdater {

    boolean supports(PostPushUpdaterInput input);

    void update(PostPushUpdaterInput input);
}
