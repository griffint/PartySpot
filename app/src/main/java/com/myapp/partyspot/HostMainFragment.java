package com.myapp.partyspot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by svaughan on 9/30/14.
 */
public class HostMainFragment extends Fragment {
    // This class holds the main view for the host

    public HostMainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_main, container, false);

        final Button pause = (Button) rootView.findViewById(R.id.pause);
        final Button resume = (Button) rootView.findViewById(R.id.resume);
        final Button ffw = (Button) rootView.findViewById(R.id.ffw);
        final Button next = (Button) rootView.findViewById(R.id.next);

        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.pause();
            }
        });


        resume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).spotifyHandler.resume();
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
