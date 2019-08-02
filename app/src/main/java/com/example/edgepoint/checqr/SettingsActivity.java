package com.example.edgepoint.checqr;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends BaseActivity{
    ListView networkList,activityList,accountList;
    private static final String URL_ACTIVITY = "http://162.144.86.26/isky/getActivities.php";
    private static final String URL_SAVE_LIST_ACTIVITY = "http://162.144.86.26/isky/saveListActivity.php";
    private static final String URL_GET_ALL_LIST_ACTIVITY = "http://162.144.86.26/isky/getAllListActivity.php";
    private static final String URL_GET_SOLICITATION = "http://162.144.86.26/isky/getAllSolicitation.php";
    public BroadcastReceiver broadcastReceiver;
    ProgressDialog progressDialog;
    Act_Scan_Adapter adapterAct_scan;
    SettingsCustomAdapter adapterSettings;
    ProgressDialog uploadDialog;
    String setSubactivity = "";
    int checkedItem = -1;

    String[] itemname_network ={
            "Upload Data",
            "Download Data",
            "Download Activity"
    };
    Integer[] imgid_network={
            R.drawable.ic_cloud_upload_blue,
            R.drawable.ic_cloud_download_blue,
            R.drawable.ic_file_download_black_24dp
    };

    String[] itemname_account ={
            "Clear All Data",
            "Logout"
    };
    Integer[] imgid_account={
            R.drawable.ic_delete_sweep,
            R.drawable.ic_exit
    };

    String[] activityscan ={
            "Activity",
            "Scanning Coverage",
            "Accomplished Activities"
    };
    Integer[] imgactscan={
            R.drawable.ic_assignment_blue_24dp,
            R.drawable.ic_playlist_add_black_24dp,
            R.drawable.ic_finish_activity_blue_24dp
    };

    String[] download_item ={
            "Activity from server",
            "Solicitation from server",
    };

    @Override
    int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(SettingsActivity.this);
        databaseSubActivity.open();
        final List<String> SubActivity = databaseSubActivity.getSubActivity();
        databaseSubActivity.close();

        String[] subitem = SubActivity.toArray(new String[SubActivity.size()]);

        adapterAct_scan=new Act_Scan_Adapter(this, activityscan, subitem,imgactscan);
        activityList=(ListView)findViewById(R.id.act_list);
        activityList.setAdapter(adapterAct_scan);
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    switch (position){
                        case 0:
                            setActivity();
                            break;
                        case 1:
                            setCoverage();
                            break;
                        case 2:
                            setFinishActivity();
                            break;
                    }
            }
        });

        adapterSettings=new SettingsCustomAdapter(this, itemname_network, imgid_network);
        networkList=(ListView)findViewById(R.id.net_list);
        networkList.setAdapter(adapterSettings);
        networkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ( checkNetworkConnection()){

                    switch (position){
                        case 0:
                            showUploadDialog();
                            break;
                        case 1:
                            if (UserAccess.equals("isky")){
                                showDownloadDialog();
                            }
                            break;
                        case 2:
                            downloadActivities();
                            break;
                    }

                }
                else Toast.makeText(SettingsActivity.this, "No Internet Connection.", Toast.LENGTH_LONG).show();
            }
        });

        adapterSettings=new SettingsCustomAdapter(this, itemname_account, imgid_account);
        accountList=(ListView)findViewById(R.id.account_list);
        accountList.setAdapter(adapterSettings);
        accountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ( checkNetworkConnection()){

                    switch (position){
                        case 0:
                            showDialog();
                            break;
                        case 1:
                            Intent intent = new Intent(SettingsActivity.this, Login_Activity.class);
                            startActivity(intent);
                            finish();
                            break;
                    }

                }
                else Toast.makeText(SettingsActivity.this, "No Internet Connection.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setActivity(){
        DatabaseAccess databaseAccessActivity = DatabaseAccess.getInstance(SettingsActivity.this);
        databaseAccessActivity.open();
        List<String> ListActivityLocal = databaseAccessActivity.getLocalActivity();
        databaseAccessActivity.close();

        final String[] localActivityArr = ListActivityLocal.toArray(new String[ListActivityLocal.size()]);

        AlertDialog.Builder setbuilder = new AlertDialog.Builder(SettingsActivity.this);
        setbuilder.setTitle("Choose Activity");
        if (ListActivityLocal.isEmpty()){
            setbuilder.setMessage("No Available Activity.");
        }


        //this will checked the item when user open the dialog
        setbuilder.setSingleChoiceItems(localActivityArr, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId) {
                setSubactivity = localActivityArr[selectedItemId].toString();
                checkedItem = selectedItemId;
            }
        });


        setbuilder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId) {
                if (checkedItem == -1){
                    Toast.makeText(SettingsActivity.this, "Set any activity", Toast.LENGTH_LONG).show();
                }else {
                    setSubActivity(setSubactivity,null,"activity");

                    DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(SettingsActivity.this);
                    databaseSubActivity.open();
                    List<String> SubActivity = databaseSubActivity.getSubActivity();
                    databaseSubActivity.close();

                    String[] subitem = SubActivity.toArray(new String[SubActivity.size()]);

                    Act_Scan_Adapter adapterAct_scan=new Act_Scan_Adapter(SettingsActivity.this, activityscan, subitem,imgactscan);
                    activityList.setAdapter(adapterAct_scan);
                }
                dialog.dismiss();
            }
        });

        setbuilder.setNeutralButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId) {

                if (checkedItem == -1){
                    Toast.makeText(SettingsActivity.this, "Set any activity", Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(SettingsActivity.this);
                    confirmationDialog.setTitle("Are you sure you want to end the activity ?");

                    confirmationDialog.setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finishActivity(setSubactivity);
                                    DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(SettingsActivity.this);
                                    databaseSubActivity.open();
                                    List<String> SubActivity = databaseSubActivity.getSubActivity();
                                    databaseSubActivity.close();

                                    String[] subitem = SubActivity.toArray(new String[SubActivity.size()]);

                                    Act_Scan_Adapter adapterAct_scan=new Act_Scan_Adapter(SettingsActivity.this, activityscan, subitem,imgactscan);
                                    activityList.setAdapter(adapterAct_scan);
                                    checkedItem = -1;
                                }
                            });

                    confirmationDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

                    confirmationDialog.show();

                }

                dialog.dismiss();
            }
        });

        setbuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId) {
                dialog.dismiss();
            }
        });

        AlertDialog setDialog = setbuilder.create();
        setDialog.show();

    }

    public void setCoverage(){
        AlertDialog.Builder setbuilder = new AlertDialog.Builder(SettingsActivity.this);
        setbuilder.setTitle("Enter Coverage Password");
        final EditText passwordtext = new EditText(SettingsActivity.this);
        setbuilder.setView(passwordtext);

        setbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String passwordValue = passwordtext.getText().toString();

                if (!passwordValue.isEmpty()){
                    DatabaseAccess databaseSubCoverage = DatabaseAccess.getInstance(SettingsActivity.this);
                    databaseSubCoverage.open();
                    List<String> SubPrecinct = databaseSubCoverage.getPrecinctPassword(passwordValue);
                    databaseSubCoverage.close();

                    if (SubPrecinct.size() > 0 || passwordValue.equals("isky"))
                    {
                        String Precinct="";
                        for (int i=0; i<SubPrecinct.size(); i++){
                            String tmp = SubPrecinct.get(i);
                            Precinct = Precinct+tmp+" ,";
                        }

                        Precinct = "Precinct: "+Precinct;
                        Precinct = Precinct.substring(0, Precinct.length()-1);
                        setSubActivity(Precinct,passwordValue,"coverage");

                        DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(SettingsActivity.this);
                        databaseSubActivity.open();

                        List<String> SubActivity = databaseSubActivity.getSubActivity();

                        databaseSubActivity.close();

                        String[] subitem = SubActivity.toArray(new String[SubActivity.size()]);

                        Act_Scan_Adapter adapterAct_scan=new Act_Scan_Adapter(SettingsActivity.this, activityscan, subitem,imgactscan);
                        activityList.setAdapter(adapterAct_scan);
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(SettingsActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        setbuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });

        AlertDialog setDialog = setbuilder.create();
        setDialog.show();

    }

    private void setFinishActivity(){
        DatabaseAccess databaseAccessActivity = DatabaseAccess.getInstance(SettingsActivity.this);
        databaseAccessActivity.open();
        List<String> ListFinishActivity = databaseAccessActivity.getFinishActivity();
        databaseAccessActivity.close();

        final String[] ListFinishActivityArr = ListFinishActivity.toArray(new String[ListFinishActivity.size()]);
        final List<String> itemsFinishSelected = new ArrayList();
        AlertDialog.Builder setbuilder = new AlertDialog.Builder(SettingsActivity.this);
        setbuilder.setTitle("List of Activities");
        if (ListFinishActivity.isEmpty()){
            setbuilder.setMessage("Empty Activity.");
        }

        setbuilder.setMultiChoiceItems(ListFinishActivityArr, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId, boolean isChecked) {
                if (isChecked) {
                    itemsFinishSelected.add(ListFinishActivityArr[selectedItemId]);
                } else if (itemsFinishSelected.contains(ListFinishActivityArr[selectedItemId])) {
                    itemsFinishSelected.remove(ListFinishActivityArr[selectedItemId]);
                }
            }
        });

        setbuilder.setPositiveButton("RESTORE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId) {
                if (itemsFinishSelected.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Select activity to restore", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder restorebuilder = new AlertDialog.Builder(SettingsActivity.this);
                    restorebuilder.setTitle("Enter Admin Password");
                    final EditText restorepassword = new EditText(SettingsActivity.this);
                    restorebuilder.setView(restorepassword);

                    restorebuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String passwordValue = restorepassword.getText().toString();

                            if (!passwordValue.isEmpty()){
                                DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(SettingsActivity.this);
                                databaseSubActivity.open();
                                List<String> SubFinishActivityPassword = databaseSubActivity.getSubActivity();
                                databaseSubActivity.close();

                                if (passwordValue.equals(SubFinishActivityPassword.get(4))){
                                    for (int x=0; x<itemsFinishSelected.size(); x++){
                                        String name_activty = itemsFinishSelected.get(x);
                                        restorectivity(name_activty);
                                    }
                                }
                                else {
                                    Toast.makeText(SettingsActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(SettingsActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });

                    restorebuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                            dialog.dismiss();
                        }
                    });

                    AlertDialog setrestoreDialog = restorebuilder.create();
                    setrestoreDialog.show();

                    dialog.dismiss();
                }
            }
        });

        setbuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId) {
                dialog.dismiss();
            }
        });

        AlertDialog setDialog = setbuilder.create();
        setDialog.show();

    }

