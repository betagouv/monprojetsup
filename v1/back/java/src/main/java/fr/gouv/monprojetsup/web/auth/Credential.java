package fr.gouv.monprojetsup.web.auth;

import jakarta.xml.bind.DatatypeConverter;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import static javax.crypto.SecretKeyFactory.getInstance;

public record Credential (String salt,
                          String hash//hashed
) {
    public static Credential getNewCredential(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //https://www.baeldung.com/java-password-hashing
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String base64Salt = DatatypeConverter.printBase64Binary(salt);
        String base64Hash = computeHash(password, base64Salt);
        return new Credential(base64Salt, base64Hash);
    }

    public static String computeHash(String id, String salt64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt =  DatatypeConverter.parseBase64Binary(salt64);
        KeySpec spec = new PBEKeySpec(id.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return DatatypeConverter.printBase64Binary(hash);
    }

}
