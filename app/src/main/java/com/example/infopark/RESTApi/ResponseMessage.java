package com.example.infopark.RESTApi;

/**
 * this class holds data in the response of a retrofit RESTApi call.
 *
 */
public class ResponseMessage {
    // this member holds the description if the fetch had failed.
    private String description;

    public String getDescription() {
        return description;
    }
}
