package com.myapp.partyspot;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.spotify.sdk.android.authentication.SpotifyAuthentication;

/**
 * Created by svaughan on 9/30/14.
 */
public class LoginFragment extends Fragment {
    // This class allows the user to login
    public String CLIENT_ID;
    public String REDIRECT_URI;

    public LoginFragment() {
        CLIENT_ID = "3e85d4f69cfb4ede9bca519fb86ce216";
        REDIRECT_URI = "partyspot://partyspot";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        final Button myButton = (Button) rootView.findViewById(R.id.login_button);

        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                        new String[]{"user-read-private", "playlist-read-private", "streaming"}, null, getActivity());
            }
        });
        return rootView;
    }
}
