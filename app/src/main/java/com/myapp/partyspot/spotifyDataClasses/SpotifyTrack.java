package com.myapp.partyspot.spotifyDataClasses;

/**
 * Created by svaughan on 10/5/14.
 */
public class SpotifyTrack {
    public String uri;
    public String name;
    public String artist;

    public SpotifyTrack(String name, String uri, String artist) {
        this.name = name;
        this.uri = uri;
        this.artist = artist;
    }

    public String getName() {
        return this.name;
    }

    public String getUri() {
        return this.uri;
    }

    public String getArtist() {
        return this.artist;
    }
}
