package com.myapp.partyspot.handlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Path;
import com.firebase.client.snapshot.ChildName;
import com.firebase.client.snapshot.Node;
import com.firebase.client.snapshot.NodePriority;
import com.myapp.partyspot.activities.MainActivity;

import java.util.Date;
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
        playlists.child("currentlyPlaying").setValue(currentlyPlaying);     //this should be set to push the uri of the current song
        playlists.child("songTime").setValue(songTime);
        playlists.child("playerState").setValue(playerState);
        playlists.child("timestamp").setValue(new Date().getTime());
        //now  use .put to insert the current playlist data and push it to firebase
    }

    public void pullFromFirebase(String playlist){
        //print tests for pulling form firebase on changes
        //this will pull all data from the firebase, isn't what we want in our final project
        Firebase playlistRef = firebaseDatabase.child(playlist);
        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.v("DICKS","DICKS");
                String uri = (String) snapshot.child("currentlyPlaying").getValue();
                Integer songTime = (Integer) snapshot.child("SongTime").getValue();
                Boolean playerState = (Boolean) snapshot.child("playerState").getValue();
                Long timestamp = (Long) snapshot.child("currentlyPlaying").getValue();
                Log.v(uri, Long.toString(timestamp));
                FirebaseHandler.this.activity.spotifyHandler.synchronize(uri, timestamp, songTime, playerState);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void validatePlaylist(final String playlist) {
        this.firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                Log.v("FUCK ME", Integer.toString((int) snapshot.getChildrenCount()));
                if (snapshot.hasChild(playlist)) {
                    activity.playlistName = playlist;
                    if (FirebaseHandler.this.activity.spotifyHandler.isSlave) {
                        activity.changeToSlaveMainFragment();
                        FirebaseHandler.this.pullFromFirebase(playlist);
                    } else {
                        activity.changeToSuggesterAddFragment();
                    }
                    Context context = FirebaseHandler.this.activity;
                    CharSequence text = "Playlist exists";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    activity.spotifyHandler.setNotHostOrSlave();
                    Context context = FirebaseHandler.this.activity;
                    CharSequence text = "Playlist doesn't exist";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /*
    firebaseDatabase.addChildEventListener(new ChildEventListener() {
        // Retrieve new posts as they are added to Firebase
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
            Map<String, Object> newPost = (Map<String, Object>) snapshot.getValue();
            System.out.println("Author: " + newPost.get("author"));
            System.out.println("Title: " + newPost.get("title"));
        }
        //... ChildEventListener also defines onChildChanged, onChildRemoved,
        //    onChildMoved and onCanceled, covered in later sections.
    });
    */
}
