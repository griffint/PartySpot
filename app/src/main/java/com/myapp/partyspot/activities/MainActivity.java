package com.myapp.partyspot.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.myapp.partyspot.handlers.PlaybackHandler;
import com.myapp.partyspot.handlers.PlaylistHandler;
import com.myapp.partyspot.fragments.ChoosePlaylistHostFragment;
import com.myapp.partyspot.fragments.LoginFragment;
import com.myapp.partyspot.fragments.MainFragment;
import com.myapp.partyspot.handlers.UserHandler;
import com.myapp.partyspot.spotifyDataClasses.SpotifyPlaylists;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {
    // This class holds most of the state of the app and contains the handlers that interact with firebase and spotify

    // fields
    public String fragment; // current fragment

    private static final String CLIENT_ID = "3e85d4f69cfb4ede9bca519fb86ce216";
    private static final String REDIRECT_URI = "partyspot://partyspot";

    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 314159;

    public MainActivity() {
        this.fragment = "Login";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this); // required to use Firebase
        FirebaseHandler.getHandler().setContext(this);
        HTTPFunctions.getInstance().setContext(this);
        PlaybackHandler.getHandler().setContext(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    // starts the app by prompting user to login
                    .add(R.id.container, new LoginFragment(), "LoginFragment") // starts with logging in the user
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Player player = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        player.addConnectionStateCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
                PlaybackHandler.getHandler().setPlayer(player);

                UserHandler.getHandler().initializeWithToken(response.getAccessToken());
                changeToMainFragment();
            }
        }
    }

    public void attemptSpotifyLogin() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
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
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    public void setPlaylist(String playlist) {
        PlaylistHandler.getHandler().playlistName = playlist;
    }

    public void displayCurrentQueue(Integer index) {
        SpotifyTracks tracks = PlaybackHandler.getHandler().getSongsToEnd(index);
        ArrayList<String> list = tracks.makeNameWithArtistArray();

        // displays the queue
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(this, R.layout.queue_view, list);
        final ListView myListView = (ListView) findViewById(R.id.queue);
        myListView.setAdapter(myListAdapter);
    }

    public void validate(String playlist) { // gets called when the slave or suggester needs to check whether the playlist exists
        FirebaseHandler.getHandler().validatePlaylist(playlist);
    }

    public void validateHost(String playlist) { // called when host checks the playlist's validity
        FirebaseHandler.getHandler().validatePlaylistHost(playlist);
    }

    public void setPlayingTracks(SpotifyTracks tracks) {
        PlaybackHandler.getHandler().playingTracks = tracks;
        shuffleTracks(); //defaults to shuffling, might change later
    }

    public void shuffleTracks() {
        PlaybackHandler.getHandler().playingTracks.shuffleTracks();
    }

    public void getPlaylistTracks(String playlistOwner, String playlistId) {
        try {
            HTTPFunctions.getInstance().getPlaylistTracks(playlistOwner, playlistId); // called when user picks playlist, gets tracks, and starts playing
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void getPlaylists() {
        try {
            HTTPFunctions.getInstance().getPlaylists(UserHandler.getHandler().user.user); // gets user's playlists to use as base
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
                PlaybackHandler.getHandler().setPlaylist(playlistOwner, playlistId); // sets variables for spotifyHandler
            }
        });

        Button myButton = (Button) this.findViewById(R.id.scratch);
        myButton.setVisibility(View.VISIBLE);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.setPlayingTracks(new SpotifyTracks());
                MainActivity.this.changeToHostFragment();
            }
        });
    }

    public void changeToHostFragment() {
        this.fragment = "Host";
        HostFragment fragment = new HostFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment, "Host");
        transaction.commit();
    }

    public void changeToLoginFragment() {
        this.fragment = "Login";
        LoginFragment fragment = new LoginFragment();

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
        this.findViewById(R.id.loadingBar).setVisibility(View.GONE);
        if (UserHandler.getHandler().user.premiumUser) {
            findViewById(R.id.host_playlist).setVisibility(View.VISIBLE);
            findViewById(R.id.listen_playlist).setVisibility(View.VISIBLE);
            findViewById(R.id.host_text).setVisibility(View.VISIBLE);
            findViewById(R.id.slave_text).setVisibility(View.VISIBLE);
        }
        this.findViewById(R.id.suggest_playlist).setVisibility(View.VISIBLE);
        this.findViewById(R.id.suggest_text).setVisibility(View.VISIBLE);
    }

    public void changeToMainFragment() {
        // reset all the variables related to current playlist
        this.fragment = "Main";
        if (!UserHandler.getHandler().isSpotifyUser) {
            PlaybackHandler.getHandler().setNotHostOrSlave();
            PlaybackHandler.getHandler().songIndex = 0;
            PlaybackHandler.getHandler().onPlaylist = false;
            PlaybackHandler.getHandler().pause();
        }
        PlaylistHandler.getHandler().playlistName ="";
        PlaylistHandler.getHandler().suggestedSongs = new SpotifyTracks();
        PlaybackHandler.getHandler().setNotMuted();

        // change fragment
        MainFragment fragment = new MainFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToMainFragmentNoLogin() {
        // reset all the variables related to current playlist
        this.fragment = "Main";
        PlaylistHandler.getHandler().playlistName ="";
        PlaylistHandler.getHandler().suggestedSongs = new SpotifyTracks();

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

    public void changeToFragment(Fragment fragment, String tag) {
        this.fragment = tag;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment, tag);
        transaction.commit();
    }

}