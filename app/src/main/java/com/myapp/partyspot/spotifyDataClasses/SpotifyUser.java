package com.myapp.partyspot.spotifyDataClasses;

/**
 * Created by svaughan on 8/13/2015.
 */
public class SpotifyUser {
    public String user; // user's name
    public boolean premiumUser; // true if premium, false otherwise
    public String userType; //host, slave or suggester

    public SpotifyUser(String user, String userType, boolean premiumUser) {
        this.user = user;
        this.userType = userType;
        this.premiumUser = premiumUser;
    }
}
