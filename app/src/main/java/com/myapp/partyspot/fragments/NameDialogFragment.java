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

/**
 * Created by svaughan on 10/10/14.
 */
public class NameDialogFragment extends DialogFragment {
    // this fragment lets the host choose the playlists name for the others to join by

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_name_dialog, null);

        final MainActivity activity = (MainActivity) getActivity();
        final EditText myEditText = (EditText) view.findViewById(R.id.name_dialog);

        builder.setView(view)
                .setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = myEditText.getText().toString();
                        if (!name.equals("")) {
                            activity.validateHost(name); // checks whether the playlist doesn't exist, then begins hosting
                        }
                    }
                })
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.v("cancel", "NOW");
                        }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
