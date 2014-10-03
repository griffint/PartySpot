package com.myapp.partyspot;import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;

public class MainActivity extends Activity {
    public boolean loggedIn;
    public Uri login_uri;
    public Spotify spotify;
    public SpotifyHandler spotifyHandler;

    public MainActivity() {
        this.loggedIn=false;
        this.login_uri=null;
        this.spotify = null;
        this.spotifyHandler = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            this.spotify = new Spotify(response.getAccessToken());
            this.loggedIn = true;
            this.spotifyHandler = new SpotifyHandler(this);
        }

        changeToHostMainFragment();
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

    public void changeToChoosePlaylistFragment() {
        ChoosePlaylistToFollowFragment fragment = new ChoosePlaylistToFollowFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
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