package com.continuum.cms.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class PasswordGenerator {

    private static final String UPPER_CASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final String ALL_CHARS = UPPER_CASE_CHARS + LOWER_CASE_CHARS + NUMERIC_CHARS + SPECIAL_CHARS;

    private static final Random random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder();

        // Add at least one character from each character set
        password.append(getRandomChar(UPPER_CASE_CHARS));
        password.append(getRandomChar(LOWER_CASE_CHARS));
        password.append(getRandomChar(NUMERIC_CHARS));
        password.append(getRandomChar(SPECIAL_CHARS));

        // Add remaining characters
        for (int i = 4; i < 8; i++) {
            password.append(getRandomChar(ALL_CHARS));
        }

        // Shuffle the characters to make it random
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int randomIndex = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[randomIndex];
            passwordArray[randomIndex] = temp;
        }

        return new String(passwordArray);
    }

    private static char getRandomChar(String charSet) {
        int randomIndex = random.nextInt(charSet.length());
        return charSet.charAt(randomIndex);
    }
}
