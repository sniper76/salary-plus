package ag.act.handler.admin;

import ag.act.api.AdminImageUploadApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.facade.FileFacade;
import ag.act.model.ImageUploadResponse;
import ag.act.validator.ImageMediaTypeValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@UseGuards({IsActiveUserGuard.class})
public class AdminImageUploadApiDelegateImpl implements AdminImageUploadApiDelegate {

    private final FileFacade fileFacade;
    private final ImageMediaTypeValidator imageMediaTypeValidator;

    public AdminImageUploadApiDelegateImpl(FileFacade fileFacade, ImageMediaTypeValidator imageMediaTypeValidator) {
        this.fileFacade = fileFacade;
        this.imageMediaTypeValidator = imageMediaTypeValidator;
    }

    @Override
    public ResponseEntity<ImageUploadResponse> uploadImageAdmin(String fileContentType, MultipartFile file, String description) {
        imageMediaTypeValidator.validate(file);

        final FileContentType fileContentTypeEnum = FileContentType.fromValue(fileContentType);

        return ResponseEntity.ok(fileFacade.uploadImage(file, fileContentTypeEnum, FileType.IMAGE, description));
    }
}
