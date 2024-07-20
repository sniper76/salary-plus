package ag.act.exception;

import ag.act.entity.WebVerification;
import lombok.Getter;

@Getter
public class NotYetVerifiedException extends ActWarningException {

    private final WebVerification webVerification;

    public NotYetVerifiedException(WebVerification webVerification) {
        super("Not yet verified web verification.");
        this.webVerification = webVerification;
    }
}
