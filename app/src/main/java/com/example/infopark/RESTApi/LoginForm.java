package com.example.infopark.RESTApi;

/**
 * This class holds the login data used to send as a request to the server via retrofit RESTApi.
 */
public class LoginForm {
    private final String username;

    private final String email;

    private final String password;

    private final boolean googleUser;

    /**
     * his function constructs a new login form.
     * @param username the user name to login with.
     * @param email the email to login with.
     * @param password the password to login with.
     * @param googleUser indicates if the user is trying to login as a google user.
     */
    public LoginForm(String username, String email, String password, boolean googleUser) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.googleUser = googleUser;
    }
}
