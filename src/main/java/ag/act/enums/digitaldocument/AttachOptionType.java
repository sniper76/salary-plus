package ag.act.enums.digitaldocument;

import ag.act.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

public enum AttachOptionType {
    REQUIRED {
        @Override
        public void validate(List<MultipartFile> images, String fileType) {
            if (CollectionUtils.isEmpty(images) || images.get(0) == null) {
                throw new BadRequestException("전자문서 필수 파일 정보가 없습니다. -%s".formatted(fileType));
            }
        }
    },
    OPTIONAL {
        @Override
        public void validate(List<MultipartFile> images, String fileType) {

        }
    },
    NONE {
        @Override
        public void validate(List<MultipartFile> images, String fileType) {
            if (images != null && images.stream().anyMatch(Objects::nonNull)) {
                throw new BadRequestException(String.format("%s는 필수 파일이 아닙니다.", fileType));
            }
        }
    };

    public static AttachOptionType fromValue(String type) {
        if (type == null) {
            return NONE;
        }
        try {
            return AttachOptionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 AttachOptionType '%s' 타입입니다.".formatted(type));
        }
    }

    public abstract void validate(List<MultipartFile> images, String fileType);
}
