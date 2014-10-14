package com.myapp.partyspot.spotifyDataClasses;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by svaughan on 10/5/14.
 */
public class SpotifyTracks {
    public ArrayList<SpotifyTrack> tracks;

    public SpotifyTracks() {
        this.tracks = new ArrayList<SpotifyTrack>();
    }

    public void addTrack(SpotifyTrack track) {
        this.tracks.add(track);
    }

    public void shuffleTracks() {
        Random rnd = new Random();
        for (int i = this.tracks.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            SpotifyTrack a = this.tracks.get(index);
            this.tracks.set(index, this.tracks.get(i));
            this.tracks.set(i, a);
        }
    }

    public ArrayList<String> makeNameArray() {
        ArrayList<String> names = new ArrayList<String>();
        for (int i=0; i<this.tracks.size(); i++) {
            names.add(this.tracks.get(i).getName());
        }
        return names;
    }

    public ArrayList<String> makeNameWithArtistArray() {
        ArrayList<String> names = new ArrayList<String>();
        for (int i=0; i<this.tracks.size(); i++) {
            names.add(this.tracks.get(i).getName()+" - "+this.tracks.get(i).getArtist());
        }
        return names;
    }

    public ArrayList<String> makeUriArray() {
        ArrayList<String> uris = new ArrayList<String>();
        for (int i=0; i<this.tracks.size(); i++) {
            uris.add(this.tracks.get(i).getUri());
        }
        return uris;
    }

    public String getUriFromTitle(String title) {
        for (int i=0; i<this.tracks.size(); i++) {
            if (this.tracks.get(i).getName().equals(title)) {
                return this.tracks.get(i).getUri();
            }
        }
        return null;
    }

    public String getArtistFromTitle(String title) {
        for (int i=0; i<this.tracks.size(); i++) {
            if (this.tracks.get(i).getName().equals(title)) {
                return this.tracks.get(i).getArtist();
            }
        }
        return null;
    }

    public String getTitleFromUri(String uri) {
        for (int i=0; i<this.tracks.size(); i++) {
            if (this.tracks.get(i).getUri().equals(uri)) {
                return this.tracks.get(i).getName();
            }
        }
        return null;
    }

    public SpotifyTrack getTrackFromTitle(String title) {
        for (int i=0; i<this.tracks.size(); i++) {
            if (this.tracks.get(i).getName().equals(title)) {
                return this.tracks.get(i);
            }
            if ((this.tracks.get(i).getName()+" - "+this.tracks.get(i).getArtist()).equals(title)) {
                return this.tracks.get(i);
            }
        }
        return null;
    }

    public void appendLast(SpotifyTrack track) {
        this.addTrack(track);
    }

    public void appendNext(SpotifyTrack track, int currentIndex) {
        Log.v(this.tracks.get(currentIndex).getName(), "NO");
        Log.v(track.getUri(), "NO");
        this.tracks.add(currentIndex+1, track);
    }
}
