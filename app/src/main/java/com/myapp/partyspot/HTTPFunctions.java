package com.myapp.partyspot;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by cwallace on 10/3/14.
 */
public class HTTPFunctions {

    public Context context;
    public RequestQueue queue;
    public ArrayList<SpotifyTrack> queriedTracks;
    public int flag;

    public HTTPFunctions(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        this.queriedTracks = new ArrayList<SpotifyTrack>();
        this.flag = 0;
    }//ALWAYS PASS IN getActivity

   

    public void get(String URL) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        int flag = 0;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        //JANK FIX SHOULD FIGURE OUT A BETTER WAY
                        ArrayList<SpotifyTrack> results = JSONtoSpotifyTrack(response);
                        Log.d("MADE","THIS IS PROGRESS");
                            //JSONtoSpotifyTrack(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "COULDN'T GET");
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);

    }

    public ArrayList<SpotifyTrack> JSONtoSpotifyTrack(JSONObject object){
            ArrayList<SpotifyTrack> returnArray = new ArrayList<SpotifyTrack>();
        try {

            JSONArray tracks = object.getJSONArray("tracks");
            for (int i = 0; i <10; i++) {
                JSONObject firstSong = tracks.getJSONObject(i);
                JSONArray artist = firstSong.getJSONArray("artists");
                String trackName = firstSong.getString("name");

                String artistName = artist.getJSONObject(0).getString("name");
                String artistHref = artist.getJSONObject(0).getString("href");
                String href = firstSong.getString("href");
                Log.d("Artsit", artist.toString());
                Log.d("Artsit", trackName);
                Log.d("Artsit", href);
                Log.d("Artist", artistHref);
                Log.d("Artist", artistName);
                returnArray.add(new SpotifyTrack(artistHref, artistName, href, trackName));
            }



            //Log.d("TEST",res.getJSONObject(0));
        }
        catch (JSONException e) {
            Log.d("JSONEXCEPTION",e.toString());
        }
        return returnArray;
    }

}
