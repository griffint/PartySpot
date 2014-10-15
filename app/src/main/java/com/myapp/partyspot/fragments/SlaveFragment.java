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

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
import com.myapp.partyspot.handlers.HTTPFunctions;

/**
 * Created by svaughan on 10/2/14.
 */
public class SlaveFragment extends Fragment {
    // This fragment holds the main view for the slave phone

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

        if (((MainActivity)getActivity()).muted) {
            volume.setBackground(getResources().getDrawable(R.drawable.volumeoff));
        } else {
            volume.setBackground(getResources().getDrawable(R.drawable.volumeon));
        }

        // return to main menu
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

}
