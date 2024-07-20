package ag.act.module.mydata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Component
public class MyDataCryptoHelper {
    private static final String AES = "AES";
    private static final String AES_PADDING = "AES/CBC/PKCS5Padding";
    private final String secretKey;

    public MyDataCryptoHelper(@Value("${external.mydata.aes256key}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String encrypt(String str) throws GeneralSecurityException {

        final Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        final byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(encrypted));
    }

    public String decrypt(String str) throws GeneralSecurityException {

        final Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        final byte[] byteStr = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
        return new String(cipher.doFinal(byteStr), StandardCharsets.UTF_8);
    }

    private Cipher getCipher(int encryptMode) throws GeneralSecurityException {
        final byte[] keyData = java.util.Base64.getDecoder().decode(secretKey);
        final String iv = secretKey.substring(0, 16);

        final SecretKey secureKey = new SecretKeySpec(keyData, AES);
        final Cipher cipher = Cipher.getInstance(AES_PADDING);

        cipher.init(encryptMode, secureKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));

        return cipher;
    }
}
