package com.example.infopark.RESTApi;

/**
 * This class holds the register data used to send as a request to the server via retrofit RESTApi.
 */
public class RegisterForm {

    private final String userName;

    private final String email;

    private final String password;

    private final String salt;

    private final LatitudeLongitude savedLocation;

    private final boolean confirmed;

    private final boolean googleUser;

    private final String uniqueID;

    /**
     * This function constructs a new register form.
     * @param userName the userName to register with
     * @param email the email to register with
     * @param password the password to register with
     * @param salt the salt to parse the password
     * @param savedLocation the saved location of the user
     * @param confirmed indicate if the user is confirmed user
     * @param googleUser indicates if the user is trying to register as a google user.
     * @param uniqueID the user unique id
     */
    public RegisterForm(String userName, String email, String password, String salt, LatitudeLongitude savedLocation
            , boolean confirmed, boolean googleUser, String uniqueID) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.savedLocation = savedLocation;
        this.confirmed = confirmed;
        this.googleUser = googleUser;
        this.uniqueID = uniqueID;

    }

    /**
     * This function return the google user parameter that indicate if this user is
     * regular user or google user
     * @return boolean that indicate if its a regular or google user
     */
    public boolean getGoogleUser(){
        return googleUser;
    }
}