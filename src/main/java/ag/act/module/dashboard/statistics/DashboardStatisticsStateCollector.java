package ag.act.module.dashboard.statistics;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class DashboardStatisticsStateCollector {
    private final Map<String, DashboardStatisticsState> stateMap;

    public DashboardStatisticsStateCollector() {
        stateMap = new ConcurrentHashMap<>();
    }

    public void addState(DashboardStatisticsState state) {
        stateMap.put(state.getName(), state);
    }

    public String getFailMessage() {
        return stateMap.values()
            .stream()
            .filter(it -> !it.isSuccess())
            .map(DashboardStatisticsState::toString)
            .collect(Collectors.joining("\n"));
    }
}
