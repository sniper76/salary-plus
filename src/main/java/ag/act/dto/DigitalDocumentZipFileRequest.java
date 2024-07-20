package ag.act.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalDocumentZipFileRequest {
    private String sourceBucketName;
    private List<String> sourceDirectories;
    private String destinationBucketName;
    private String destinationDirectory;
    private Long digitalDocumentDownloadId;
    private String password;
    private String zipFilename;
    private String fileType;
}
