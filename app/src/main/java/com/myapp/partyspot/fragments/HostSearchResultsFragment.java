package com.myapp.partyspot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.partyspot.R;
import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

import java.util.ArrayList;

/**
 * Created by svaughan on 10/10/14.
 */

public class HostSearchResultsFragment extends DialogFragment {
    // this fragment displays the search results for the host
    public ListView myListView; // for easy storage to manipulate the list
    public TextView myTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final MainActivity activity = (MainActivity) getActivity();

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_search_results, null);
        this.myListView = (ListView) view.findViewById(R.id.search_results);
        this.myTextView = (TextView) view.findViewById(R.id.no_results);

        builder.setView(view)
                // set a cancel button for the user
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.fragment = "Host";
                    }
                });

        return builder.create();
    }

    public void displaySearchResults(final SpotifyTracks tracks) {
        if (tracks.tracks.isEmpty()) {
            this.myTextView.setText("We didn't get any results. Try to just search for the song's name without the artist or album");
            return;
        }

        // called after the httpFunctions gets the users playlists
        ArrayList<String> list = tracks.makeNameWithArtistArray();

        // displays the list of playlists
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.tracks_view, list);
        this.myListView.setAdapter(myListAdapter);

        //create an onItemClickListener for the user to choose playlist to play
        this.myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String tmp = (String) myListView.getItemAtPosition(position);
                int pos = tmp.indexOf(" - ");
                String s = tmp.substring(0,pos);

                DialogFragment newFragment = new AddDialogFragment();
                newFragment.show(getFragmentManager(), "missiles");

                // this is how the track information is passed to the add dialog
                Bundle bundle = new Bundle();
                bundle.putString("song", s); //any string to be sent
                bundle.putString("uri", tracks.getUriFromTitle(s));
                bundle.putString("artist", tracks.getArtistFromTitle(s));
                newFragment.setArguments(bundle);
            }
        });
    }
}