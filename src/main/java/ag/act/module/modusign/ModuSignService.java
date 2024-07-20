package ag.act.module.modusign;


import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.entity.User;
import ag.act.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ModuSignService {

    private final ModuSignHttpClientUtil moduSignHttpClientUtil;
    private final CryptoHelper cryptoHelper;
    private final int retryCount;

    public ModuSignService(
        @Value("${external.modusign.retry-count:10}") int retryCount,
        ModuSignHttpClientUtil moduSignHttpClientUtil,
        CryptoHelper cryptoHelper
    ) {
        this.retryCount = retryCount;
        this.moduSignHttpClientUtil = moduSignHttpClientUtil;
        this.cryptoHelper = cryptoHelper;
    }

    public ModuSignDocument requestSignature(String templateId, String templateName, String templateRole) {
        final User user = ActUserProvider.getNoneNull();
        try {
            final ModuSignRequest request = ModuSignRequest.builder()
                .name(user.getName())
                .phoneNumber(cryptoHelper.decrypt(user.getHashedPhoneNumber()))
                .email(user.getEmail())
                .templateId(templateId)
                .templateName(templateName)
                .templateRole(templateRole)
                .build();
            return requestSignature(request);
        } catch (GeneralSecurityException e) {
            log.error("Failed to decrypt user's phone number. user: {}", user, e);
            throw new InternalServerException("모두사인 호출 중에 알 수 없는 오류가 발생하였습니다.", e);
        } catch (Exception e) {
            log.error("Error occurred while requesting signature. user: {}", user, e);
            throw new InternalServerException("모두사인 호출 중에 알 수 없는 오류가 발생하였습니다.", e);
        }

    }

    public ModuSignDocument requestSignature(ModuSignRequest request) {
        return moduSignHttpClientUtil.post("/documents/request-with-template", createRequestMap(request));
    }

    public String getEmbeddedUrl(String documentId, String participantId) {
        final String url = "/documents/%s/participants/%s/embedded-view".formatted(documentId, participantId);
        return moduSignHttpClientUtil.get(url).getEmbeddedUrl();
    }

    public String getEmbeddedUrlInRetry(String documentId, String participantId) {
        for (int i = 0; i < retryCount; i++) {
            try {
                final String embeddedUrl = getEmbeddedUrl(documentId, participantId);
                if (embeddedUrl != null) {
                    return embeddedUrl;
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Failed to sleep in Retry", e);
            } catch (RuntimeException e) {
                log.error("Failed to get embedded url from modusign. documentId: {}, participantId: {} in Retry", documentId, participantId, e);
            }
        }

        log.error("Failed to get embedded url from modusign. documentId: {}, participantId: {}", documentId, participantId);
        throw new InternalServerException("의결권위임 문서를 가져오는 중에 알수 없는 오류가 발생하였습니다.");
    }

    private Map<String, Object> createRequestMap(ModuSignRequest request) {
        // TODO Should change this logic later when we implement our own system.
        return Map.of(
            "templateId", request.getTemplateId(),
            "document", Map.of(
                "title", request.getTemplateName(),
                "participantMappings", List.of(
                    Map.of(
                        "role", request.getTemplateRole(),
                        "name", request.getName(),
                        "signingMethod", Map.of(
                            "type", "SECURE_LINK",
                            "value", request.getEmail()
                        ),
                        "verification", Map.of(
                            "mobileIdentification", Map.of(
                                "name", request.getName(),
                                "phoneNumber", request.getPhoneNumber()
                            )
                        )
                    )
                )
            )
        );
    }
}