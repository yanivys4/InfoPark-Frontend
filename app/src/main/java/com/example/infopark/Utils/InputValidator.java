package com.example.infopark.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InputValidator {

    private static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";


    public static boolean isEmailValid(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static String getPasswordMistakes(String password) {
        String passwordMistakes = "";

        if (password.length() > 15 || password.length() < 8) {
            passwordMistakes += "- Password must be less than 15 and more than 8 characters in length\n";

        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            passwordMistakes += "- Password must have at least one uppercase character\n";

        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            passwordMistakes += "- Password must have at least one lowercase character\n";

        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            passwordMistakes += "- Password must have at least one number\n";

        }
        String specialChars = "(.*[@,#,$,%].*$)";
        if (!password.matches(specialChars)) {
            passwordMistakes += "- Password must have at least one special character among @#$%\n";

        }

        return passwordMistakes;
    }
}
