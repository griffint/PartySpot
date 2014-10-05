package com.myapp.partyspot;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by svaughan on 10/5/14.
 */
public class ChoosePlaylistHostFragment extends Fragment {
    // This fragment lets the user choose which playlist to host

    // class fields
    HashMap<String, String> playlistIdMap;

    // class constructor
    public ChoosePlaylistHostFragment() {
        this.playlistIdMap = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_playlist_host, container, false);

        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);

        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToMainFragment();
            }
        });

        return rootView;
    }
}
