package com.example.sbbTest;

import java.util.Random;

public class PasswordGenerator {
    private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_LENGTH = 8;

    public static String getPasswordChars(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0;i<PASSWORD_LENGTH;i++){
            int index = random.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(index));
        }
        return sb.toString();
    }
}
