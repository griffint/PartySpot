package com.myapp.partyspot.handlers;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.myapp.partyspot.activities.MainActivity;
import com.myapp.partyspot.spotifyDataClasses.SpotifyPlaylist;
import com.myapp.partyspot.spotifyDataClasses.SpotifyPlaylists;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTrack;
import com.myapp.partyspot.spotifyDataClasses.SpotifyTracks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;

/**
 * Created by cwallace on 10/3/14.
 */
public class HTTPFunctions {

    public Context context;
    public RequestQueue queue;
    public ArrayList<SpotifyTrack> queriedTracks;

    public HTTPFunctions(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }//ALWAYS PASS IN getActivity

    public void getHostSearch(String URL) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        URL = URL.replaceAll(" ","+"); // for proper url functionality
        final MainActivity activity = ((MainActivity)HTTPFunctions.this.context);
        this.queriedTracks = new ArrayList<SpotifyTrack>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        //JANK FIX SHOULD FIGURE OUT A BETTER WAY
                        SpotifyTracks results = JSONtoSpotifyTrack(response);
                        if (activity.fragment.equals("HostSearchResults")) {
                            activity.displayHostSearchResults(results);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+((MainActivity)context).accessToken);
                params.put("Accept", "application/json");

                return params;
            }
        };

        queue.add(getRequest);
    }

    public void getSlaveSearch(String URL) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        URL = URL.replaceAll(" ","+");
        final MainActivity activity = ((MainActivity)HTTPFunctions.this.context);
        this.queriedTracks = new ArrayList<SpotifyTrack>();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        SpotifyTracks results = JSONtoSpotifyTrack(response);
                        if (activity.fragment.equals("SlaveSearchResults") || activity.fragment.equals("SuggesterSearchResults")) {
                            activity.displaySlaveSearchResults(results);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        queue.add(getRequest);
    }

    public void getPlaylists(String user) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        String URL = "https://api.spotify.com/v1/users/"+user+"/playlists";
        final SpotifyPlaylists playlists = new SpotifyPlaylists();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONArray responseList;
                        try {
                            responseList = (JSONArray) response.get("items");
                        } catch (Exception e) {
                            responseList = new JSONArray();
                            e.getStackTrace();
                        }
                        for (int i=0; i<responseList.length(); i++) {
                            try {
                                String name = ((JSONObject)responseList.get(i)).get("name").toString();
                                String id = ((JSONObject)responseList.get(i)).get("id").toString();
                                String owner = ((JSONObject)((JSONObject)responseList.get(i)).get("owner")).get("id").toString();
                                playlists.addPlaylists(new SpotifyPlaylist(name, id, owner));
                            } catch (Exception e) {
                                Log.d("ERROR", "Child didn't exist");
                            }
                        }
                        ((MainActivity)HTTPFunctions.this.context).setPlaylistsLoaded();
                        ((MainActivity)HTTPFunctions.this.context).displayPlaylists(playlists);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "COULDN'T GET");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+((MainActivity)context).accessToken);
                params.put("Accept", "application/json");

                return params;
            }
        };
        queue.add(getRequest);
    }

    public void getUser() { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        String URL = "https://api.spotify.com/v1/me";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            String name = (String)response.get("id");
                            String userType = (String)response.get("product");
                            if (userType.equals("premium")) {((MainActivity)HTTPFunctions.this.context).setPremiumUser();}
                            ((MainActivity)HTTPFunctions.this.context).setUser(name);
                            ((MainActivity)HTTPFunctions.this.context).setMainFragmentLoaded();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "COULDN'T GET");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+((MainActivity)context).accessToken);
                params.put("Accept", "application/json");

                return params;
            }
        };
        queue.add(getRequest);
    }

    public void getPlaylistTracks(String user, String playlistId) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        String URL = "https://api.spotify.com/v1/users/"+user+"/playlists/"+playlistId+"/tracks";
        final SpotifyTracks tracks = new SpotifyTracks();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray responseList;
                        try {
                            responseList = (JSONArray) response.get("items");
                        } catch (Exception e) {
                            responseList = new JSONArray();
                            e.getStackTrace();
                        }
                        for (int i=0; i<responseList.length(); i++) {
                            try {
                                JSONObject trackInfo = (JSONObject)((JSONObject)responseList.get(i)).get("track");
                                String name = trackInfo.get("name").toString();
                                String uri = trackInfo.get("uri").toString();
                                String artist = ((JSONObject)((JSONArray)trackInfo.get("artists")).get(0)).get("name").toString();
                                tracks.addTrack(new SpotifyTrack(name, uri, artist));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ((MainActivity)HTTPFunctions.this.context).setPlayingTracks(tracks);
                        ((MainActivity)HTTPFunctions.this.context).changeToHostFragment();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "COULDN'T GET");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+((MainActivity)context).accessToken);
                params.put("Accept", "application/json");

                return params;
            }
        };

        queue.add(getRequest);

    }

    public SpotifyTracks JSONtoSpotifyTrack(JSONObject object){
        final SpotifyTracks tracks = new SpotifyTracks();

        JSONArray responseList;
        JSONObject testObject;
        try {
            testObject = object.getJSONObject("tracks");
            responseList = testObject.getJSONArray("items");
        } catch (Exception e) {
            responseList = new JSONArray();
            e.getStackTrace();
        }

        for (int i=0; i<responseList.length(); i++) {
            try {
                JSONObject entry = responseList.getJSONObject(i);
                String name = entry.getString("name");
                String uri = entry.getString("uri");
                String artist = ((JSONObject)((JSONArray)entry.get("artists")).get(0)).get("name").toString();
                SpotifyTrack track = new SpotifyTrack(name,uri, artist);
                tracks.addTrack(track);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tracks;
    }

}
