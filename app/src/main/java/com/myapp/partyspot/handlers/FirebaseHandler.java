package com.myapp.partyspot.handlers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.myapp.partyspot.R;
import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.fragments.HostFragment;
import com.myapp.partyspot.fragments.SlaveFragment;
import com.myapp.partyspot.fragments.SuggesterFragment;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

import java.util.Date;

/**
 * Created by svaughan on 10/2/14.
 */
public class FirebaseHandler {
    // This fragment handles the firebase. It can update currently playing song, or pull currently playing song
    //it'll  have methods for both pushing and pulling data.
    private static FirebaseHandler mInstance;

    // class fields
    public Firebase firebaseDatabase;
    public String URL;
    public Context context;

    public FirebaseHandler() {
        this.URL = "https://partyspot.firebaseIO.com";
        this.firebaseDatabase = new Firebase(this.URL);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static FirebaseHandler getHandler() {
        if (mInstance == null) {
            mInstance = new FirebaseHandler();
        }
        return mInstance;
    }

    public void pushToFirebase(String playlistName, String currentlyPlayingURI, String songName, int songTime, boolean playerState) {
        // this function is called by the host to push the current playlist status
        Firebase playlists = firebaseDatabase.child(playlistName);

        String[] invalid_characters = {".", "#", "$", "[", "]"};

        for(String str : invalid_characters){
            songName = songName.replace(str, "");
        }

        playlists.child("currentlyPlaying").setValue(currentlyPlayingURI);     //this should be set to push the uri of the current song
        playlists.child("songTime").setValue(songTime);
        playlists.child("title").setValue(songName);
        playlists.child("playerState").setValue(playerState);
        playlists.child("timestamp").setValue(new Date().getTime());
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
                String name = (String) snapshot.child("title").getValue();
                // TODO: don't update ui here
                View rootView = ((MainActivity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                ((TextView)rootView.findViewById(R.id.currently_playing)).setText(name);
                if (UserHandler.getHandler().isSpotifyUser) {
                    PlaybackHandler.getHandler().synchronize(uri, timestamp, songTime, playerState);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void validatePlaylistHost(final String playlist) {
        // checks whether the playlist exists. If it doesn't, redirect to host fragment
        this.firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once

                String[] invalid_characters = {".", "#", "$", "[", "]"};
                boolean valid = true;
                for(String str : invalid_characters){
                    if (playlist.contains(str)) {
                        valid = false;
                    }
                }

                if (!valid) {
                    PlaybackHandler.getHandler().setNotHostOrSlave();
                    CharSequence text = "Playlist can't contain '.', '#', '$', '[', or ']'";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if (snapshot.hasChild(playlist)) {
                    PlaybackHandler.getHandler().setNotHostOrSlave();
                    CharSequence text = "Playlist already exists";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    PlaylistHandler.getHandler().playlistName = playlist;
                    ((MainActivity)context).changeToChoosePlaylistToHostFragment();
                    FirebaseHandler.this.pullSuggestion(playlist);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void validatePlaylist(final String playlist) {
        // checks whether the playlist exists. If it does, redirect to slave fragment
        this.firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                if (snapshot.hasChild(playlist)) {
                    PlaylistHandler.getHandler().playlistName = playlist;
                    if (!UserHandler.getHandler().isSpotifyUser) {
                        ((MainActivity)context).changeToSuggesterFragment();
                        FirebaseHandler.this.pullSuggestion(playlist);
                        FirebaseHandler.this.pullFromFirebase(playlist);
                    } else if (PlaybackHandler.getHandler().isSlave) {
                        ((MainActivity)context).changeToSlaveFragment();
                        FirebaseHandler.this.pullFromFirebase(playlist);
                        FirebaseHandler.this.pullSuggestion(playlist);
                    } else {
                        ((MainActivity)context).changeToSuggesterFragment();
                        FirebaseHandler.this.pullSuggestion(playlist);
                        FirebaseHandler.this.pullFromFirebase(playlist);
                    }
                } else {
                    if (PlaybackHandler.getHandler() != null) {
                        PlaybackHandler.getHandler().setNotHostOrSlave();
                    }
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

        String[] invalid_characters = {".", "#", "$", "[", "]"};

        for(String str : invalid_characters){
            trackName = trackName.replace(str, "");
        }

        //Here we create a child of the current playlist called suggestions, then create a child of that that is the song's name
        Firebase suggestions = firebaseDatabase.child(currentPlaylist).child("suggestions").child(trackName);
        //then feed in the uri and artist as children of the trackname
        suggestions.child("uri").setValue(uri);
        suggestions.child("artist").setValue(artist);
   }

    public SpotifyTrack pullSuggestion(String playlist){
        /*this function will pull down any new suggestion from firebase when a suggestion is added
        */
        Firebase playlistSuggestions = firebaseDatabase.child(playlist).child("suggestions");    //setting up firebase reference

        playlistSuggestions.addChildEventListener(new ChildEventListener() {
            //this onChildAdded will add any new suggestions to the suggestion SpotifyTracks object
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                //get the SpotifyTrack object from data in firebase
                String trackname = snapshot.getName();
                String uri = (String) snapshot.child("uri").getValue();
                String artist = (String) snapshot.child("artist").getValue();
                
                //then add it to the SpotifyTracks object
                SpotifyTrack outputTrack = new SpotifyTrack(trackname, uri, artist);

                if (artist != null) {
                    PlaylistHandler.getHandler().suggestedSongs.addTrackIfNotDuplicate(outputTrack);
                }
                if (((MainActivity)context).fragment.equals("Suggester")) {
                    SuggesterFragment frag = (SuggesterFragment) ((MainActivity)context).getFragmentManager().findFragmentByTag("Suggester");
                    frag.displaySuggested(PlaylistHandler.getHandler().suggestedSongs);
                } else if (((MainActivity)context).fragment.equals("Slave")) {
                    SlaveFragment frag = (SlaveFragment) ((MainActivity)context).getFragmentManager().findFragmentByTag("Slave");
                    frag.displaySuggested(PlaylistHandler.getHandler().suggestedSongs);
                } else if (((MainActivity)context).fragment.equals("Host")) {
                    HostFragment frag = (HostFragment) ((MainActivity)context).getFragmentManager().findFragmentByTag("Host");
                    frag.displaySuggested(PlaylistHandler.getHandler().suggestedSongs);
                }

                //-----------IMPORTANT-----------------
                //SOMETHING SHOULD BE CALLED HERE TO ADD THE TRACK outputTrack TO suggestedSongs in mainactivity
                //------------IMPORTANT STUFF OVER-------------------

            }


            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                //get the SpotifyTrack object from data in firebase
                String trackname = snapshot.getName();
                String uri = (String) snapshot.child("uri").getValue();
                String artist = (String) snapshot.child("artist").getValue();

                //then add it to the SpotifyTracks object
                SpotifyTrack outputTrack = new SpotifyTrack(trackname, uri, artist);

                if (artist != null) {
                    PlaylistHandler.getHandler().suggestedSongs.addTrackIfNotDuplicate(outputTrack);
                }
                if (((MainActivity)context).fragment.equals("Suggester")) {
                    SuggesterFragment frag = (SuggesterFragment) ((MainActivity)context).getFragmentManager().findFragmentByTag("Suggester");
                    frag.displaySuggested(PlaylistHandler.getHandler().suggestedSongs);
                } else if (((MainActivity)context).fragment.equals("Slave")) {
                    SlaveFragment frag = (SlaveFragment) ((MainActivity)context).getFragmentManager().findFragmentByTag("Slave");
                    frag.displaySuggested(PlaylistHandler.getHandler().suggestedSongs);
                } else if (((MainActivity)context).fragment.equals("Host")) {
                    HostFragment frag = (HostFragment) ((MainActivity)context).getFragmentManager().findFragmentByTag("Host");
                    frag.displaySuggested(PlaylistHandler.getHandler().suggestedSongs);
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

        });

        return null;
}
}
