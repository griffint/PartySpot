package com.myapp.partyspot.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
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

        final SpotifyTracks suggested = ((MainActivity)getActivity()).suggestedSongs;

        // called after the httpFunctions gets the users playlists
        ArrayList<String> list = suggested.makeNameArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tracks_view, list);
        final ListView myListView = (ListView) rootView.findViewById(R.id.host_suggested);
        myListView.setAdapter(myListAdapter);

        //create an onItemClickListener for the user to choose playlist to play
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String s = (String) myListView.getItemAtPosition(position);

                DialogFragment newFragment = new AddDialogFragment();
                newFragment.show(getFragmentManager(), "missiles");

                Bundle bundle = new Bundle();
                bundle.putString("song", s); //any string to be sent
                bundle.putString("uri", suggested.getUriFromTitle(s));
                newFragment.setArguments(bundle);
            }
        });

        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);
        final Button host_main = (Button) rootView.findViewById(R.id.host_main);
        final Button play = (Button) rootView.findViewById(R.id.play);
        final Button next = (Button) rootView.findViewById(R.id.next);
        final Button volume = (Button) rootView.findViewById(R.id.volume);

        if (((MainActivity)getActivity()).muted) {
            volume.setBackground(getResources().getDrawable(R.drawable.volumeoff));
        } else {
            volume.setBackground(getResources().getDrawable(R.drawable.volumeon));
        }

        // return to main menu
        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeMutedState();

                if (((MainActivity)getActivity()).muted) {
                    volume.setBackground(getResources().getDrawable(R.drawable.volumeoff));
                } else {
                    volume.setBackground(getResources().getDrawable(R.drawable.volumeon));
                }
            }
        });

        if (((MainActivity)getActivity()).playing) {
            play.setBackground(getResources().getDrawable(R.drawable.pause));
        } else {
            play.setBackground(getResources().getDrawable(R.drawable.play));
        }

        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isPlaying = ((MainActivity)getActivity()).playing;
                if (isPlaying) {
                    ((MainActivity) getActivity()).spotifyHandler.pause();
                    ((MainActivity)getActivity()).playing = false;
                    play.setBackground(getResources().getDrawable(R.drawable.play));
                } else {
                    ((MainActivity) getActivity()).spotifyHandler.play();
                    ((MainActivity)getActivity()).playing = true;
                    play.setBackground(getResources().getDrawable(R.drawable.pause));
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.next();
            }
        });

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
