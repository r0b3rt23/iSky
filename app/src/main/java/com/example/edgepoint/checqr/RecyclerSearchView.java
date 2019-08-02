package com.example.edgepoint.checqr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerSearchView extends AppCompatActivity implements RecyclerSearchViewAdapter.OnItemCLickListener {
    private RecyclerSearchViewAdapter adapter;
    private ArrayList<ViewInfoRecycler> exampleList;

    private RecyclerView dRecyclerView;
    private RecyclerView.LayoutManager dLayoutManager;
    ProgressDialog progressDialog;
    EditText solicite_id,amount_id;
    TextView solicite_tv,amount_tv,scan_tv_id;
    private String datestring;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_search_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.show();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(RecyclerSearchView.this);
                databaseSubActivity.open();
                final List<String> SubActivity = databaseSubActivity.getSubActivity();
                databaseSubActivity.close();

                solicite_id = (EditText)findViewById(R.id.solicite_id_rv);
                solicite_tv = (TextView) findViewById(R.id.solicite_tv_rv);
                amount_id = (EditText)findViewById(R.id.amount_id_rv);
                amount_tv = (TextView) findViewById(R.id.amount_tv_rv);

                Switch sw = (Switch) findViewById(R.id.switch_rv);
                Boolean boolean1 = Boolean.valueOf(SubActivity.get(3));
                sw.setChecked(boolean1);

                if(SubActivity.get(3).equals("true")){
                    solicite_id.setVisibility(View.VISIBLE);
                    amount_id.setVisibility(View.VISIBLE);
                    solicite_tv.setVisibility(View.VISIBLE);
                    amount_tv.setVisibility(View.VISIBLE);
                }
                else {
                    solicite_id.setVisibility(View.GONE);
                    amount_id.setVisibility(View.GONE);
                    solicite_tv.setVisibility(View.GONE);
                    amount_tv.setVisibility(View.GONE);
                }



                sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            solicite_id.setVisibility(View.VISIBLE);
                            solicite_tv.setVisibility(View.VISIBLE);
                            amount_id.setVisibility(View.VISIBLE);
                            amount_tv.setVisibility(View.VISIBLE);

                        } else {
                            solicite_id.setVisibility(View.GONE);
                            amount_id.setVisibility(View.GONE);
                            solicite_tv.setVisibility(View.GONE);
                            amount_tv.setVisibility(View.GONE);

                        }

                        String strChecked = String.valueOf(isChecked);
                        DatabaseAccess databaseSolicitation = DatabaseAccess.getInstance(RecyclerSearchView.this);
                        databaseSolicitation.open();
                        boolean saveActivity = databaseSolicitation.updateSolicitation(strChecked);
                        databaseSolicitation.close();

                    }
                });
                setUpRecyclerView();
                SearchBackButton();
            }
        },1000);

    }

    private void setUpRecyclerView() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
