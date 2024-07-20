package ag.act.module.dart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class DartCorpCodeInputStreamReader {

    private final DartHttpClientUtil dartHttpClientUtil;

    public InputStream read() {
        final HttpResponse<InputStream> corpCodeZip = dartHttpClientUtil.getCorpCodeZip();
        return corpCodeZip.body();
    }
}