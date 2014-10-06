package com.myapp.partyspot;

import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;

import java.util.ArrayList;

/**
 * Created by svaughan on 9/30/14.
 */
public class SpotifyHandler implements
        PlayerNotificationCallback, ConnectionStateCallback {
    // This handles all of the handling of the Spotify playback

    private static final String CLIENT_ID = "3e85d4f69cfb4ede9bca519fb86ce216";
    private static final String REDIRECT_URI = "partyspot://partyspot";

    private Player mPlayer;
    public MainActivity activity;
    public boolean paused;
    public boolean onPlaylist;
    public String playlistOwner;
    public String playlistId;
    public spotifyTracks playingTracks;

    public SpotifyHandler(MainActivity activity) {
        this.mPlayer = null;
        this.activity = activity;
        this.paused = false;
        this.onPlaylist = false;
        this.playlistOwner = "bgatkinson";
        this.playlistId = "4KekJB2Z8CE0EhUDiKzHUU";
        this.playingTracks = new spotifyTracks();

        Spotify spotify = activity.getSpotify();
        mPlayer = spotify.getPlayer(activity, "My Company Name", this, new Player.InitializationObserver() {
            @Override
            public void onInitialized() {
                Log.e("MainActivity", "Worked");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public void setPlaylist(String playlistOwner, String playlistId) {
        this.playlistOwner = playlistOwner;
        this.playlistId = playlistId;
    }

    protected void login_return(Intent intent) {
    }

    public String getSongTitle() {
        return "";
    }

    public int getSongPosInMs() {
        return 1;
    }

    public String getSongUri() {
        return "";
    }

    public ArrayList<String> getTrackUriArray() {
        return this.playingTracks.makeUriArray();
    }

    public void play() {
        if (this.paused && this.onPlaylist) {
            resume();
            this.paused = false;
        } else {
            mPlayer.play(this.getTrackUriArray());
            this.onPlaylist = true;
        }
    }

    public void resume() {
        mPlayer.resume();
    }

    public void pause() {
        mPlayer.pause();
        this.paused = true;
    }

    public void fastForward() {
        mPlayer.seekToPosition(100000);
    }

    public void next() {
        mPlayer.skipToNext();
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MainActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // TODO: Handle event type as necessary
            default:
                break;
        }
    }

    public void destroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
    }
}
