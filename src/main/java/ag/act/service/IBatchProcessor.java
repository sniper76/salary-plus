package ag.act.service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface IBatchProcessor {

    record BatchProcessorParameters(
        Consumer<Integer> batchCountLog,
        AtomicInteger updateCount,
        AtomicInteger createCount,
        int chunkSize
    ) {

    }
}
