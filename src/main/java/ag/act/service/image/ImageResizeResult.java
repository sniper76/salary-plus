package ag.act.service.image;

import java.io.ByteArrayOutputStream;

public record ImageResizeResult(ByteArrayOutputStream byteArrayOutputStream, String extension) {
    public byte[] toByteArray() {
        return byteArrayOutputStream.toByteArray();
    }
}
