package br.inf.gr.convertercm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BouncyCastleTool {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String criptografar(String texto, String senha, int keySize) throws Exception {
        SecretKey secretKey = generateKey(senha, keySize);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String descriptografar(String textoCriptografado, String senha, int keySize) throws Exception {
        SecretKey secretKey = generateKey(senha, keySize);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(textoCriptografado));
        return new String(decryptedBytes);
    }

    private static SecretKey generateKey(String senha, int keySize) throws Exception {
        byte[] keyBytes = new byte[keySize / 8]; // Use 128, 192, or 256 bits key
        byte[] passwordBytes = senha.getBytes();
        System.arraycopy(passwordBytes, 0, keyBytes, 0, Math.min(passwordBytes.length, keyBytes.length));
        return new SecretKeySpec(keyBytes, "AES");
    }
}
