package com.myapp.partyspot.handlers;

import com.myapp.partyspot.spotifyDataClasses.SpotifyUser;

/**
 * Created by svaughan on 8/13/2015.
 */
public class UserHandler {
    private static UserHandler mInstance = null;

    public boolean loggedIn; // whether the user is logged in
    public String accessToken; // Authentication token from spotify
    public boolean isSpotifyUser;
    public SpotifyUser user;

    public static UserHandler getHandler(){
        if(mInstance == null)
        {
            mInstance = new UserHandler();

        }
        return mInstance;
    }

    public UserHandler() {
        this.loggedIn=false;
        this.accessToken = null;
        this.isSpotifyUser = false;
        this.user = null;
    }

    public void initializeWithToken(String token) {
        this.accessToken = token;
        this.loggedIn = true;
        this.isSpotifyUser = true;
        getUser();
    }

    public void getUser() {
        try {
            HTTPFunctions.getUser(); // will get the user and then set the fragment to be loaded
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void setUser(SpotifyUser user) {
        this.user = user;
    }
}
