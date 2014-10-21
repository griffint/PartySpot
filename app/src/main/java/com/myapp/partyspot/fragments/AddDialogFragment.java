package com.myapp.partyspot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;

/**
 * Created by svaughan on 10/10/14.
 */
public class AddDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // this class creates a dialog when the host chooses a song to add to the queue
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set empty in case we can't get them
        String s = "";
        String u = "";
        String a = "";

        // this is how data is passed from fragment to fragment
        if(getArguments()!=null) {
            s = getArguments().getString("song");
            u = getArguments().getString("uri");
            a = getArguments().getString("artist");
        }

        // declared final for usage inside other class
        final String song_name = s;
        final String uri = u;
        final String artist = a;

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_add_dialog, null);

        // set textview to be song view so that user can see what they're adding
        final MainActivity activity = (MainActivity) getActivity();
        final TextView myListView = (TextView) view.findViewById(R.id.add_dialog);
        myListView.setText(song_name);

        final SpotifyTrack track = new SpotifyTrack(song_name, uri, artist);

        // dialog stuff
        builder.setView(view)
                // sets button functionality to queue
                .setPositiveButton(R.string.add_now, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity)getActivity()).spotifyHandler.queueNext(track);
                        ((MainActivity)getActivity()).displayCurrentQueue(((MainActivity) getActivity()).spotifyHandler.songIndex);
                        if (((MainActivity)getActivity()).fragment.equals("HostSearchResults")) { // needed in case user is adding from main view instead of from search
                            ((DialogFragment) getActivity().getFragmentManager().findFragmentByTag("host_search")).dismiss();
                        }
                        activity.fragment = "Host";
                    }
                })
                        // sets button functionality to queue last
                .setNeutralButton(R.string.add_last, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).spotifyHandler.queueLast(track);
                        ((MainActivity)getActivity()).displayCurrentQueue(((MainActivity) getActivity()).spotifyHandler.songIndex);
                        if (((MainActivity)getActivity()).fragment.equals("HostSearchResults")) {  // needed in case user is adding from main view instead of from search
                            ((DialogFragment) getActivity().getFragmentManager().findFragmentByTag("host_search")).dismiss();
                        }
                        activity.fragment = "Host";
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}