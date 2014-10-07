package com.myapp.partyspot.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.firebase.client.Firebase;
import com.myapp.partyspot.handlers.FirebaseHandler;
import com.myapp.partyspot.handlers.HTTPFunctions;
import com.myapp.partyspot.R;
import com.myapp.partyspot.handlers.SpotifyHandler;
import com.myapp.partyspot.fragments.ChooseHostOrSlaveFragment;
import com.myapp.partyspot.fragments.ChoosePlaylistHostFragment;
import com.myapp.partyspot.fragments.ChoosePlaylistToFollowFragment;
import com.myapp.partyspot.fragments.HostAddFragment;
import com.myapp.partyspot.fragments.HostMainFragment;
import com.myapp.partyspot.fragments.LoginFragment;
import com.myapp.partyspot.fragments.MainFragment;
import com.myapp.partyspot.fragments.NameHostedFragment;
import com.myapp.partyspot.fragments.SlaveAddFragment;
import com.myapp.partyspot.fragments.SlaveMainFragment;
import com.myapp.partyspot.fragments.SlaveVoteFragment;
import com.myapp.partyspot.fragments.SuggesterAddFragment;
import com.myapp.partyspot.fragments.SuggesterVoteFragment;
import com.myapp.partyspot.spotifyDataClasses.SpotifyPlaylists;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import java.util.ArrayList;

public class MainActivity extends Activity {
    public boolean loggedIn;
    public Spotify spotify;
    public SpotifyHandler spotifyHandler;
    public FirebaseHandler firebaseHandler;
    public String accessToken; // Authentication token from spotify
    public String user; // user's name
    public boolean premiumUser; // true if premium, false otherwise

    public MainActivity() {
        this.loggedIn=false;
        this.spotify = null;
        this.spotifyHandler = null;
        this.firebaseHandler = null;
        this.accessToken = null;
        this.user = null;
        this.premiumUser = false;
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
                    .add(R.id.container, new LoginFragment(), "LoginFragment")
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // this method gets called when user logs in with spotify
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
        changeToMainFragment();
    }

    public void setPlayingTracks(SpotifyTracks tracks) {
        this.spotifyHandler.playingTracks = tracks;
        shuffleTracks();
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
            functions.getUser();
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void getPlaylistTracks(String playlistOwner, String playlistId) {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getPlaylistTracks(playlistOwner, playlistId);
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void getPlaylists() {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getPlaylists(this.user);
        } catch (Exception e) { // needed by volley
            e.printStackTrace();
        }
    }

    public void displayPlaylists(final SpotifyPlaylists playlists) {
        // called after the httpFunctions gets the users playlists
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

    public void changeToHostMainFragment() {
        HostMainFragment fragment = new HostMainFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void setPlaylistsLoaded() {
        // changes from the progress bar view to the list of playlists
        findViewById(R.id.loadingBar).setVisibility(View.GONE);
    }

    public void changeToChooseHostOrSlaveFragment() {
        ChooseHostOrSlaveFragment fragment = new ChooseHostOrSlaveFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToChoosePlaylistToFollowFragment() {
        ChoosePlaylistToFollowFragment fragment = new ChoosePlaylistToFollowFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToChoosePlaylistToHostFragment() {
        ChoosePlaylistHostFragment fragment = new ChoosePlaylistHostFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment, "choosePlaylist");
        transaction.commit();

        getPlaylists();
    }

    public void setMainFragmentLoaded() {
        findViewById(R.id.loadingBar).setVisibility(View.GONE);
        if (this.premiumUser) {
            findViewById(R.id.host_playlist).setVisibility(View.VISIBLE);
            findViewById(R.id.listen_playlist).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.suggest_playlist).setVisibility(View.VISIBLE);
    }

    public void changeToHostAddFragment() {
        HostAddFragment fragment = new HostAddFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToLoginFragment() {
        LoginFragment fragment = new LoginFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToMainFragment() {
        MainFragment fragment = new MainFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToNameHostedFragment() {
        NameHostedFragment fragment = new NameHostedFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToSlaveAddFragment() {
        SlaveAddFragment fragment = new SlaveAddFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToSlaveMainFragment() {
        SlaveMainFragment fragment = new SlaveMainFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToSlaveVoteFragment() {
        SlaveVoteFragment fragment = new SlaveVoteFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToSuggesterAddFragment() {
        SuggesterAddFragment fragment = new SuggesterAddFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public void changeToSuggesterVoteFragment() {
        SuggesterVoteFragment fragment = new SuggesterVoteFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    public Spotify getSpotify() {
        return this.spotify;
    }
}