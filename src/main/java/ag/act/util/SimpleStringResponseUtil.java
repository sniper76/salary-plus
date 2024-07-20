package ag.act.util;

import ag.act.model.SimpleStringResponse;
import org.springframework.http.ResponseEntity;

public class SimpleStringResponseUtil {

    public static ResponseEntity<SimpleStringResponse> okResponse(String status) {
        return ResponseEntity.ok(ok(status));
    }

    public static ResponseEntity<SimpleStringResponse> okResponse() {
        return okResponse("ok");
    }

    public static SimpleStringResponse ok() {
        return new SimpleStringResponse().status("ok");
    }

    public static SimpleStringResponse ok(String status) {
        return new SimpleStringResponse().status(status);
    }
}
