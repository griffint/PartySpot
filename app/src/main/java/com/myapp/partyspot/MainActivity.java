package com.myapp.partyspot;import android.app.Activity;
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
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends Activity {
    public boolean loggedIn;
    public Uri login_uri;
    public Spotify spotify;
    public SpotifyHandler spotifyHandler;
    public FirebaseHandler firebaseHandler;
    public String accessToken;
    public spotifyTracks playingTracks;
    public ArrayList<String> playingOrder;
    public String user;

    public MainActivity() {
        this.loggedIn=false;
        this.login_uri=null;
        this.spotify = null;
        this.spotifyHandler = null;
        this.firebaseHandler = null;
        this.accessToken = null;
        this.playingTracks = new spotifyTracks();
        this.playingOrder = null;
        this.user = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        this.firebaseHandler = new FirebaseHandler(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment(), "LoginFragment")
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            this.accessToken = response.getAccessToken();
            this.spotify = new Spotify(this.accessToken);
            Log.v("HI", response.getAccessToken());
            this.loggedIn = true;
            this.spotifyHandler = new SpotifyHandler(this);
        }

        getUser();
        changeToMainFragment();
    }

    public void play() {

    }

    public ArrayList<String> getTrackUriArray() {
        return this.playingTracks.makeUriArray();
    }

    public void setPlayingTracks(spotifyTracks tracks) {
        this.playingTracks = tracks;
        shuffleTracks();
    }

    public void shuffleTracks() {
        this.playingTracks.shuffleTracks();
    }

    public void setUser(String name) {
        this.user = name;
    }

    public void getUser() {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlaylistTracks(String playlistOwner, String playlistId) {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            Log.v("USER", this.user);
            functions.getPlaylistTracks(playlistOwner, playlistId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlaylists() {
        try {
            HTTPFunctions functions = new HTTPFunctions(this);
            functions.getPlaylists(this.user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPlaylists(final spotifyPlaylists playlists) {
        ArrayList<String> list = playlists.makeNameArray();

        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(this, R.layout.playlist_view, list);
        final ListView myListView = (ListView) this.findViewById(R.id.playlist_list);
        myListView.setAdapter(myListAdapter);

        FragmentManager fm = getFragmentManager();
        fm.findFragmentByTag("choosePlaylist");

        //create an onItemClickListener to listen for clicks to specific entries
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String s = (String) myListView.getItemAtPosition(position);
                String playlistOwner = playlists.getOwnerFromTitle(s);
                String playlistId = playlists.getUriFromTitle(s);
                Log.v("YAY", playlistId);
                getPlaylistTracks(playlistOwner, playlistId);
                MainActivity.this.spotifyHandler.setPlaylist(playlistOwner, playlistId);
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



    public void setLoggedIn() {
        this.loggedIn = true;
    }

    public void setLoggedOut() {
        this.loggedIn = false;
    }

    public Uri getUri() {
        return this.login_uri;
    }

    public Spotify getSpotify() {
        return this.spotify;
    }
}