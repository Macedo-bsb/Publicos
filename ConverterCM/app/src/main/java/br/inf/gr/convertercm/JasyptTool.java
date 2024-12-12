package br.inf.gr.convertercm;

import org.jasypt.util.text.AES256TextEncryptor;

public class JasyptTool {

    public static String criptografar(String texto, String senha) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(senha);
        return textEncryptor.encrypt(texto);
    }

    public static String descriptografar(String textoCriptografado, String senha) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(senha);
        return textEncryptor.decrypt(textoCriptografado);
    }
}
