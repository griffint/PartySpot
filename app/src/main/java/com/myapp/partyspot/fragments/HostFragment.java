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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.myapp.partyspot.handlers.HTTPFunctions;
import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;
import com.myapp.partyspot.handlers.SpotifyHandler;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

import java.util.ArrayList;

/**
 * Created by svaughan on 9/30/14.
 */
public class HostFragment extends Fragment {
    // This class holds the main view for the host

    public ListView suggestedListView;

    public HostFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu ,MenuInflater inflater) {
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
        final View rootView = inflater.inflate(R.layout.fragment_host, container, false);
        setHasOptionsMenu(true);// need to replace this line

        this.suggestedListView = (ListView) rootView.findViewById(R.id.suggested);
        final SpotifyTracks suggested = ((MainActivity)getActivity()).suggestedSongs;

        displaySuggested(suggested);

        final Button play = (Button) rootView.findViewById(R.id.play);
        final Button next = (Button) rootView.findViewById(R.id.next);
        final Button suggest_song = (Button) rootView.findViewById(R.id.suggest_song);
        final Button volume = (Button) rootView.findViewById(R.id.volume);

        final SpotifyTracks queue = ((MainActivity)getActivity()).spotifyHandler.playingTracks;


        ArrayList<String> queuelist = queue.makeNameWithArtistArray();

        // displays the queue
        ArrayAdapter<String> queueListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.queue_view, queuelist);
        final ListView queueListView = (ListView) rootView.findViewById(R.id.queue);
        queueListView.setAdapter(queueListAdapter);

        //create an onItemClickListener for the user to choose playlist to play
        queueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("POOP","POOP");
        }
        });

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

        if (((MainActivity)getActivity()).playing) {
            play.setBackground(getResources().getDrawable(R.drawable.pause));
        } else {
            play.setBackground(getResources().getDrawable(R.drawable.play));
        }

        // return to main menu
        suggest_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) rootView.findViewById(R.id.suggestSong);
                HTTPFunctions http = new HTTPFunctions(getActivity());
                String song = editText.getText().toString();
                editText.setText("");
                String URL = "https://api.spotify.com/v1/search?q=" + song + "&type=track";
                ((MainActivity)HostFragment.this.getActivity()).changeToHostSearchResults();
                http.getHostSearch(URL);
            }
        });


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
                MainActivity activity = ((MainActivity)getActivity());
                SpotifyHandler handler = activity.spotifyHandler;
                handler.next();
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
                String tmp = (String) HostFragment.this.suggestedListView.getItemAtPosition(position);
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
