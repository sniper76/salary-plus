package ag.act.dto;

import ag.act.model.CreatePostRequest;
import ag.act.model.UpdatePostRequest;

public interface CampaignHtmlContent {
    default CreatePostRequest getCreatePostRequest() {
        return null;
    }

    default UpdatePostRequest getUpdatePostRequest() {
        return null;
    }
}
