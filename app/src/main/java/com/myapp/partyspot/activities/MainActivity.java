package com.myapp.partyspot.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.myapp.partyspot.fragments.ChooseSlaveDialogFragment;
import com.myapp.partyspot.fragments.HostFragment;
import com.myapp.partyspot.fragments.HostSearchResultsFragment;
import com.myapp.partyspot.fragments.NameDialogFragment;
import com.myapp.partyspot.fragments.SlaveFragment;
import com.myapp.partyspot.fragments.SlaveSearchResultsFragment;
import com.myapp.partyspot.fragments.SuggesterFragment;
import com.myapp.partyspot.handlers.FirebaseHandler;
import com.myapp.partyspot.handlers.HTTPFunctions;
import com.myapp.partyspot.R;
import com.myapp.partyspot.handlers.SpotifyHandler;
import com.myapp.partyspot.fragments.ChoosePlaylistHostFragment;
import com.myapp.partyspot.fragments.LoginFragment;
import com.myapp.partyspot.fragments.MainFragment;
import com.myapp.partyspot.spotifyDataClasses.SpotifyPlaylists;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import java.util.ArrayList;

public class MainActivity extends Activity {
    // This class holds most of the state of the app and contains the handlers that interact with firebase and spotify

    // fields
    public boolean loggedIn; // whether the user is logged in
    public Spotify spotify; // the spotify session
    public SpotifyHandler spotifyHandler; // the class that handles all spotify events
    public FirebaseHandler firebaseHandler; // the class that handles all firebase events
    public String accessToken; // Authentication token from spotify
    public String user; // user's name
    public boolean premiumUser; // true if premium, false otherwise
    public SpotifyTracks suggestedSongs; // a list of the suggested songs
    public boolean playing; // whether the spotify player is currently streaming music
    public String playlistName; // stores the name of the playlist that the app is following or hosting
    public boolean muted; // whether the app is muted or not
    public String userType; //host, slave or suggester
    public String fragment; // current fragment

    public MainActivity() {
        this.loggedIn=false;
        this.spotify = null;
        this.spotifyHandler = null;
        this.firebaseHandler = null;
        this.accessToken = null;
        this.user = null;
        this.premiumUser = false;
        this.suggestedSongs = new SpotifyTracks();
        this.playing = false;
        this.playlistName = "";
        this.muted = false;
        this.userType = "";
        this.fragment = "Login";
    }

    public void setNotMuted() {
        if (this.muted) { // if it is muted, set to be not muted
            this.muted = false;
            AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamMute(AudioManager.STREAM_MUSIC, this.muted);
        }
    }

    public void setMuted() {
        if (!this.muted) { // if it is not muted, set to be muted
            this.muted = true;
            AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamMute(AudioManager.STREAM_MUSIC, this.muted);
        }
    }

    public void setPlaylist(String playlist) {
        this.playlistName = playlist;
    }

    public void displayCurrentQueue(Integer index) {
        SpotifyTracks tracks = this.spotifyHandler.getSongsToEnd(index);
        ArrayList<String> list = tracks.makeNameWithArtistArray();

        // displays the queue
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(this, R.layout.queue_view, list);
        final ListView myListView = (ListView) findViewById(R.id.queue);
        myListView.setAdapter(myListAdapter);
    }


    public void validate(String playlist) { // gets called when the slave or suggester needs to check whether the playlist exists
        this.firebaseHandler.validatePlaylist(playlist);
    }

    public void validateHost(String playlist) { // called when host checks the playlist's validity
        this.firebaseHandler.validatePlaylistHost(playlist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this); // required to use Firebase
        this.firebaseHandler = new FirebaseHandler(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    // starts the app by prompting user to login
                    .add(R.id.container, new LoginFragment(), "LoginFragment") // starts with logging in the user
                    .commit();
        }

        // temporary suggested songs
        //suggestedSongs.addTrack(new SpotifyTrack("Whoa Whoa Whoa", "spotify:track:3tpdc8zHIOXy8rYhuI9car", "Watsky"));
        //suggestedSongs.addTrack(new SpotifyTrack("3005", "spotify:track:3Z2sglqDj1rDRMF5x0Sz2R", "Childish Gambino"));
        //suggestedSongs.addTrack(new SpotifyTrack("Handyman", "spotify:track:31Fw4CZistkNF4Uo3S39Md", "Seven"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // this method gets called when user logs in with spotify, kind of breaks if app is restarted or switched
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);

