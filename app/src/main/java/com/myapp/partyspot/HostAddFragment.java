package com.myapp.partyspot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by svaughan on 10/2/14.
 */
public class HostAddFragment extends Fragment {
    // This fragment allows the host to add to the playlist, whether from voted or searching

    // class fields

    // class constructor
    public HostAddFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
