package week_02;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class DESCipher {
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    private DESCipher() {}

    private static SecretKeySpec key(String key) {
        CipherUtil.requireKey(key);
        byte[] k = CipherUtil.utf8(key);
        if (k.length != 8) {
            throw new IllegalArgumentException("Khóa DES phải đúng 8 byte (gõ 8 ký tự không dấu). Hiện có " + k.length + " byte.");
        }
        return new SecretKeySpec(k, "DES");
    }

    public static String encrypt(String plaintext, String key) throws Exception {
        byte[] iv = CipherUtil.randomBytes(8);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key(key), new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(CipherUtil.utf8(plaintext));
        return CipherUtil.b64(CipherUtil.concat(iv, encrypted));
    }

    public static String decrypt(String ciphertext, String key) throws Exception {
        byte[] all = CipherUtil.fromB64(ciphertext);
        if (all.length < 8 + 8) {
            throw new IllegalArgumentException("Bản mã quá ngắn hoặc bị hỏng.");
        }
        byte[] iv = java.util.Arrays.copyOfRange(all, 0, 8);
        byte[] data = java.util.Arrays.copyOfRange(all, 8, all.length);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key(key), new IvParameterSpec(iv));
        return CipherUtil.text(cipher.doFinal(data));
    }
}