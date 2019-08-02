package com.example.edgepoint.checqr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NetworkMonitor extends BroadcastReceiver {

    private static final String URL = "http://162.144.86.26/isky/login.php";
    @Override
    public void onReceive(final Context context, Intent intent) {

        if(checkNetworkConnection(context))
        {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //converting the string to json array object
                                JSONArray array = new JSONArray(response);

                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject datalist = array.getJSONObject(i);

                                    int id_json = datalist.getInt("id");
                                    String username_json = datalist.getString("username");
                                    String password_json = datalist.getString("password");

                                    DatabaseAccess databaseLoginSubmit = DatabaseAccess.getInstance(context);
                                    databaseLoginSubmit.open();
                                    boolean submitUpdate = databaseLoginSubmit.updateLogin(id_json,username_json,password_json);
                                    databaseLoginSubmit.close();
                                    if(submitUpdate == false) {
                                        Toast.makeText(context,"error!", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context,"error!", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_LONG).show();

                        }
                    });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);

        }
        else
        {
//            Toast.makeText(context,"No Internet", Toast.LENGTH_LONG).show();
        }

    }


    public boolean checkNetworkConnection(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());

    }


}