// ===============================================NETWORK===================================================================================

    private void showUploadDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Enter Sender Name");
        final EditText sendertext = new EditText(SettingsActivity.this);
        alertDialogBuilder.setView(sendertext);
        uploadDialog = new ProgressDialog(this);
        uploadDialog.setMessage("Uploading Data...");

        DatabaseAccess databaseAccessListActivity = DatabaseAccess.getInstance(this);
        databaseAccessListActivity.open();
        final ArrayList<List_Activity_Data> ListActivity = databaseAccessListActivity.getAll_Activity();
        final ArrayList<Solicitation_Data> ListSolicitation = databaseAccessListActivity.getAll_Solicitation();
        databaseAccessListActivity.close();

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String sendername = sendertext.getText().toString();
                        try {
                            if (!sendername.isEmpty()){
                                if (ListActivity.size() != 0 || ListSolicitation.size() != 0){
                                    uploadDialog.show();
//
//                                    Toast.makeText(SettingsActivity.this, ListActivityJson, Toast.LENGTH_LONG).show();

                                    Date date = new Date();
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String UploadTime = formatter.format(date);
                                    DatabaseAccess databaseAccessUploadTime = DatabaseAccess.getInstance(SettingsActivity.this);
                                    databaseAccessUploadTime.open();
                                    boolean updateUploadTime = databaseAccessUploadTime.updateUploadTime(UploadTime);
                                    ArrayList<List_Activity_Data> ListActivityNew = databaseAccessUploadTime.getAll_Activity();
                                    databaseAccessUploadTime.close();

                                        String ListActivityJson = new Gson().toJson(ListActivityNew);
                                        String ListSolicitationJson = new Gson().toJson(ListSolicitation);

//                                    Toast.makeText(SettingsActivity.this, ListSolicitationJson, Toast.LENGTH_LONG).show();
                                        uploadtoServer(ListActivityJson,ListSolicitationJson,sendername);

                                }
                                else {
                                    Toast.makeText(SettingsActivity.this, "Empty List!", Toast.LENGTH_LONG).show();
                                    uploadDialog.dismiss();
                                }

                            }
                            else Toast.makeText(SettingsActivity.this, "Input Sender Name", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            Toast.makeText(SettingsActivity.this,"Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();

    }

    private void uploadtoServer(final String ListActivityServer,final String ListSolicitationServer,final String Sender_Name) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
        alert.setTitle("Sending Failed...");

        RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_LIST_ACTIVITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("Success")){
                            Toast.makeText(SettingsActivity.this, response, Toast.LENGTH_LONG).show();
                            uploadDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(UploadToServer.this, "Sending Failed! No Internet Connection.", Toast.LENGTH_LONG).show();
                        uploadDialog.dismiss();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ListActivityJson", ListActivityServer);
                params.put("ListSolicitationJson", ListSolicitationServer);
                params.put("Sender_Name", Sender_Name);
                return params;
            }
        };

        queue.add(stringRequest);
    }


    private void showDownloadDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Download Data From Server ?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("DOWNLOAD ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        downloadAll_ActivityFromServer();
                        download_SolicitationFromServer();
                    }
                });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();

    }

    private void downloadAll_ActivityFromServer() {
        final ProgressDialog progressDownload = new ProgressDialog(SettingsActivity.this);
        progressDownload.setMessage("Downloading...");
        progressDownload.show();

        DatabaseAccess databaseAccessListActivity = DatabaseAccess.getInstance(this);
        databaseAccessListActivity.open();
        ArrayList<List_Activity_Data> ListActivity = databaseAccessListActivity.getAll_Activity();
        databaseAccessListActivity.close();

        final String ListActivityJson = new Gson().toJson(ListActivity);

        RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_ALL_LIST_ACTIVITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object

//                            Toast.makeText(SettingsActivity.this, response+" ", Toast.LENGTH_SHORT).show();
                            JSONArray array = new JSONArray(response);

                            int count = array.length();
                            if (count != 0){

                                DatabaseAccess databaseAccessListActivity = DatabaseAccess.getInstance(SettingsActivity.this);
                                databaseAccessListActivity.open();

                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject datalist = array.getJSONObject(i);

                                    String Activity_name = datalist.getString("Activity");
                                    int votersID = datalist.getInt("Voters_ID");
                                    String datestring = datalist.getString("Timestamp");
                                    boolean submitUpdate = databaseAccessListActivity.insertAttendance(Activity_name,datestring,votersID);

                                    if(submitUpdate == true) {
                                        Toast.makeText(SettingsActivity.this,"Saved",Toast.LENGTH_LONG).show();
                                        progressDownload.dismiss();
                                    }
                                }
                                databaseAccessListActivity.close();
                                progressDownload.dismiss();

                            }
                            else{
                                Toast.makeText(SettingsActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                                progressDownload.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            progressDownload.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(UploadToServer.this, "Sending Failed! No Internet Connection.", Toast.LENGTH_LONG).show();
                        progressDownload.dismiss();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ListActivityJson", ListActivityJson);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void download_SolicitationFromServer() {
        final ProgressDialog progressDownload = new ProgressDialog(this);
        progressDownload.setMessage("Downloading...");
        progressDownload.show();

        DatabaseAccess databaseAccessListActivity = DatabaseAccess.getInstance(this);
        databaseAccessListActivity.open();
        ArrayList<Solicitation_Data> ListSolicitation = databaseAccessListActivity.getAll_Solicitation();
        databaseAccessListActivity.close();

        final String ListSoliciteJson = new Gson().toJson(ListSolicitation);

        RequestQueue queue = Volley.newRequestQueue(SettingsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_SOLICITATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object

//                            Toast.makeText(SettingsActivity.this, response+" ", Toast.LENGTH_SHORT).show();
                            JSONArray array = new JSONArray(response);

                            int count = array.length();
                            if (count != 0){

                                DatabaseAccess databaseAccessListSolicite = DatabaseAccess.getInstance(SettingsActivity.this);
                                databaseAccessListSolicite.open();

                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject datalist = array.getJSONObject(i);

                                    int voters_id = datalist.getInt("solicite_voter_id");
                                    String solicite_name = datalist.getString("solicite_name");
                                    int amount = datalist.getInt("solicite_amount");

                                    boolean submitUpdate = databaseAccessListSolicite.insertSolicitation(voters_id,solicite_name,amount);

                                    if(submitUpdate == true) {
                                        Toast.makeText(SettingsActivity.this,"Saved",Toast.LENGTH_LONG).show();
//                                        progressDownload.dismiss();
                                    }
                                }
                                databaseAccessListSolicite.close();
                                progressDownload.dismiss();

                            }
                            else{
                                Toast.makeText(SettingsActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                                progressDownload.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            progressDownload.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(UploadToServer.this, "Sending Failed! No Internet Connection.", Toast.LENGTH_LONG).show();
                        progressDownload.dismiss();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ListSoliciteJson", ListSoliciteJson);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void downloadActivities() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting to Server...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ACTIVITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            int count = array.length();
                            if (count != 0){
                                List<String> activityList = new ArrayList<String>();
                                DatabaseAccess databaseAccessActivity= DatabaseAccess.getInstance(SettingsActivity.this);
                                databaseAccessActivity.open();
                                //traversing through all the object
                                for (int i = 0; i < array.length(); i++) {

                                    //getting product object from json array
                                    JSONObject datalist = array.getJSONObject(i);

                                    String Activity_name = datalist.getString("Activity_name");
                                    String id = datalist.getString("ID");
                                    String Activity_server = ("("+id+") "+Activity_name);

                                    String NameActivity = databaseAccessActivity.getActivity(Activity_server);
                                    String NameFinishActivity = databaseAccessActivity.getFinishActivity(Activity_server);

                                    if (!NameActivity.equals(Activity_server) && !NameFinishActivity.equals(Activity_server)){
                                        activityList.add(Activity_server);
                                    }
                                }

                                databaseAccessActivity.close();
                                progressDialog.dismiss();

                                final String[] activityListArr = activityList.toArray(new String[activityList.size()]);
                                final List<String> itemsSelected = new ArrayList();
                                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                                builder.setTitle("Choose Activity");
                                if (activityList.isEmpty()){
                                    builder.setMessage("No Available Activity.");
                                }
                                builder.setMultiChoiceItems(activityListArr, null, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedItemId, boolean isChecked) {
                                        if (isChecked) {
                                            itemsSelected.add(activityListArr[selectedItemId]);
                                        } else if (itemsSelected.contains(activityListArr[selectedItemId])) {
                                            itemsSelected.remove(activityListArr[selectedItemId]);
                                        }
                                    }
                                });

                                builder.setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedItemId) {
                                        if (itemsSelected.isEmpty()){
                                            Toast.makeText(SettingsActivity.this, "Select activity to download", Toast.LENGTH_SHORT).show();
                                        }else {
                                            for (int x=0; x<itemsSelected.size(); x++){
                                                String name_activty = itemsSelected.get(x);
//                                                String column_activity = itemsSelected.get(x).replaceAll("\\s","");
//                                                String timecolumn_activity = (column_activity+"_Time");
                                                DownloadActivity(name_activty);
                                            }

                                            dialog.dismiss();
                                        }
                                    }
                                });

                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedItemId) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                            else{
                                Toast.makeText(SettingsActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SettingsActivity.this, "Unable to Connect. No Internet Connection.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void DownloadActivity(final String download_name){

                final ProgressDialog progress = new ProgressDialog(SettingsActivity.this);
                progress.setMessage("Saving Activities...");
                progress.show();

                DatabaseAccess databaseAccessSave= DatabaseAccess.getInstance(SettingsActivity.this);
                databaseAccessSave.open();
                boolean saveActivity = databaseAccessSave.insertActivityTable(download_name);

                if(saveActivity == true) {
//                        Toast.makeText(SettingsActivity.this,"Activities Saved",Toast.LENGTH_LONG).show();
                        progress.dismiss();
                }
                else
                    Toast.makeText(SettingsActivity.this,"Activities Not Save",Toast.LENGTH_LONG).show();
                databaseAccessSave.close();
    }

    public void setSubActivity(String set_subactivity, String set_password, String set_sub){

        DatabaseAccess databaseAccessSaveSubactivity= DatabaseAccess.getInstance(SettingsActivity.this);
        databaseAccessSaveSubactivity.open();
        boolean saveActivity = false;
        if (set_sub.equals("activity")){
            saveActivity = databaseAccessSaveSubactivity.updateSubActivity(set_subactivity);
        }
        else if (set_sub.equals("coverage")){
            if (set_password.equals("isky")){
                set_subactivity = "All List";
            }
            saveActivity = databaseAccessSaveSubactivity.updateSubScan(set_subactivity,set_password);
        }


        if(saveActivity == true) {
//            Toast.makeText(SettingsActivity.this,"Saved",Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(SettingsActivity.this,"Not Save",Toast.LENGTH_LONG).show();
        databaseAccessSaveSubactivity.close();
    }

    public void finishActivity(String finish_activity){

        DatabaseAccess databaseAccessSaveFinishactivity= DatabaseAccess.getInstance(SettingsActivity.this);
        databaseAccessSaveFinishactivity.open();
        boolean savefinisActivity = databaseAccessSaveFinishactivity.insertFinishActivity(finish_activity);
        List<String> SubActivity = databaseAccessSaveFinishactivity.getSubActivity();
        if(savefinisActivity == true) {
            boolean deleteActivity = databaseAccessSaveFinishactivity.deleteActivity(finish_activity);
            if(deleteActivity == true) {
                if (finish_activity.equals(SubActivity.get(0))){
                    setSubActivity("Choose Activity",null,"activity");
                }
                Toast.makeText(SettingsActivity.this,"Activity is done.",Toast.LENGTH_LONG).show();
            }
        }
        databaseAccessSaveFinishactivity.close();
    }

    public void restorectivity(String restore_activity){

        DatabaseAccess databaseAccessSaveFinishactivity= DatabaseAccess.getInstance(SettingsActivity.this);
        databaseAccessSaveFinishactivity.open();
        boolean restoreActivity = databaseAccessSaveFinishactivity.insertActivityTable(restore_activity);
        if(restoreActivity == true) {
            boolean deleteActivity = databaseAccessSaveFinishactivity.deleteFinishActivity(restore_activity);
            if(deleteActivity == true) {
                Toast.makeText(SettingsActivity.this,"Activity is restore.",Toast.LENGTH_LONG).show();
            }
        }
        databaseAccessSaveFinishactivity.close();
    }

    private void showDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Are you sure you want to RESET ALL DATA ?");

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.show();
                        DatabaseAccess databaseAccessSave= DatabaseAccess.getInstance(SettingsActivity.this);
                        databaseAccessSave.open();
                        boolean deleteData = databaseAccessSave.delete();
                        if(deleteData == true){
                            DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(SettingsActivity.this);
                            databaseSubActivity.open();
                            List<String> SubActivity = databaseSubActivity.getSubActivity();
                            databaseSubActivity.close();

                            String[] subitem = SubActivity.toArray(new String[SubActivity.size()]);

                            Act_Scan_Adapter adapterAct_scan=new Act_Scan_Adapter(SettingsActivity.this, activityscan, subitem,imgactscan);
                            activityList.setAdapter(adapterAct_scan);

                            Toast.makeText(SettingsActivity.this,"Data has been reset",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                        databaseAccessSave.close();
                    }
                });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();

    }

    public boolean checkNetworkConnection()
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());

    }

}
