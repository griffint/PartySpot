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
 * Created by svaughan on 10/10/14.
 */

public class HostSearchResultsFragment extends Fragment {

    @Override
    public void onCreateOptionsMenu(Menu menu ,MenuInflater inflater) {
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
                ((MainActivity)HostSearchResultsFragment.this.getActivity()).changeToHostSearchResults();
                http.getHostSearch(Tracksjson);
                //Here u can getHostSearch the value "query" which is entered in the search box.
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_search_results, container, false);
        setHasOptionsMenu(true);

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

    public void displaySearchResults(final SpotifyTracks tracks) {
        // called after the httpFunctions gets the users playlists
        ArrayList<String> list = tracks.makeNameWithArtistArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tracks_view, list);
        final ListView myListView = (ListView) getActivity().findViewById(R.id.host_search_results);
        myListView.setAdapter(myListAdapter);

        //create an onItemClickListener for the user to choose playlist to play
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String tmp = (String) myListView.getItemAtPosition(position);
                int pos = tmp.indexOf(" - ");
                String s = tmp.substring(0,pos);

                DialogFragment newFragment = new AddDialogFragment();
                newFragment.show(getFragmentManager(), "missiles");

                Bundle bundle = new Bundle();
                bundle.putString("song", s); //any string to be sent
                bundle.putString("uri", tracks.getUriFromTitle(s));
                bundle.putString("artist", tracks.getArtistFromTitle(s));
                newFragment.setArguments(bundle);
            }
        });
    }
}