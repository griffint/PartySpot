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
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;

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


    public void pushToFirebase(String playlistName, String currentlyPlayingURI, String songName, int songTime, boolean playerState) {
        Firebase playlists = firebaseDatabase.child(playlistName);
        playlists.child("currentlyPlaying").setValue(currentlyPlayingURI);     //this should be set to push the uri of the current song
        playlists.child("songTime").setValue(songTime);
        playlists.child("title").setValue(songName);
        playlists.child("playerState").setValue(playerState);
        playlists.child("timestamp").setValue(new Date().getTime());
        //now  use .put to insert the current playlist data and push it to firebase
    }

    public void pullFromFirebase(String playlist){
        //print tests for pulling form firebase on changes

        Firebase playlistRef = firebaseDatabase.child(playlist);
        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String uri = (String) snapshot.child("currentlyPlaying").getValue();
                int songTime = ((Long) snapshot.child("songTime").getValue()).intValue();
                Boolean playerState = (Boolean) snapshot.child("playerState").getValue();
                Long timestamp = (Long) snapshot.child("timestamp").getValue();
                FirebaseHandler.this.activity.spotifyHandler.synchronize(uri, timestamp, songTime, playerState);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void validatePlaylistHost(final String playlist) {
        this.firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.hasChild(playlist)) {
                    activity.spotifyHandler.setNotHostOrSlave();
                    Context context = FirebaseHandler.this.activity;
                    CharSequence text = "Playlist already exists";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    activity.playlistName = playlist;
                    activity.changeToChoosePlaylistToHostFragment();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void validatePlaylist(final String playlist) {
        this.firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.hasChild(playlist)) {
                    activity.playlistName = playlist;
                    if (FirebaseHandler.this.activity.spotifyHandler.isSlave) {
                        activity.changeToSlaveMainFragment();
                        FirebaseHandler.this.pullFromFirebase(playlist);
                    } else {
                        activity.changeToSuggesterAddFragment();
                    }
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

    public void pushSuggestion(String currentPlaylist, SpotifyTrack track){
/*        track is a spotify track class, consists of
        public String uri;
        public String name;
        public String artist;
        this will need to extract the relevant info from the track class to send to firebase*/
        String trackName = track.name;
        String uri=track.uri;
        String artist=track.artist;

        //Here we create a child of the current playlist called suggestions, then create a child of that that is the song's name
        Firebase suggestions = firebaseDatabase.child(currentPlaylist).child("suggestions").child(trackName);
        //then feed in the uri and artist as children of the trackname
        suggestions.child("uri").setValue(uri);
        suggestions.child("artist").setValue(artist);
   }

    public SpotifyTrack pullSuggestion(String playlist){
        /*this function will pull the data down from firebase about a given selection
        and will output a SpotifyTrack object
        We're putting this seperate from the rest of pulling from firebase, because we want people who aren't
        slave or master phones to be able to suggest songs
        */
        Firebase playlistSuggestions = firebaseDatabase.child(playlist);    //setting up firebase reference
        playlistSuggestions.addValueEventListener(new ValueEventListener() {        //looks for a change in data
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String title = (String) snapshot.child("curre").getValue();
                String uri = (String) snapshot.child("uri").getValue();
                String artist = (String) snapshot.child("artist").getValue();

                //makes a new SpotifyTrack with the output data from the firebase pull
                //there are potential asynchronous issues here
                SpotifyTrack outputTrack = new SpotifyTrack(title,uri,artist);
                // return outputTrack;
            }



            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        return null;
    }
}
