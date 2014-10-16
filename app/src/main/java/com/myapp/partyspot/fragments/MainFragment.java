package com.myapp.partyspot.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        // gets buttons to manipulate
        Button host = (Button) rootView.findViewById(R.id.host_playlist);
        Button listen = (Button) rootView.findViewById(R.id.listen_playlist);
        Button suggest = (Button) rootView.findViewById(R.id.suggest_playlist);
        TextView hostText = (TextView) rootView.findViewById(R.id.host_text);
        TextView slaveText = (TextView) rootView.findViewById(R.id.slave_text);
        TextView suggestText = (TextView) rootView.findViewById(R.id.suggest_text);

        View bar = rootView.findViewById(R.id.loadingBar); // for if user isn't loaded

        if (((MainActivity)getActivity()).user == null) { // if user is not logged in, don't let them break anything
            suggest.setVisibility(View.GONE);
            host.setVisibility(View.GONE);
            listen.setVisibility(View.GONE);
            hostText.setVisibility(View.GONE);
            slaveText.setVisibility(View.GONE);
            suggestText.setVisibility(View.GONE);
        } else {
            bar.setVisibility(View.GONE);
            if (!((MainActivity)getActivity()).premiumUser) { // if user isn't premium, don't let them stream
                host.setVisibility(View.GONE);
                listen.setVisibility(View.GONE);
                hostText.setVisibility(View.GONE);
                slaveText.setVisibility(View.GONE);
            }
        }

        // become a host
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.setHost();
                ((MainActivity)getActivity()).namePlaylist();
            }
        });

        // become a slave
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.setSlave();
                ((MainActivity)getActivity()).choosePlaylistSlave();
            }
        });

        // become a suggester
        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).choosePlaylistSlave();
            }
        });

        if (((MainActivity)getActivity()).notSpotifyUser) {
            rootView.findViewById(R.id.loadingBar).setVisibility(View.GONE);
            rootView.findViewById(R.id.suggest_playlist).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.suggest_text).setVisibility(View.VISIBLE);
        }

        return rootView;
    }
}
