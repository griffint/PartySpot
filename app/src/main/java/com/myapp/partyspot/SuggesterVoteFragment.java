package com.myapp.partyspot;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by svaughan on 10/2/14.
 */
public class SuggesterVoteFragment extends Fragment {
    // This song allows the suggester to vote on others suggestions

    // class fields

    // class constructor
    public SuggesterVoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

}
