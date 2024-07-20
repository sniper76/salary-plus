package ag.act.module.okcert;

import ag.act.service.aws.S3Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Getter
@Component
@Profile({"default", "local", "dev", "prod"})
public class OkCertResourceManager implements IOkCertResourceManager {

    private final String target;
    private final String cpCd;
    private final String reqSvcName;
    private final String resSvcName;
    private final String siteName;
    private final String siteUrl;
    private final String licenseFileName;
    private final String okCertLicencePath;
    private final S3Service s3Service;

    public OkCertResourceManager(
        @Value("${external.kcb.target:PROD}") String target,
        @Value("${external.kcb.cp-cd:V60880000000}") String cpCd,
        @Value("${external.kcb.req-svc-name:IDS_HS_EMBED_SMS_REQ}") String reqSvcName,
        @Value("${external.kcb.res-svc-name:IDS_HS_EMBED_SMS_CIDI}") String resSvcName,
        @Value("${external.kcb.site-name:act}") String siteName,
        @Value("${external.kcb.site-url:act.ag}") String siteUrl,
        @Value("${external.kcb.license-file:license.dat}") String licenseFileName,
        S3Service s3Service
    ) throws IOException {
        this.target = target;
        this.cpCd = cpCd;
        this.reqSvcName = reqSvcName;
        this.resSvcName = resSvcName;
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.licenseFileName = licenseFileName;
        this.s3Service = s3Service;

        try {
            this.okCertLicencePath = getResourcePath(s3Service.readObject(licenseFileName));
        } catch (IOException e) {
            log.error("Failed to read OkCert License file - {}", e.getMessage(), e);
            throw e;
        }
    }

    private String getResourcePath(InputStream inputStream) throws IOException {
        final String[] resourceFileNameInfo = licenseFileName.split("\\.");
        final File tempFile = File.createTempFile(resourceFileNameInfo[0], "." + resourceFileNameInfo[1]);

        try {
            FileUtils.copyInputStreamToFile(inputStream, tempFile);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return tempFile.getAbsolutePath();
    }
}
