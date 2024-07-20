package ag.act.dto.download;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFile {
    private Resource resource;
    private String fileName;
    private MediaType contentType;
}
