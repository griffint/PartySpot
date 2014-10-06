package com.myapp.partyspot;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by cwallace on 10/3/14.
 */
public class HTTPFunctions {

    public Context context;
    public RequestQueue queue;

    public HTTPFunctions(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }//ALWAYS PASS IN getActivity

    public void get(String URL) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
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



    public void getPlaylists(String user) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        String URL = "https://api.spotify.com/v1/users/"+user+"/playlists";
        final spotifyPlaylists playlists = new spotifyPlaylists();
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
                                playlists.addPlaylists(new spotifyPlaylist(name, id, owner));
                            } catch (Exception e) {
                                Log.v("DICKS","DICKS");
                            }
                        }
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
                            ((MainActivity)HTTPFunctions.this.context).setUser(name);
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
        final spotifyTracks tracks = new spotifyTracks();
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
                                tracks.addTrack(new spotifyTrack(name, uri));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ((MainActivity)HTTPFunctions.this.context).setPlayingTracks(tracks);
                        ((MainActivity)HTTPFunctions.this.context).changeToHostMainFragment();

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

}
