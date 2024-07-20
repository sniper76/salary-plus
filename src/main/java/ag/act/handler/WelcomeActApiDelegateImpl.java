package ag.act.handler;

import ag.act.api.WelcomeActApiDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class WelcomeActApiDelegateImpl implements WelcomeActApiDelegate {
    @Override
    public ResponseEntity<String> welcomeActHome() {
        return ResponseEntity.ok("Welcome to Act API!");
    }
}
