package com.myapp.partyspot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.myapp.partyspot.handlers.HTTPFunctions;
import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;

/**
 * Created by svaughan on 9/30/14.
 */
public class HostMainFragment extends Fragment {
    // This class holds the main view for the host

    public HostMainFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu ,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d("HERE","RAGEEEEE");
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
                http.get(Tracksjson);
                //Here u can get the value "query" which is entered in the search box.
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_main, container, false);
        setHasOptionsMenu(true);
        Log.d("DUCK","GUCK");
        final Button pause = (Button) rootView.findViewById(R.id.pause);
        final Button play = (Button) rootView.findViewById(R.id.play);
        final Button ffw = (Button) rootView.findViewById(R.id.ffw);
        final Button next = (Button) rootView.findViewById(R.id.next);
        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);
        final Button host_add = (Button) rootView.findViewById(R.id.host_add_songs);

        // return to main menu
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToMainFragment();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.pause();
            }
        });

        host_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToHostAddFragment();
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.play();
            }
        });


        ffw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.fastForward();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.next();
            }
        });

        return rootView;
    }


}