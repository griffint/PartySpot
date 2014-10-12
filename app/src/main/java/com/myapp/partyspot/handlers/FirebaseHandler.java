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
    //hostData class will hold all the info that the host needs to be pushing to firebase
      public class hostData {
                public String playlistName;     //self explanatory
                public String currentlyPlaying;     //currently playing song - calculated or actual data?
                public float songTime;      //how far into currently playing song the host is
                public float timeStamp;     //
                public boolean playerState;     //true=playing, false=paused

                //constructor bullshit
                public hostData(String playlistName, String currentlyPlaying, float songTime, float timeStamp, boolean playerState ){
                    this.playlistName = playlistName;
                    this.currentlyPlaying = currentlyPlaying;
                    this.songTime = songTime;
                    this.timeStamp = timeStamp;
                    this.playerState = playerState;
                }

                //cuz it's 3 am
                public String suckMyDick(){
                    return "JIIIZZZZZZZZZ";
                }

            }

               public void pushToFirebase(String playlistName, String currentlyPlaying, float songTime, float timeStamp, boolean playerState ){
                   hostData currentData = new hostData(playlistName, currentlyPlaying, songTime, timeStamp, playerState);
                   //now set up hashmap of all hosted playlists
                   Map<String, hostData> hostedPlaylists = new HashMap<String, hostData>();
                   //now  use .put to insert the current playlist data
                   hostedPlaylists.put("", alanisawesome);


}
