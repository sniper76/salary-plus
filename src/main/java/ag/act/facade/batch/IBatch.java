package ag.act.facade.batch;

import ag.act.util.DateTimeUtil;

@SuppressWarnings("checkstyle:MemberName")
public interface IBatch {
    int BATCH_SIZE = 100;
    IBatchName BatchName = new IBatchName() {
    };
    IBatchGroupName BatchGroupName = new IBatchGroupName() {
    };

    default String getCurrentFormattedDateTime() {
        return DateTimeUtil.getCurrentFormattedDateTime();
    }
}
