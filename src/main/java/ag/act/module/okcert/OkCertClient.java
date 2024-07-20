package ag.act.module.okcert;

import kcb.module.v3.OkCert;
import kcb.module.v3.exception.OkCertException;
import org.springframework.stereotype.Component;

@Component
public class OkCertClient {

    public String callOkCert(String target, String cpCd, String svcName, String license, String param) throws OkCertException {
        return new OkCert().callOkCert(target, cpCd, svcName, license, param, null);
    }
}
