package com.myapp.partyspot.handlers;

import com.firebase.client.Firebase;
import com.myapp.partyspot.activities.MainActivity;

/**
 * Created by svaughan on 10/2/14.
 */
public class FirebaseHandler {
    // This class handles the firebase. It can update currently playing song, or pull currently playing song
    //will have methods for pulling and pushing relevant data

    //NEEDED:

    // class fields
    public Firebase firebaseDatabase;
    public String URL;
    public MainActivity activity;

    public FirebaseHandler(MainActivity activity) {
        this.activity = activity;
        this.URL = "https://partyspot.firebaseIO.com";
        this.firebaseDatabase = new Firebase(this.URL);
    }

    //time for pushing here
    public class hostData {

       
    }

}
