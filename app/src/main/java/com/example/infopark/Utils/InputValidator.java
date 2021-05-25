package com.example.infopark.Utils;

import android.content.Context;

import com.example.infopark.R;

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

    public static String getPasswordMistakes(String password, Context context) {
        String passwordMistakes = "";
        String mistake = null;
        if (password.length() > 15 || password.length() < 8) {
            passwordMistakes += "- " ;
            mistake = context.getString(R.string.password_length);
            passwordMistakes += mistake += "\n";

        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            passwordMistakes += "- " ;
            mistake = context.getString(R.string.password_uppercase);
            passwordMistakes += mistake += "\n";
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            passwordMistakes += "- " ;
            mistake = context.getString(R.string.password_lowercase);
            passwordMistakes += mistake += "\n";
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            passwordMistakes += "- " ;
            mistake = context.getString(R.string.password_number);
            passwordMistakes += mistake += "\n";

        }
        String specialChars = "(.*[@,#,$,%].*$)";
        if (!password.matches(specialChars)) {

            passwordMistakes += "- " ;
            mistake = context.getString(R.string.password_special);
            passwordMistakes += mistake += "\n";
        }

        return passwordMistakes;
    }
}
