package ag.act.configuration.security;

import ag.act.util.DateTimeUtil;
import ag.act.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {
    private final String secretKey;
    private final long appExpirationDateInDays;
    private final long webExpirationDateInDays;
    private final TokenUtil tokenUtil;

    public TokenProvider(
        @Value("${security.token.secret-key}") String secretKey,
        @Value("${security.token.expiration-date-in-days.app}") long appExpirationDateInDays,
        @Value("${security.token.expiration-date-in-days.web}") long webExpirationDateInDays,
        TokenUtil tokenUtil
    ) {
        this.secretKey = secretKey;
        this.appExpirationDateInDays = appExpirationDateInDays;
        this.webExpirationDateInDays = webExpirationDateInDays;
        this.tokenUtil = tokenUtil;
    }

    public String createAppToken(String subject) {
        return create(subject, appExpirationDateInDays);
    }

    public String createWebToken(String subject) {
        return create(subject, webExpirationDateInDays);
    }

    @SuppressWarnings("deprecation")
    private String create(String subject, long expirationDateInDays) {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setSubject(subject)
            .setIssuer("act.ag")
            .setIssuedAt(DateTimeUtil.newDate())
            .setExpiration(DateTimeUtil.getFutureDateFromCurrentInstant(expirationDateInDays))
            .compact();
    }

    @SuppressWarnings("deprecation")
    public Long validateAndGetUserId(String token) {
        final Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return Long.valueOf(claims.getSubject());
    }

    public String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return tokenUtil.parse(bearerToken);
    }

}
