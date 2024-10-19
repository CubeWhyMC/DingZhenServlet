package fuck.manthe.nmsl.util;

import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class CryptoUtil {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Value("${service.gateway.key}")
    String gatewayKeyString;

    SecretKey gatewayKey;

    @PostConstruct
    public void init() {
        gatewayKey = toKey(gatewayKeyString);
    }

    public String encryptGateway(@NotNull String plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        return encrypt(plaintext, gatewayKey);
    }

    public String decryptGateway(@NotNull String ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return decrypt(ciphertext, gatewayKey);
    }

    public String encrypt(@NotNull String plaintext, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParams = new IvParameterSpec(getInitializationVector(key));
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String ciphertext, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParams = new IvParameterSpec(getInitializationVector(key));
        cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted);
    }

    public byte[] getInitializationVector(SecretKey secretKey) {
        byte[] iv = new byte[16]; // 128-bit IV
        System.arraycopy(secretKey.getEncoded(), 0, iv, 0, iv.length); // Simple IV for demonstration, should be random in production
        return iv;
    }

    public SecretKey toKey(String key) {
        return new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
    }
}
