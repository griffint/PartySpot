package com.myapp.partyspot.handlers;

import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

/**
 * Created by svaughan on 8/13/2015.
 */
public class PlaylistHandler {
    private static PlaylistHandler mInstance;

    public SpotifyTracks suggestedSongs; // a list of the suggested songs
    public String playlistName; // stores the name of the playlist that the app is following or hosting

    public PlaylistHandler() {
        this.suggestedSongs = new SpotifyTracks();
        this.playlistName = "";
    }

    public static PlaylistHandler getHandler() {
        if (mInstance == null) {
            mInstance = new PlaylistHandler();
        }
        return mInstance;
    }
}