            // gets access token to create spotify class and for web api requests.
            this.accessToken = response.getAccessToken();
            this.spotify = new Spotify(this.accessToken);
            this.loggedIn = true;
            this.spotifyHandler = new SpotifyHandler(this);
        }

        getUser(); // gets and sets user from the web api
        this.fragment = "Main";
        changeToMainFragment();
    }

    public void setPlayingTracks(SpotifyTracks tracks) {
        this.spotifyHandler.playingTracks = tracks;
        shuffleTracks(); //defaults to shuffling, might change later
    }

    public void setPremiumUser() {
        this.premiumUser = true;
    }

    public void shuffleTracks() {
        this.spotifyHandler.playingTracks.shuffleTracks();
    }

    public void setUser(String name) {
        this.user = name;
    }

    public void getUser() {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getUser(); // will get the user and then set the fragment to be loaded
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void getPlaylistTracks(String playlistOwner, String playlistId) {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getPlaylistTracks(playlistOwner, playlistId); // called when user picks playlist, gets tracks, and starts playing
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void getPlaylists() {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getPlaylists(this.user); // gets user's playlists to use as base
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void displayPlaylists(final SpotifyPlaylists playlists) {
        // called after the httpFunctions gets the users playlists
        // needed so that playlists can be displayed asynchronously
        ArrayList<String> list = playlists.makeNameArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(this, R.layout.playlist_view, list);
        final ListView myListView = (ListView) this.findViewById(R.id.playlist_list);
        myListView.setAdapter(myListAdapter);

        //create an onItemClickListener for the user to choose playlist to play
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String s = (String) myListView.getItemAtPosition(position);
                String playlistOwner = playlists.getOwnerFromTitle(s);
                String playlistId = playlists.getUriFromTitle(s);
                getPlaylistTracks(playlistOwner, playlistId); // gets playlist tracks to play
                MainActivity.this.spotifyHandler.setPlaylist(playlistOwner, playlistId); // sets variables for spotifyHandler
            }
        });
    }

    public void changeToHostFragment() {
        this.fragment = "Host";
        HostFragment fragment = new HostFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void setPlaylistsLoaded() {
        // changes from the progress bar view to the list of playlists
        findViewById(R.id.loadingBar).setVisibility(View.GONE);
    }

    public void changeToChoosePlaylistToHostFragment() {
        this.fragment = "ChoosePlaylistHost";
        ChoosePlaylistHostFragment fragment = new ChoosePlaylistHostFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment, "choosePlaylist");
        transaction.commit();

        getPlaylists();
    }

    public void setMainFragmentLoaded() {
        // called after user is found so that the app doesn't break while it's loading
        findViewById(R.id.loadingBar).setVisibility(View.GONE);
        if (this.premiumUser) {
            findViewById(R.id.host_playlist).setVisibility(View.VISIBLE);
            findViewById(R.id.listen_playlist).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.suggest_playlist).setVisibility(View.VISIBLE);
    }

    public void changeToMainFragment() {
        // reset all the variables related to current playlist
        this.fragment = "Main";
        this.spotifyHandler.setNotHostOrSlave();
        this.spotifyHandler.songIndex = 0;
        this.spotifyHandler.onPlaylist = false;
        this.playing = false;
        this.playlistName ="";
        this.spotifyHandler.pause();
        this.suggestedSongs = new SpotifyTracks();
        this.setNotMuted();

        // change fragment
        MainFragment fragment = new MainFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void displayHostSearchResults(SpotifyTracks tracks) { // called asynchronously after gotten with http
        HostSearchResultsFragment frag = (HostSearchResultsFragment) getFragmentManager().findFragmentByTag("host_search");
        frag.displaySearchResults(tracks);
    }

    public void displaySlaveSearchResults(SpotifyTracks tracks) { // called asynchronously after gotten with http
        SlaveSearchResultsFragment frag = (SlaveSearchResultsFragment) getFragmentManager().findFragmentByTag("slave_search");
        frag.displaySearchResults(tracks);
    }

    public void changeToHostSearchResults() {
        if (!this.fragment.equals("HostSearchResults")) { // needed because android has a bug where search is submitted twice
            this.fragment = "HostSearchResults";
            DialogFragment newFragment = new HostSearchResultsFragment();
            newFragment.show(getFragmentManager(), "host_search");
        }
    }

    public void changeToSlaveSearchResults() {
        if (!this.fragment.equals("SlaveSearchResults")) { // needed because android has a bug where search is submitted twice
            this.fragment = "SlaveSearchResults";
            DialogFragment newFragment = new SlaveSearchResultsFragment();
            newFragment.show(getFragmentManager(), "slave_search");
        }
    }

    public void namePlaylist() {
        DialogFragment newFragment = new NameDialogFragment();
        newFragment.show(getFragmentManager(), "host_name_playlist");
    }

    public void choosePlaylistSlave() {
        DialogFragment newFragment = new ChooseSlaveDialogFragment();
        newFragment.show(getFragmentManager(), "slave_choose_playlist");
    }

    public void changeToSlaveFragment() {
        this.fragment = "Slave";
        SlaveFragment fragment = new SlaveFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment, "Slave");
        transaction.commit();
    }

    public void changeToSuggesterFragment() {
        this.fragment = "Suggester";
        SuggesterFragment fragment = new SuggesterFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment, "Suggester");
        transaction.commit();
    }

    public Spotify getSpotify() {
        return this.spotify;
    }

    @Override
    public void onDestroy() {
        this.spotifyHandler.destroy(); // needed so android doesn't leak resources
    }
}