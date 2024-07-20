package ag.act.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthCheckApiDelegateImpl implements ag.act.api.HealthCheckApiDelegate {

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> healthCheck() {
        return ResponseEntity.ok(new ag.act.model.SimpleStringResponse().status("ok"));
    }
}