//        String UserAccess = databaseAccess.getUserAccess();
//        if (UserAccess.equals("skadoosh")){
//            exampleList = databaseAccess.getRecyclerVoterName();
//        }else {
//            exampleList = databaseAccess.getRecyclerVoterNamebyCity(UserAccess);
//        }

        exampleList = databaseAccess.getRecyclerVoterName();
        databaseAccess.close();
        dRecyclerView = findViewById(R.id.recycler_view);
        dRecyclerView.setHasFixedSize(true);
        dLayoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerSearchViewAdapter(exampleList);

        dRecyclerView.setLayoutManager(dLayoutManager);
        dRecyclerView.setAdapter(adapter);
        progressDialog.dismiss();
        adapter.setOnItemClickListener(RecyclerSearchView.this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    public void onItemClick(int position) {
        final ViewInfoRecycler clickedItem = exampleList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecyclerSearchView.this);
        alertDialogBuilder.setTitle(clickedItem.getName());
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("ATTEND",new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick (DialogInterface dialogInterface,int i){

                String VotersName = clickedItem.getName().toString();
                AddVoterAttendance(VotersName);
            }

        });

        alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick (DialogInterface dialog,int which){
            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void SearchBackButton(){
        Button prev2button = (Button) findViewById(R.id.backprofbtn);

        prev2button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecyclerSearchView.this);
                alertDialogBuilder.setTitle("Confirm Exit");
                alertDialogBuilder.setMessage("This will restart the loading process. Are you sure you want to EXIT?");
                alertDialogBuilder.setCancelable(false);

                alertDialogBuilder.setPositiveButton("YES",new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick (DialogInterface dialogInterface,int i){
                        finish();
                    }

                });

                alertDialogBuilder.setNegativeButton("NO",new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick (DialogInterface dialog,int which){
                    }

                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    public void AddVoterAttendance(String VotersName){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        datestring = formatter.format(date).toString();
        DatabaseAccess databaseAttendance = DatabaseAccess.getInstance(RecyclerSearchView.this);
        databaseAttendance.open();
        List<String> SubActivity = databaseAttendance.getSubActivity();
        int voters_id = databaseAttendance.getVotersID(VotersName);

        if (SubActivity.get(3).equals("true")){
            if (solicite_id.getText().toString().isEmpty()){

                Toast.makeText(RecyclerSearchView.this,"Fill-up Solicitation Name",Toast.LENGTH_LONG).show();
            }else {
                String solicite_name = solicite_id.getText().toString();
                int amount = 0;
                if (!amount_id.getText().toString().isEmpty()){
                    amount = Integer.parseInt(amount_id.getText().toString());
                }

                boolean submitSolicitation = databaseAttendance.insertSolicitation(voters_id,solicite_name,amount);
                if(submitSolicitation == true) {

                    Intent intent = new Intent(RecyclerSearchView.this, Information.class);
                    intent.putExtra("Name",VotersName);
                    startActivity(intent);
                }
                databaseAttendance.close();
            }
        }
        else {
            if (SubActivity.get(0).equals("Choose Activity")){
                Toast.makeText(RecyclerSearchView.this,"Empty Activity. Please Choose Activity",Toast.LENGTH_LONG).show();
            }
            else
            {
                boolean CheckList = databaseAttendance.CheckList(VotersName,SubActivity.get(0));
                List<String> SubPassword = databaseAttendance.getSubActivity();

                databaseAttendance.close();

                if (SubActivity.size() > 0){
                    if (CheckList == true){
                        Toast.makeText(RecyclerSearchView.this,"Already Saved",Toast.LENGTH_LONG).show();
                    }
                    else {

                        if (SubPassword.get(2).equals("isky")){
                            AllCoverage(SubActivity.get(0),voters_id,VotersName);
                        }
                        else if (SubPassword.get(2).equals("set")){
                            Toast.makeText(RecyclerSearchView.this,"[Failed] Set a coverage",Toast.LENGTH_LONG).show();
                        }
                        else {
                            withCoverage(SubActivity.get(0),SubPassword.get(2),voters_id,VotersName);
                        }
                    }
                }
                else {
                    Toast.makeText(RecyclerSearchView.this,"Go to Settings and Create New Activity",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void withCoverage(String activity, String password, int votersID, String Votersname){

        DatabaseAccess databaseAttendance = DatabaseAccess.getInstance(RecyclerSearchView.this);
        databaseAttendance.open();
        List<String> userPrecinct = databaseAttendance.getPrecinctPassword(password);
        userPrecinct.add(Votersname);
        String[] whereArgs = userPrecinct.toArray(new String[userPrecinct.size()]);

        String questionMark = "";
        for(int i=1; i<userPrecinct.size();i++){
            String temp = "?,";
            questionMark = questionMark+temp;
        }
        String questionMarkFinal = questionMark.substring(0, questionMark.length()-1);

        boolean checkPrecinctList = databaseAttendance.checkPrecinct(Votersname,whereArgs,questionMarkFinal);

        if (checkPrecinctList == true){
            String Activity_name = activity;

            boolean submitUpdate = databaseAttendance.insertAttendance(Activity_name,datestring,votersID);

            if(submitUpdate == true) {
                Toast.makeText(RecyclerSearchView.this,"Saved",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RecyclerSearchView.this, Information.class);
                intent.putExtra("Name",Votersname);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(RecyclerSearchView.this,"Not in the Coverage",Toast.LENGTH_LONG).show();
        }
        databaseAttendance.close();
    }

    public void AllCoverage(String activity, int votersID, String Votersname){

        DatabaseAccess databaseAttendance = DatabaseAccess.getInstance(RecyclerSearchView.this);
        databaseAttendance.open();

        String Activity_name = activity;

        boolean submitUpdate = databaseAttendance.insertAttendance(Activity_name,datestring,votersID);

        if(submitUpdate == true) {
            Toast.makeText(RecyclerSearchView.this,"Saved",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RecyclerSearchView.this, Information.class);
            intent.putExtra("Name",Votersname);
            startActivity(intent);
        }
        databaseAttendance.close();
    }
}
