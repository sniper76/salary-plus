package ag.act.dto.digitaldocument;

import ag.act.model.CreatePostRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class HolderListReadAndCopyDocumentDto {
    private String stockCode;
    private String boardGroupName;
    private CreatePostRequest createPostRequest;
    private MultipartFile signImage;
    private MultipartFile idCardImage;
    private List<MultipartFile> bankAccountImages;
    private MultipartFile hectoEncryptedBankAccountPdf;
}
