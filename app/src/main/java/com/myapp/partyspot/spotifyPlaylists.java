package com.myapp.partyspot;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by svaughan on 10/5/14.
 */
public class spotifyPlaylists {
    ArrayList<spotifyPlaylist> playlists;

    public spotifyPlaylists() {
        this.playlists = new ArrayList<spotifyPlaylist>();
    }

    public void addPlaylists(spotifyPlaylist playlist) {
        this.playlists.add(playlist);
    }

    public ArrayList<String> makeNameArray() {
        ArrayList<String> names = new ArrayList<String>();
        for (int i=0; i<this.playlists.size(); i++) {
            names.add(this.playlists.get(i).getName());
        }
        return names;
    }

    public ArrayList<String> makeIdArray() {
        ArrayList<String> ids = new ArrayList<String>();
        for (int i=0; i<this.playlists.size(); i++) {
            ids.add(this.playlists.get(i).getId());
        }
        return ids;
    }

    public ArrayList<String> makeOwnerArray() {
        ArrayList<String> owners = new ArrayList<String>();
        for (int i=0; i<this.playlists.size(); i++) {
            owners.add(this.playlists.get(i).getOwner());
        }
        return owners;
    }

    public String getOwnerFromTitle(String title) {
        for (int i=0; i<this.playlists.size(); i++) {
            if (this.playlists.get(i).getName().equals(title)) {
                return this.playlists.get(i).getOwner();
            }
        }
        return null;
    }

    public String getUriFromTitle(String title) {
        for (int i=0; i<this.playlists.size(); i++) {
            if (this.playlists.get(i).getName().equals(title)) {
                return this.playlists.get(i).getId();
            }
        }
        return null;
    }
}
