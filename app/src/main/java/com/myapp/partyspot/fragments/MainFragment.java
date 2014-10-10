package com.myapp.partyspot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;

/**
 * Created by svaughan on 10/2/14.
 */
public class MainFragment extends Fragment {
    // This fragment is the main view for the app. It allows the user to choose whether to host or follow a playlist or suggest songs

    // class fields

    // class constructor
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button host = (Button) rootView.findViewById(R.id.host_playlist);
        Button listen = (Button) rootView.findViewById(R.id.listen_playlist);
        Button suggest = (Button) rootView.findViewById(R.id.suggest_playlist);
        View bar = rootView.findViewById(R.id.loadingBar);

        if (((MainActivity)getActivity()).user == null) { // if user is not logged in
            suggest.setVisibility(View.GONE);
            host.setVisibility(View.GONE);
            listen.setVisibility(View.GONE);
        } else {
            bar.setVisibility(View.GONE);
            if (!((MainActivity)getActivity()).premiumUser) { // if user isn't premium
                host.setVisibility(View.GONE);
                listen.setVisibility(View.GONE);
            }
        }

        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.setHost();
                ((MainActivity)getActivity()).changeToChoosePlaylistToHostFragment();
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToSlaveMainFragment();
            }
        });

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToSuggesterAddFragment();
            }
        });

        return rootView;
    }
}