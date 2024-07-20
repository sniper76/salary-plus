package ag.act.module.okcert;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"test-persistence", "test"})
public class TestOkCertResourceManager implements IOkCertResourceManager {
    @Override
    public String getSiteName() {
        return "test";
    }

    @Override
    public String getSiteUrl() {
        return "test";
    }

    @Override
    public String getReqSvcName() {
        return "test";
    }

    @Override
    public String getResSvcName() {
        return "test";
    }

    @Override
    public String getTarget() {
        return "test";
    }

    @Override
    public String getCpCd() {
        return "test";
    }

    @Override
    public String getOkCertLicencePath() {
        return "test";
    }
}
