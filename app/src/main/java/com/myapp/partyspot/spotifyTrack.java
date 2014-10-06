package com.myapp.partyspot;

/**
 * Created by svaughan on 10/5/14.
 */
public class spotifyTrack {
    public String uri;
    public String name;

    public spotifyTrack(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return this.name;
    }

    public String getUri() {
        return this.uri;
    }
}
