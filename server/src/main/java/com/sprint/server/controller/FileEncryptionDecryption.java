package com.sprint.server.controller;

import com.sprint.common.response.HttpApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileEncryptionDecryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SALT = "ssshhhhhhhhhhh!!!!";
    private static final String key = "TjWnZr4u7x!z%C*F";

    private static IvParameterSpec iv;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] bytes = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        this.iv = new IvParameterSpec(bytes, 0, 16);
    }

    public void encrypt(File inputFile, File outputFile)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        log.info("Encrypting");
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] keyDigest = DigestUtils.sha256(key);

        Key secretKey = new SecretKeySpec(key.getBytes(), 0, keyDigest.length, ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static String decrypt(byte[] bytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

        byte[] keyDigest = DigestUtils.sha256(key);

        Key secretKey = new SecretKeySpec(key.getBytes(), 0, keyDigest.length, ALGORITHM);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(new String(bytes)));
        return new String(plainText);
    }

    public static void encrypt1(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt1(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static byte[] doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
        try {

            // SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // KeySpec spec = new PBEKeySpec(key.toCharArray(), SALT.getBytes(), 65536, 256);
            // SecretKey tmp = factory.generateSecret(spec);
            // SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(cipherMode, secretKey, iv);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = inputStream.readAllBytes();

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();
            return outputBytes;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            log.error("Error encrypting/decrypting file, Error: ", ex);
            throw new Exception("Error encrypting/decrypting file", ex);
        }
    }

    @GetMapping("/encrypt")
    public HttpApiResponse encryptApi() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {

            File inputFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/sampleImage.jpg");
            File outputFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/sampleImage.jpg.enc");

            // File inputFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/original.txt");
            // File outputFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/encrypted.txt");

            encrypt1(key, inputFile, outputFile);
            return new HttpApiResponse("Encrypted the file successfully");
        } catch (Exception e) {
            log.error("[encryptApi] ERROR while encrypting the file: ", e);
            return new HttpApiResponse(false, null, null);
        }
    }

    @GetMapping("/decrypt")
    public HttpApiResponse decryptApi() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {
            File encryptedFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/sampleImage.jpg.enc");
            File decryptedFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/decryptedImage.jpg");

            // File encryptedFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/encrypted.txt");
            // File decryptedFile = new File("/Users/upstox/IdeaProjects/spring-boiler-plate/decrypted.txt");

            if (!decryptedFile.exists()) {
                decryptedFile.createNewFile();
            }

            decrypt1(key, encryptedFile, decryptedFile);
            return new HttpApiResponse("Decrypted content: " + decryptedFile);
        } catch (Exception e) {
            log.error("[encryptApi] ERROR while encrypting the file: ", e);
            return new HttpApiResponse(false, null, null);
        }
    }

    @GetMapping("/encryptanddecrypt")
    public HttpApiResponse encryptAndDecrypt() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        encryptApi();
        decryptApi();
        return new HttpApiResponse("Performed encryption and decryption");
    }

}
