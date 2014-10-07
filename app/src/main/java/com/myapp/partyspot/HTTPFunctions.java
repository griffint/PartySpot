package com.myapp.partyspot;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

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

    public void get(String URL) { //WONT ALWAYS BE VOID, RETURN INFO FROM DATA
        this.queriedTracks = new ArrayList<SpotifyTrack>();
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
        Log.v("DICKS", user);
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
                                Log.v("DICKS","DICKS");
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
                                tracks.addTrack(new SpotifyTrack(name, uri));
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
