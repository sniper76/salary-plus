package ag.act.service.image;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ScalrClient {

    public BufferedImage resize(BufferedImage image, int targetSize) {
        return Scalr.resize(image, targetSize);
    }
}
