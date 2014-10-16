package com.myapp.partyspot.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.R;

import java.util.HashMap;

/**
 * Created by svaughan on 10/5/14.
 */
public class ChoosePlaylistHostFragment extends Fragment {
    // This fragment lets the user choose which playlist to host
    // the playlist information will be added as soon as it is retrieved by HTTPFunctions, that is why this class is so empty

    // class constructor
    public ChoosePlaylistHostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_playlist_host, container, false);

        final Button main_menu = (Button) rootView.findViewById(R.id.main_menu);

        // return to main menu
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToMainFragment();
            }
        });

        return rootView;
    }
}
