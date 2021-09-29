package com.feanaro.lindale_api.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Random;

public class PasswordUtils {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String SALT = "tMzZgy=m)ZIz86j_.+iSmZ/_?99+r(g8JHWvm9pdw";

    private static String genRdmPws(int len) {
        String chars = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz"
                + "&~#{[(-|_^@°+=}$¤£*µ%!§:/;.,?<>";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public static String uniqGenRdmHtag(List<String> htags) {
        int begin = 2;
        String initHtag = genRdmPws(begin);
        if (htags.size() > 0) {
            while (htags.contains(initHtag)) {
                begin = begin + 1;
                initHtag = genRdmPws(begin);
            }
        }
        return initHtag;
    }

    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSecurePassword(String password) {
        String returnValue = null;
        byte[] securePassword = hash(password.toCharArray(), SALT.getBytes());
        returnValue = Base64.getEncoder().encodeToString(securePassword);
        return returnValue;
    }

    public static boolean verifyUserPassword(String providedPassword, String securedPassword) {
        boolean returnValue = false;
        String newSecurePassword = generateSecurePassword(providedPassword);
        returnValue = newSecurePassword.equals(securedPassword);
        return returnValue;
    }
}