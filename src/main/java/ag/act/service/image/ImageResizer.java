package ag.act.service.image;

import ag.act.enums.MediaType;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageResizer {
    private static final int TARGET_SIZE = 200;
    private final ScalrClient scalrClient;
    private final ImageIOWrapper imageIOWrapper;
    private static final List<String> SUPPORTED_EXTENSIONS = MediaType.getSupportedExtensions();

    @Nonnull
    public Optional<ImageResizeResult> resize(BufferedImage originalImage) {
        BufferedImage resizeImage = scalrClient.resize(originalImage, TARGET_SIZE);
        //TODO 현재 cms 에서 강제로 jpg 로 이미지가 업로드 되고 있어서 원본이미지의 확장자와 맞지 않아 0byte 파일이 만들어지는 문제를 처리함
        return SUPPORTED_EXTENSIONS.parallelStream()
            .map(extension -> writeImage(extension, resizeImage))
            .filter(Optional::isPresent)
            .filter(it -> it.get().byteArrayOutputStream().size() > 0)
            .findFirst()
            .orElseGet(Optional::empty);
    }

    private Optional<ImageResizeResult> writeImage(String extension, BufferedImage resizeImage) {
        try {
            return Optional.of(new ImageResizeResult(
                imageIOWrapper.write(resizeImage, extension).orElseGet(ByteArrayOutputStream::new),
                extension
            ));
        } catch (Exception e) {
            // ignore
            return Optional.empty();
        }
    }

    public boolean isResizeNeeded(BufferedImage image) {
        return TARGET_SIZE < image.getWidth();
    }
}