package com.myapp.partyspot.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
public class SuggesterFragment extends Fragment {
    // This fragment allows the suggester to suggest songs to the host

    // class fields
    public ListView myListView; // for easy manipulation of the list later

    // class constructor
    public SuggesterFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_suggester, container, false);
        setHasOptionsMenu(true);

        this.myListView = (ListView) rootView.findViewById(R.id.suggested_tracks);

        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);

        // go back to main menu
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToMainFragment();
            }
        });

        displaySuggested(((MainActivity)getActivity()).suggestedSongs);

        return rootView;
    }

    public void displaySuggested(final SpotifyTracks tracks) {
        // called after the suggested songs are updated through firebase
        ArrayList<String> list = tracks.makeNameWithArtistArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tracks_view, list);
        this.myListView.setAdapter(myListAdapter);

    }
}
