package ag.act.dto;

public interface HtmlContent {
    String getContent();

    void setContent(String content);

    default Boolean getIsEd() {
        return false;
    }
}
