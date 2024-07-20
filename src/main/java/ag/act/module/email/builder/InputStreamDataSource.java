package ag.act.module.email.builder;

import ag.act.exception.ActRuntimeException;
import org.springframework.http.MediaType;

import javax.activation.DataSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamDataSource implements DataSource {

    private final String fileName;
    private final byte[] bytes;

    public InputStreamDataSource(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        try {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new ActRuntimeException("An error occurred while reading bytes from InputStream", e);
        }
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @Override
    public String getName() {
        return fileName;
    }
}
