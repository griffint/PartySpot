package com.myapp.partyspot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by svaughan on 10/2/14.
 */
public class SlaveAddFragment extends Fragment {
    // This fragment allows the slave phone to suggest songs to the host

    // class fields

    // class constructor
    public SlaveAddFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
