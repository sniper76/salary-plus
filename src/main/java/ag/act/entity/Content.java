package ag.act.entity;

public interface Content {
    Long getId();

    String getTitle();

    ag.act.model.Status getStatus();

    String getDeletedMessage();

    String getDeletedByAdminMessage();

    String getReportedMessage();
    
    String getExclusiveToHoldersMessage();

    Boolean isDeletedByUser();
}
