package com.myapp.partyspot.handlers;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myapp.partyspot.R;
import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by svaughan on 9/30/14.
 */

public class PlaybackHandler {
    private static PlaybackHandler mInstance;

    public Context context;
    public boolean muted; // whether the app is muted or not
    public boolean playing; // whether the spotify player is currently streaming music

    private Player mPlayer;
    public boolean paused;
    public boolean onPlaylist;
    public String playlistOwner;
    public String playlistId;
    public SpotifyTracks playingTracks;
    public int songIndex;
    public boolean isHost;
    public boolean isSlave;
    public long timestamp;
    public int origSongPos;
    public String currentlyPlaying;

    public static PlaybackHandler getHandler() {
        if(mInstance == null)
        {
            mInstance = new PlaybackHandler();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PlaybackHandler() {
        this.isHost = false;
        this.paused = false;
        this.onPlaylist = false;
        this.playlistOwner = "";
        this.playlistId = "";
        this.playingTracks = new SpotifyTracks();
        this.isSlave = false;
        this.timestamp = 0;
        this.origSongPos = 0;
        this.songIndex = 0;
        this.currentlyPlaying = "";
        this.playing = false;
        this.muted = false;
    }

    public void setPlayer(Player player) {
        this.mPlayer = player;

        mPlayer.addPlayerNotificationCallback(new PlayerNotificationCallback() {
            @Override
            public void onPlaybackEvent(EventType eventType, PlayerState state) {
                if (PlaybackHandler.this.isHost) {
                    if (eventType == EventType.PAUSE) {
                        String playlist = PlaylistHandler.getHandler().playlistName;
                        String songUri = state.trackUri;
                        String song = PlaybackHandler.this.playingTracks.getTitleFromUri(songUri);
                        song = song + " - "+ PlaybackHandler.this.playingTracks.getArtistFromTitle(song);
                        int time = state.positionInMs;
                        FirebaseHandler.getHandler().pushToFirebase(playlist, songUri, song, time, !PlaybackHandler.this.paused);
                    }
                    if (eventType == EventType.AUDIO_FLUSH) {
                        String playlist = PlaylistHandler.getHandler().playlistName;
                        String songUri = state.trackUri;
                        String song = PlaybackHandler.this.playingTracks.getTitleFromUri(songUri);
                        song = song + " - "+ PlaybackHandler.this.playingTracks.getArtistFromTitle(song);
                        int time = state.positionInMs;
                        //TODO: fix sketch
                        View rootView = ((MainActivity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                        ((TextView)rootView.findViewById(R.id.currently_playing)).setText(song);
                        FirebaseHandler.getHandler().pushToFirebase(playlist, songUri, song, time, !PlaybackHandler.this.paused);
                    } else if (eventType == EventType.PLAY) {
                        String playlist = PlaylistHandler.getHandler().playlistName;
                        String songUri = state.trackUri;
                        String song = PlaybackHandler.this.playingTracks.getTitleFromUri(songUri);
                        song = song + " - "+ PlaybackHandler.this.playingTracks.getArtistFromTitle(song);
                        int time = state.positionInMs;
                        View rootView = ((MainActivity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                        ((TextView)rootView.findViewById(R.id.currently_playing)).setText(song);
                        FirebaseHandler.getHandler().pushToFirebase(playlist, songUri, song, time, !PlaybackHandler.this.paused);
                    }

                    // We're only allowing the user to go forward, so call this as if it means onNextSong:
                    if (eventType == EventType.END_OF_CONTEXT) {
                        PlaybackHandler.this.songIndex += 1;
                        PlaybackHandler.this.mPlayer.play(PlaybackHandler.this.playingTracks.tracks.get(PlaybackHandler.this.songIndex).getUri());
                        ((MainActivity)context).displayCurrentQueue(PlaybackHandler.this.songIndex);
                    }
                } else if (PlaybackHandler.this.isSlave) {
                    if (eventType == EventType.AUDIO_FLUSH) {
                        long current_time = new Date().getTime();
                        int diff = (int) (current_time-PlaybackHandler.this.timestamp);
                        int newPos = diff+PlaybackHandler.this.origSongPos;
                        if ((Math.abs(newPos-state.positionInMs))>150) {
                            mPlayer.seekToPosition(newPos);
                            PlaybackHandler.getHandler().setNotMuted();
                        }
                    }
                }
            }

            @Override
            public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {

            }
        });
    }

    public void setNotMuted() {
        if (this.muted) { // if it is muted, set to be not muted
            this.muted = false;
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamMute(AudioManager.STREAM_MUSIC, this.muted);
        }
    }

    public void setMuted() {
        if (!this.muted) { // if it is not muted, set to be muted
            this.muted = true;
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamMute(AudioManager.STREAM_MUSIC, this.muted);
        }
    }

    public SpotifyTracks getSongsToEnd(Integer i){
        SpotifyTracks returnTracks = new SpotifyTracks();
        Integer size = playingTracks.tracks.size();
        for (int index = i;index< size;index++){
            returnTracks.addTrack(playingTracks.tracks.get(index));
        }
        return returnTracks;

    }

    public void synchronize(String songUri, long timestamp, int origPos, boolean playerState) {
        if (timestamp != this.timestamp) {
            this.timestamp = timestamp;
            PlaybackHandler.getHandler().setMuted();
            mPlayer.play(songUri);
            this.origSongPos = origPos;
            if (!playerState) {
                mPlayer.pause();
            }
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

    public ArrayList<String> getTrackUriArray() {
        return this.playingTracks.makeUriArray();
    }

    public void play() {
        if (this.paused && this.onPlaylist) {
            this.paused = false;
            resume();
        } else if (!this.playingTracks.tracks.isEmpty()) {
            this.paused = false;
            mPlayer.play(this.getTrackUriArray().get(this.songIndex));
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

    public void queueNext(SpotifyTrack track) {
        this.playingTracks.appendNext(track, this.songIndex);
    }

    public void queueLast(SpotifyTrack track) {
        this.playingTracks.appendLast(track);
    }
}
