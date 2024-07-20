package ag.act.dto.file;

import ag.act.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFilePathDto {
    private String originalPath;
    private String uploadPath;
    private String mimeType;
    private FileType fileType;
    private long fileSize;
}
