package com.myapp.partyspot.handlers;

import com.firebase.client.Firebase;
import com.myapp.partyspot.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by svaughan on 10/2/14.
 */
public class FirebaseHandler {
    // This fragment handles the firebase. It can update currently playing song, or pull currently playing song
    //it'll  have methods for both pushing and pulling data.

    // class fields
    public Firebase firebaseDatabase;
    public String URL;
    public MainActivity activity;

    public FirebaseHandler(MainActivity activity) {
        this.activity = activity;
        this.URL = "https://partyspot.firebaseIO.com";
        this.firebaseDatabase = new Firebase(this.URL);
    }

    /*
    //hostData class will hold all the info that the host needs to be pushing to firebase
    public class hostData {
        public String playlistName;     //self explanatory
        public String currentlyPlaying;     //currently playing song - calculated or actual data?
        public float songTime;      //how far into currently playing song the host is
        public boolean playerState;     //true=playing, false=paused

        //constructor stuff
        public hostData(String playlistName, String currentlyPlaying, float songTime, boolean playerState) {
            this.playlistName = playlistName;
            this.currentlyPlaying = currentlyPlaying;
            this.songTime = songTime;
            this.playerState = playerState;
        }
    }*/

    public void pushToFirebase(String playlistName, String currentlyPlaying, int songTime, boolean playerState) {
        Firebase playlists = firebaseDatabase.child(playlistName);
        playlists.child(playlistName).child("currentlyPlaying").setValue(currentlyPlaying);
        playlists.child(playlistName).child("songTime").setValue(songTime);
        playlists.child(playlistName).child("playerState").setValue(playerState);
        //now  use .put to insert the current playlist data and push it to firebase
    }

    public void pullFromFirebase(){
        
    }
}
