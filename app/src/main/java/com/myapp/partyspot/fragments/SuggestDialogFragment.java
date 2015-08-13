package com.myapp.partyspot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
import com.myapp.partyspot.handlers.PlaybackHandler;
import com.myapp.partyspot.handlers.PlaylistHandler;
import com.myapp.partyspot.handlers.UserHandler;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;
import com.myapp.partyspot.handlers.FirebaseHandler;

/**
 * Created by svaughan on 10/10/14.
 */
public class SuggestDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String s = "";
        String u = "";
        String a = "";

        // this is how arguments are passed to fragments
        if(getArguments()!=null) {
            s = getArguments().getString("song");
            u = getArguments().getString("uri");
            a = getArguments().getString("artist");
        }

        // declared final so they can be accessed from different class
        final String song_name = s;
        final String uri = u;
        final String artist = a;

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_add_dialog, null);

        final MainActivity activity = (MainActivity) getActivity();

        // display the song that the user is trying to suggest
        final TextView myTextView = (TextView) view.findViewById(R.id.add_dialog);
        myTextView.setText(song_name);

        final SpotifyTrack track = new SpotifyTrack(song_name, uri, artist);

        builder.setView(view)
                .setPositiveButton(R.string.suggest_song, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //fix this:::::::::((MainActivity)getActivity()).firebaseHandler.suggest(track);
                        if (activity.fragment.equals("SlaveSearchResults") || activity.fragment.equals("SuggesterSearchResults")) { // if is suggesting from search, also dismiss search
                            ((DialogFragment) getActivity().getFragmentManager().findFragmentByTag("slave_search")).dismiss();
                        }
                        FirebaseHandler.getHandler().pushSuggestion(PlaylistHandler.getHandler().playlistName, track);
                        if (!UserHandler.getHandler().isSpotifyUser) {
                            activity.fragment = "Suggester";
                        } else if (PlaybackHandler.getHandler().isSlave) {
                            activity.fragment = "Slave";
                        } else {
                            activity.fragment = "Suggester";
                        }
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