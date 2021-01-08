package com.example.infopark.RESTApi;

public class LoginForm {
    private final String username;

    private final String email;

    private final String password;

    public LoginForm(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
