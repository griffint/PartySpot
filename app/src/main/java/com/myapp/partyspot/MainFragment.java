package com.myapp.partyspot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by svaughan on 10/2/14.
 */
public class MainFragment extends Fragment {
    // This fragment is the main view for the app. It allows the user to choose whether to host or follow a playlist or suggest songs

    // class fields

    // class constructor
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button host = (Button) rootView.findViewById(R.id.host_playlist);
        Button listen = (Button) rootView.findViewById(R.id.listen_playlist);
        Button suggest = (Button) rootView.findViewById(R.id.suggest_playlist);

        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToChoosePlaylistToHostFragment();
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToSlaveMainFragment();
            }
        });

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).changeToSuggesterAddFragment();
            }
        });

        return rootView;
    }
}
