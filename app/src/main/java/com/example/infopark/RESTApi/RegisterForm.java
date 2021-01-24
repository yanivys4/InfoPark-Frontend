package com.example.infopark.RESTApi;


public class RegisterForm {


    private final String userName;

    private final String email;

    private final String password;

    private final String salt;

    private final LatitudeLongitude savedLocation;

    private final int trustPoints;

    private final int creditPoints;

    private final boolean confirmed;

    private final boolean googleUser;

    private final String confirmationUniqueID;

    public RegisterForm(String userName, String email, String password, String salt, LatitudeLongitude savedLocation,
                        int trustPoints, int creditPoints, boolean confirmed, boolean googleUser, String confirmationUniqueID) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.savedLocation = savedLocation;
        this.trustPoints = trustPoints;
        this.creditPoints = creditPoints;
        this.confirmed = confirmed;
        this.googleUser = googleUser;
        this.confirmationUniqueID = confirmationUniqueID;

    }

    public boolean getGoogleUser(){
        return googleUser;
    }
}