package ag.act.handler;

import ag.act.api.ImageUploadApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.facade.FileFacade;
import ag.act.validator.ImageMediaTypeValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@UseGuards({IsActiveUserGuard.class})
public class ImageUploadApiDelegateImpl implements ImageUploadApiDelegate {

    private final FileFacade fileFacade;
    private final ImageMediaTypeValidator imageMediaTypeValidator;

    public ImageUploadApiDelegateImpl(FileFacade fileFacade, ImageMediaTypeValidator imageMediaTypeValidator) {
        this.fileFacade = fileFacade;
        this.imageMediaTypeValidator = imageMediaTypeValidator;
    }

    @Override
    public ResponseEntity<ag.act.model.ImageUploadResponse> uploadImage(MultipartFile file) {
        imageMediaTypeValidator.validate(file);

        return ResponseEntity.ok(fileFacade.uploadImage(file, FileContentType.DEFAULT, FileType.IMAGE));
    }
}
