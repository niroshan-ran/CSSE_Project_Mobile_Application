package com.csse.mobileapp.ui.login;

import java.io.Serializable;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private String userEmail;
    //... other data fields that may be accessible to the UI

    public String getUserEmail() {
        return userEmail;
    }

    public LoggedInUserView(String displayName, String userEmail) {
        this.displayName = displayName;
        this.userEmail = userEmail;
    }


    public String getDisplayName() {
        return displayName;
    }
}