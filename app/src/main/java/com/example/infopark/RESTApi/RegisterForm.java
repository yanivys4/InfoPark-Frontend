package com.example.infopark.RESTApi;


public class RegisterForm {


    private final String userName;

    private final String email;

    private final String password;

    private final com.example.infopark.RESTApi.LatLng savedLocation;

    private final int trustPoints;

    private final int creditPoints;

    public RegisterForm(String userName, String email, String password, com.example.infopark.RESTApi.LatLng savedLocation,
                        int trustPoints, int creditPoints) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.savedLocation = savedLocation;
        this.trustPoints = trustPoints;
        this.creditPoints = creditPoints;
    }
}
