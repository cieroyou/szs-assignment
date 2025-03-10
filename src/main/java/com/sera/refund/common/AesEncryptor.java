package com.sera.refund.common;

import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AesEncryptor {

    private static final String AES = "AES";
    private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef"; // 32바이트 키 (AES-256)

    // AES-256 암호화
    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.ENCRYPT_EXCEPTION);
        }

    }

    // AES-256 복호화
    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.ENCRYPT_EXCEPTION);
        }

    }
}
