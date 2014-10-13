package com.myapp.partyspot.handlers;

import android.content.Intent;
import android.util.Log;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;

import java.util.ArrayList;
import java.util.Date;

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
    public SpotifyTracks playingTracks;
    public int songIndex;
    public boolean isHost;
    public boolean isSlave;

    public SpotifyHandler(MainActivity activity) {
        this.isHost = false;
        this.mPlayer = null;
        this.activity = activity;
        this.paused = false;
        this.onPlaylist = false;
        this.playlistOwner = "bgatkinson";
        this.playlistId = "4KekJB2Z8CE0EhUDiKzHUU";
        this.playingTracks = new SpotifyTracks();
        this.songIndex = -1;
        this.isSlave = false;

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
        mPlayer.addPlayerNotificationCallback(new PlayerNotificationCallback() {
            @Override
            public void onPlaybackEvent(EventType eventType) {
                if (SpotifyHandler.this.isHost) {
                    if (eventType == EventType.PAUSE) {
                        String playlist = SpotifyHandler.this.activity.playlistName;
                        String song = SpotifyHandler.this.playingTracks.tracks.get(SpotifyHandler.this.songIndex).getUri();
                        int time = mPlayer.getPlaybackPosition();
                        boolean playing = false;
                        Log.v(playlist, song);
                        SpotifyHandler.this.activity.firebaseHandler.pushToFirebase(playlist, song, time, playing);
                    }
                    if (eventType == EventType.PLAY) {
                        String playlist = SpotifyHandler.this.activity.playlistName;
                        String song = SpotifyHandler.this.playingTracks.tracks.get(SpotifyHandler.this.songIndex).getUri();
                        int time = mPlayer.getPlaybackPosition();
                        boolean playing = true;
                        Log.v(playlist, song);
                        SpotifyHandler.this.activity.firebaseHandler.pushToFirebase(playlist, song, time, playing);
                    }

                    // We're only allowing the user to go forward, so call this as if it means onNextSong:
                    if (eventType == EventType.TRACK_CHANGED) {
                        SpotifyHandler.this.songIndex += 1;
                        String playlist = SpotifyHandler.this.activity.playlistName;
                        String song = getSongUri();
                        int time = getSongPosInMs();
                        boolean playing = true;
                        Log.v(playlist, song);
                        SpotifyHandler.this.activity.firebaseHandler.pushToFirebase(playlist, song, time, playing);
                    }
                }
            }
        });
    }

    public void synchronize(String songUri, long timestamp, int origSongPos, boolean playerState) {
        long current_time = new Date().getTime();
        int diff = (int) (current_time-timestamp);
        int newSongPos = origSongPos+diff;
        mPlayer.play(songUri);
        mPlayer.seekToPosition(newSongPos);
        if (!playerState) {
            mPlayer.pause();
        }
    }

    public void setHost() {
        this.isHost = true;
    }

    public void setSlave() {
        this.isSlave = true;
    }

    public void setNotHostOrSlave() {
        this.isHost = false;
        this.isSlave = false;
    }

    public void setPlaylist(String playlistOwner, String playlistId) {
        this.playlistOwner = playlistOwner;
        this.playlistId = playlistId;
    }

    public String getSongTitle() {
        return this.playingTracks.tracks.get(songIndex).getName();
    }

    public int getSongPosInMs() {
        return this.mPlayer.getPlaybackPosition();
    }

    public String getSongUri() {
        return this.playingTracks.tracks.get(songIndex).getUri();
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

    public void queue(SpotifyTrack track) {
        // needs work
        mPlayer.queue(track.getUri());
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
