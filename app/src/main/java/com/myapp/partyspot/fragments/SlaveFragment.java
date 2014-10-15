package com.myapp.partyspot.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
        Log.d("HERE", "RAGEEEEE");
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.hostmenu, menu);


        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                Log.d("Test", query);
                HTTPFunctions http = new HTTPFunctions(getActivity()); // HANDLE SPACES ALSO CWALLACE
                String Tracksjson = "https://api.spotify.com/v1/search?q=" + query + "&type=track";
                Log.v("CHANGE","CHANGE");
                ((MainActivity) SlaveFragment.this.getActivity()).changeToSlaveSearchResults();
                http.getSlaveSearch(Tracksjson);
                //Here u can getHostSearch the value "query" which is entered in the search box.
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slave_main, container, false);
        setHasOptionsMenu(true);

        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);
        final Button slave_main = (Button) rootView.findViewById(R.id.slave_main);
        final Button volume = (Button) rootView.findViewById(R.id.volume);

        this.suggestedListView = (ListView) rootView.findViewById(R.id.suggested_slave);
        final SpotifyTracks suggested = ((MainActivity)getActivity()).suggestedSongs;
        displaySuggested(suggested);

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

        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToMainFragment();
            }
        });

        slave_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToSlaveFragment();
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

        //create an onItemClickListener for the user to choose playlist to play
        this.suggestedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String tmp = (String) SlaveFragment.this.suggestedListView.getItemAtPosition(position);
                int pos = tmp.indexOf(" - ");
                String s = tmp.substring(0, pos);

                DialogFragment newFragment = new AddDialogFragment();
                newFragment.show(getFragmentManager(), "add");

                Bundle bundle = new Bundle();
                bundle.putString("song", s); //any string to be sent
                bundle.putString("uri", suggested.getUriFromTitle(s));
                bundle.putString("artist", suggested.getArtistFromTitle(s));
                newFragment.setArguments(bundle);
            }
        });

    }

}
