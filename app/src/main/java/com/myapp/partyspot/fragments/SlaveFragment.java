package com.myapp.partyspot.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
import com.myapp.partyspot.handlers.HTTPFunctions;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

import java.util.ArrayList;

/**
 * Created by svaughan on 10/2/14.
 */
public class SlaveFragment extends Fragment {
    // This fragment holds the main view for the slave phone

    public ListView suggestedListView;
    // class fields

    // class constructor
    public SlaveFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.hostmenu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // handle item selection
        switch (item.getItemId()) {
            case R.id.return_to_main:
                ((MainActivity)getActivity()).changeToMainFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_slave, container, false);
        setHasOptionsMenu(true);

        // get buttons to add click listeners to them

        final Button suggest_song = (Button) rootView.findViewById(R.id.suggest_song_listener);


        final Button volume = (Button) rootView.findViewById(R.id.volume);

        // set muted icon
        this.suggestedListView = (ListView) rootView.findViewById(R.id.suggested_slave);
        final SpotifyTracks suggested = ((MainActivity)getActivity()).suggestedSongs;
        displaySuggested(suggested);
        
        if (((MainActivity)getActivity()).muted) {
            volume.setBackground(getResources().getDrawable(R.drawable.volumeoff));
        } else {
            volume.setBackground(getResources().getDrawable(R.drawable.volumeon));
        }

        // change muted state and icon
        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)getActivity()).muted) {
                    ((MainActivity)getActivity()).setNotMuted();
                    volume.setBackground(getResources().getDrawable(R.drawable.volumeon));
                } else {
                    ((MainActivity)getActivity()).setMuted();
                    volume.setBackground(getResources().getDrawable(R.drawable.volumeoff));
                }
            }
        });

        // return to main menu
        suggest_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) rootView.findViewById(R.id.suggestedSongListener);
                HTTPFunctions http = new HTTPFunctions(getActivity());
                String song = editText.getText().toString();
                editText.setText("");
                String URL = "https://api.spotify.com/v1/search?q=" + song + "&type=track";
                ((MainActivity)SlaveFragment.this.getActivity()).changeToSlaveSearchResults();
                http.getSlaveSearch(URL);
            }
        });

        return rootView;
    }

    public void displaySuggested(final SpotifyTracks suggested) {

        // called after the httpFunctions gets the users playlists
        ArrayList<String> list = suggested.makeNameWithArtistArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tracks_view, list);
        this.suggestedListView.setAdapter(myListAdapter);
    }

}
