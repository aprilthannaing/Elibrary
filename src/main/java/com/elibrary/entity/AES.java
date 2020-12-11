package com.elibrary.entity;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	static String PLAIN_TEXT = "SS18617710213463"; 
    static String ENCRYPTION_KEY = "mykey@91mykey@91";
    static String INITIALIZATIO_VECTOR = "AODVNUASDNVVAOVF";
    public static void main(String [] args) {
        try {

            System.out.println("Plain text: " + PLAIN_TEXT);

            byte[] encryptedMsg = encrypt(PLAIN_TEXT, ENCRYPTION_KEY);
            String base64Encrypted = Base64.getEncoder().encodeToString(encryptedMsg);
            System.out.println("Encrypted: "+  base64Encrypted);

            byte[] base64Decrypted = Base64.getDecoder().decode(base64Encrypted);
            String decryptedMsg = decrypt(base64Decrypted, ENCRYPTION_KEY);
            System.out.println("Decrypted: " + decryptedMsg);
        } catch (Exception e) { 
            e.printStackTrace();
        } 
    }

    public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/pkcs5padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(INITIALIZATIO_VECTOR.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
      }

      public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/pkcs5padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(INITIALIZATIO_VECTOR.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText),"UTF-8");
      }
}
