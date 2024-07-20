package ag.act.entity;

public enum LeaderStatus {
    ELECTED("Elected"),
    ELECTION_IN_PROGRESS("Election in progress");

    private final String name;

    LeaderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}