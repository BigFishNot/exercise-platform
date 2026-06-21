package com.exercise.campus.component;

import com.exercise.campus.enums.ResponseCodeEnum;
import com.exercise.campus.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 对称加密（用于 SMTP 授权码等需要解密的场景）
 * 格式: base64(iv) + ":" + base64(cipher)
 */
@Slf4j
public class EncryptUtil {

    private static final String ALG = "AES/CBC/PKCS5Padding";
    /** AES key, 必须 16/24/32 字节 */
    private static final String KEY = "exercise-16bytes"; // 16 bytes
    private static final SecureRandom RNG = new SecureRandom();

    public static String encrypt(String plain) {
        if (plain == null) return null;
        try {
            byte[] iv = new byte[16];
            RNG.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("[encrypt] failed: {}", e.getMessage());
            throw new BusinessException(ResponseCodeEnum.MAIL_PASSWORD_DECRYPT_FAIL);
        }
    }

    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) return null;
        try {
            String[] parts = cipherText.split(":");
            if (parts.length != 2) {
                throw new BusinessException(ResponseCodeEnum.MAIL_PASSWORD_DECRYPT_FAIL);
            }
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] data = Base64.getDecoder().decode(parts[1]);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("[decrypt] failed: {}", e.getMessage());
            throw new BusinessException(ResponseCodeEnum.MAIL_PASSWORD_DECRYPT_FAIL);
        }
    }
}