package ag.act.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class CryptoHelper {
    private final SecretKey key;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public CryptoHelper(
        @Value("${security.token.secret-key}") String secretKey,
        @Value("${security.token.secret-salt}") String secretSalt
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = generateSecretKey(secretKey, secretSalt);
    }

    public String encrypt(String input) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
            .encodeToString(cipherText);
    }

    public String decrypt(String cipherText) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
            .decode(cipherText));
        return new String(plainText);
    }

    private SecretKey generateSecretKey(String secretToken, String secretSalt)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = secretSalt.getBytes();
        int iterationCount = 10000;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(secretToken.toCharArray(), salt, iterationCount, 256);
        SecretKey secretKey = secretKeyFactory.generateSecret(spec);

        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
    }
}
