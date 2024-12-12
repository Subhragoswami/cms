package com.continuum.cms.util;

import com.continuum.cms.exceptions.CMSSecurityException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class EncryptionUtil {

    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_FACTORY_ALGORITHM = "AES";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static String secretUniqueKey = "continuum";
    private static final Map<String, String> DECRYPTION_CACHE = new ConcurrentHashMap<>();
    private static final SecretKey SECRET_KEY = generateSecretKey(secretUniqueKey);

    public static String decrypt(String encryptedText) {
        return decrypt(encryptedText, secretUniqueKey);
    }

    public static String decryptEmailAndMobile(String encryptedText) {
        return DECRYPTION_CACHE.computeIfAbsent(encryptedText, EncryptionUtil::decryptionForEmailMobile);
    }

    public static String encrypt(String plainText, String password) {
        try {
            SecretKey secretKey = generateSecretKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            byte[] ivBytes = new byte[16]; // Initialization Vector (IV) should be random
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] combinedBytes = new byte[ivBytes.length + encryptedBytes.length];

            System.arraycopy(ivBytes, 0, combinedBytes, 0, ivBytes.length);
            System.arraycopy(encryptedBytes, 0, combinedBytes, ivBytes.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combinedBytes);
        } catch (Exception e) {
            log.error("There is some error in encryption: {}", e.getMessage());
            throw new CMSSecurityException(ErrorConstants.ENCRYPTION_ERROR_CODE, ErrorConstants.ENCRYPTION_ERROR_MESSAGE);
        }
    }

    public static String decrypt(String encryptedText, String password) {
        try {
            SecretKey secretKey = generateSecretKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            byte[] encryptedBytesWithIV = Base64.getDecoder().decode(encryptedText);
            byte[] ivBytes = new byte[16];
            byte[] encryptedBytes = new byte[encryptedBytesWithIV.length - ivBytes.length];

            System.arraycopy(encryptedBytesWithIV, 0, ivBytes, 0, ivBytes.length);
            System.arraycopy(encryptedBytesWithIV, ivBytes.length, encryptedBytes, 0, encryptedBytes.length);

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("There is some error in decryption: {}", e.getMessage());

            throw new CMSSecurityException(ErrorConstants.DECRYPTION_ERROR_CODE, ErrorConstants.DECRYPTION_ERROR_MESSAGE);
        }
    }

    //Custom Decryption for email and mobile
    public static String decryptionForEmailMobile(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            byte[] encryptedBytesWithIV = Base64.getDecoder().decode(encryptedText);
            byte[] ivBytes = new byte[16];
            byte[] encryptedBytes = new byte[encryptedBytesWithIV.length - ivBytes.length];

            System.arraycopy(encryptedBytesWithIV, 0, ivBytes, 0, ivBytes.length);
            System.arraycopy(encryptedBytesWithIV, ivBytes.length, encryptedBytes, 0, encryptedBytes.length);

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("There is some error in decryption: {}", e.getMessage());

            throw new CMSSecurityException(ErrorConstants.DECRYPTION_ERROR_CODE, ErrorConstants.DECRYPTION_ERROR_MESSAGE);
        }
    }

    private static SecretKey generateSecretKey(String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), password.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), SECRET_KEY_FACTORY_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    public static Map<String, List<String>> bulkDecryptParallel(List<String> encryptedValues) {
        Map<String, List<String>> result = new ConcurrentHashMap<>();
        encryptedValues.parallelStream().forEach(value -> {
            String decryptedValue = decryptEmailAndMobile(value);
            result.computeIfAbsent(value, k -> new CopyOnWriteArrayList<>()).add(decryptedValue);
        });
        return result;
    }

}
