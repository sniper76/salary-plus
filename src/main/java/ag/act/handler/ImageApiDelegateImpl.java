package ag.act.handler;

import ag.act.api.ImageApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.FileFacade;
import ag.act.model.DefaultProfileImagesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsActiveUserGuard.class})
public class ImageApiDelegateImpl implements ImageApiDelegate {

    private final FileFacade fileFacade;

    public ImageApiDelegateImpl(FileFacade fileFacade) {
        this.fileFacade = fileFacade;
    }

    @Override
    public ResponseEntity<DefaultProfileImagesResponse> getDefaultProfileImages() {
        return ResponseEntity.ok(fileFacade.getDefaultProfileImages());
    }
}
