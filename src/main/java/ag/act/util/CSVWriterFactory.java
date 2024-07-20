package ag.act.util;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

@Component
public class CSVWriterFactory {

    public CSVWriter create(OutputStream outputStream) {
        return new CSVWriter(
            new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
            ',',
            NO_QUOTE_CHARACTER, // '"'
            '\'',
            "\n"
        );
    }
}
