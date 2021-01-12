package com.example.infopark.RESTApi;


public class RegisterForm {


    private final String userName;

    private final String email;

    private final String password;

    private final LatLng savedLocation;

    private final int trustPoints;

    private final int creditPoints;

    private final boolean confirmed;

    private final boolean googleUser;

    public RegisterForm(String userName, String email, String password, LatLng savedLocation,
                        int trustPoints, int creditPoints, boolean confirmed, boolean googleUser) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.savedLocation = savedLocation;
        this.trustPoints = trustPoints;
        this.creditPoints = creditPoints;
        this.confirmed = confirmed;
        this.googleUser = googleUser;
    }
}
