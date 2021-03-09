package com.example.infopark.RESTApi;

/**
 * this class holds data in the response of a retrofit RESTApi call.
 *
 */
public class ResponseMessage {
    // this data member indicated if the fetch has succeeded.
    private Boolean success;
    // this member holds the description if the fetch had failed.
    private String description;

    public Boolean getSuccess() {
        return success;
    }

    public String getDescription() {
        return description;
    }
}
