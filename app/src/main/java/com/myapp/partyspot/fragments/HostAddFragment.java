package com.myapp.partyspot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

import java.util.ArrayList;

/**
 * Created by svaughan on 10/2/14.
 */
public class HostAddFragment extends Fragment {
    // This fragment allows the host to add to the playlist, whether from voted or searching

    // class fields

    // class constructor
    public HostAddFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_add, container, false);

        final SpotifyTracks tracks = new SpotifyTracks();
        tracks.addTrack(new SpotifyTrack("Whoa Whoa Whoa", "spotify:track:3tpdc8zHIOXy8rYhuI9car"));
        tracks.addTrack(new SpotifyTrack("3005", "spotify:track:3Z2sglqDj1rDRMF5x0Sz2R"));
        tracks.addTrack(new SpotifyTrack("Handyman", "spotify:track:3tpdc8zHIOXy8rYhuI9car"));

        // called after the httpFunctions gets the users playlists
        ArrayList<String> list = tracks.makeNameArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tracks_view, list);
        final ListView myListView = (ListView) rootView.findViewById(R.id.host_suggested);
        myListView.setAdapter(myListAdapter);

        //create an onItemClickListener for the user to choose playlist to play
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String s = (String) myListView.getItemAtPosition(position);
                SpotifyTrack track = tracks.getTrackFromTitle(s);
                Log.v(track.getName(), track.getUri());
                ((MainActivity)getActivity()).spotifyHandler.queue(track); // sets variables for spotifyHandler
            }
        });

        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);
        final Button host_main = (Button) rootView.findViewById(R.id.host_main);

        // return to main menu
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToMainFragment();
            }
        });

        // return to main menu
        host_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToHostMainFragment();
            }
        });

        return rootView;
    }
}
