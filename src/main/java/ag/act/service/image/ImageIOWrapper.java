package ag.act.service.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Component
public class ImageIOWrapper {

    public Optional<ByteArrayOutputStream> write(BufferedImage bufferedImage, String extension) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, extension, byteArrayOutputStream);
        } catch (IOException e) {
            log.warn("Failed to write output stream: {}", e.getMessage(), e);
            return Optional.empty();
        }
        return Optional.of(byteArrayOutputStream);
    }

    public Optional<BufferedImage> read(InputStream inputStream) {
        try {
            return Optional.ofNullable(ImageIO.read(inputStream));
        } catch (IOException e) {
            log.warn("Failed to read input stream: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }
}
