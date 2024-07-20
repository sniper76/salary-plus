package ag.act.repository.interfaces;

public interface PollItemCount extends JoinCount {
    Long getPollId();

    Long getPollItemId();

    Long getUserId();
}
