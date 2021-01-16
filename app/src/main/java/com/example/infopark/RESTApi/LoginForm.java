package com.example.infopark.RESTApi;

public class LoginForm {
    private final String username;

    private final String email;

    private final String password;

    private final boolean googleUser;

    public LoginForm(String username, String email, String password, boolean googleUser) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.googleUser = googleUser;
    }
}
