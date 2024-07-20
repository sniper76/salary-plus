package ag.act.enums;

public enum LinkType {
    RANKING("랭킹", "https://blog.naver.com/conduit_act/223233886536");

    private final String linkTitle;
    private final String initialUrl;

    LinkType(String linkTitle, String initialUrl) {
        this.linkTitle = linkTitle;
        this.initialUrl = initialUrl;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public String getInitialUrl() {
        return initialUrl;
    }

    public static LinkType fromValue(String name) {
        try {
            return LinkType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 링크 타입 이름입니다.", e);
        }
    }
}
