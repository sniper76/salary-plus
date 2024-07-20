package ag.act.configuration.initial.caching;

public interface CachingScheduler extends Caching {

    void run();

    void clear();
}
