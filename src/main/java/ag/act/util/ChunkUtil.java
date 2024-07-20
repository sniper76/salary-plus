package ag.act.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChunkUtil {

    public <T> List<List<T>> getChunks(List<T> items, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < items.size(); i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, items.size());
            List<T> chunk = items.subList(i, endIndex);
            chunks.add(chunk);
        }
        return chunks;
    }
}
