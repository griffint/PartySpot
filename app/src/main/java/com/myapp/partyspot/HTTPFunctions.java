package com.myapp.partyspot;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

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
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

}
