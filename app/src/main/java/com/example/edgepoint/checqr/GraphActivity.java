package com.example.edgepoint.checqr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends BaseActivity{
    //This is Graph Activity
    ListView cityList,barangayList,precinctList;
    Spinner barangay_spinner, precinct_spinner;
    public static String Bgypsin;
    public static String Prcntpsin;
    private EditText TargetCount;
    int BrgyTargetCount;
    String TargetCountText;
    ProgressDialog progressDialog;
    List<String> precinct,barangays;

    String[] itemname ={
            "Generate Graph",
            "Generate Missing Graph",
    };

    Integer[] imgid={
            R.drawable.ic_equalizer_white_24dp,
            R.drawable.ic_poll_blue_24dp
    };

    @Override
    int getContentViewId() {
        return R.layout.activity_graph;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_graph;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                TargetCount = (EditText) findViewById(R.id.count_id);
                TargetCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            // do something, e.g. set your TextView here via .setText()
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });

                barangay_spinner = (Spinner)findViewById(R.id.qr_bgy_spinner);
                DatabaseAccess databaseAccessBarangay = DatabaseAccess.getInstance(GraphActivity.this);
                databaseAccessBarangay.open();
                barangays = databaseAccessBarangay.getQRDatabase("Barangay");
                databaseAccessBarangay.close();

                ArrayAdapter<String> adapterBarangay = new ArrayAdapter<String>(GraphActivity.this, android.R.layout.simple_spinner_item, barangays);
                barangay_spinner.setAdapter(adapterBarangay);

                precinct_spinner = (Spinner) findViewById(R.id.qr_pct_spinner);

                barangay_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        Bgypsin = barangay_spinner.getSelectedItem().toString();

                        DatabaseAccess databaseAccessPrecinct = DatabaseAccess.getInstance(GraphActivity.this);
                        databaseAccessPrecinct.open();
                        precinct = databaseAccessPrecinct.getQR_2_Database(Bgypsin, "Barangay=?", "Precinct");
                        databaseAccessPrecinct.close();

                        //Toast.makeText(GraphActivity.this,String.valueOf(precinct.size()),Toast.LENGTH_LONG).show();

                        ArrayAdapter<String> adapterPrecinct = new ArrayAdapter<String>(GraphActivity.this, android.R.layout.simple_spinner_item, precinct);
                        adapterPrecinct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapterPrecinct.notifyDataSetChanged();
                        precinct_spinner.setAdapter(adapterPrecinct);

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                progressDialog.dismiss();
            }
        },1500);

        cityListGraph();
        barangayListGraph();
        precinctListGraph();

    }

    public void cityListGraph(){
        SettingsCustomAdapter CityAdapter=new SettingsCustomAdapter(this, itemname, imgid);
        cityList=(ListView)findViewById(R.id.city_list);
        cityList.setAdapter(CityAdapter);
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String graphItem =  (String) cityList.getItemAtPosition(position);
                TargetCountText = TargetCount.getText().toString();
                if (graphItem.equals("Generate Graph")){
                    if (TargetCountText.isEmpty()){
                        Toast.makeText(GraphActivity.this,"Please input targeted number.",Toast.LENGTH_LONG).show();
                    }else {
                        countingGraph("city","City",TargetCountText );
                    }
                }
                else if(graphItem.equals("Generate Missing Graph")){
                    if (TargetCountText.isEmpty()){
                        Toast.makeText(GraphActivity.this,"Please input targeted number.",Toast.LENGTH_LONG).show();
                    }else {
                        countingMissingGrpah("city","City", TargetCountText);
                    }
                }
            }
        });
    }

    public void barangayListGraph(){
        SettingsCustomAdapter BarangayAdapter=new SettingsCustomAdapter(this, itemname, imgid);
        barangayList=(ListView)findViewById(R.id.barangay_list);
        barangayList.setAdapter(BarangayAdapter);
        barangayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String graphItem =  (String) barangayList.getItemAtPosition(position);
                TargetCountText = TargetCount.getText().toString();
                if (graphItem.equals("Generate Graph")){
                    Bgypsin = barangay_spinner.getSelectedItem().toString();
                    if (TargetCount.getText().toString().isEmpty()){
                        Toast.makeText(GraphActivity.this,"Please input targeted number.",Toast.LENGTH_LONG).show();
                    }else {
                        countingGraph("barangay",Bgypsin, TargetCountText);
                    }
                }
                else if(graphItem.equals("Generate Missing Graph")){
                    if (TargetCount.getText().toString().isEmpty()){
                        Toast.makeText(GraphActivity.this,"Please input targeted number.",Toast.LENGTH_LONG).show();
                    }else {
                        countingMissingGrpah("barangay",Bgypsin, TargetCountText);
                    }
                }
            }
        });
    }

    public void precinctListGraph(){
        SettingsCustomAdapter PrecinctAdapter=new SettingsCustomAdapter(this, itemname, imgid);
        precinctList=(ListView)findViewById(R.id.precinct_list);
        precinctList.setAdapter(PrecinctAdapter);
        precinctList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String graphItem =  (String) precinctList.getItemAtPosition(position);
                Prcntpsin = precinct_spinner.getSelectedItem().toString();
                TargetCountText = TargetCount.getText().toString();
                if (graphItem.equals("Generate Graph")){
                    if (TargetCount.getText().toString().isEmpty()){
                        Toast.makeText(GraphActivity.this,"Please input targeted number.",Toast.LENGTH_LONG).show();
                    }else {
                        countingGraph("precinct",Prcntpsin, TargetCountText);
                        //Toast.makeText(GraphActivity.this,String.valueOf(precinct.size()),Toast.LENGTH_LONG).show();
                    }
                }
                else if(graphItem.equals("Generate Missing Graph")){
                    if (TargetCount.getText().toString().isEmpty()){
                        Toast.makeText(GraphActivity.this,"Please input targeted number.",Toast.LENGTH_LONG).show();
                    }else {
                        countingMissingGrpah("precinct",Prcntpsin, TargetCountText);


                    }
                }
            }
        });
    }

    public void countingGraph(String graphLevel,String BgyPct, final String getTextInput){
        float TargetCountNumberCounting = Integer.valueOf(getTextInput);
        float TargetCountNumber = 0;
        float barangayNumber = barangays.size();
        float TargetPerPrecicnt =  (TargetCountNumberCounting/590);
        float TargetPerBrgy = TargetPerPrecicnt*precinct.size();

        List<String> labels = new ArrayList<>();
        labels.add("Attend");
        labels.add("Target");

        List<Integer> count = new ArrayList<>();
        DatabaseAccess datacount = DatabaseAccess.getInstance(GraphActivity.this); //count the names
        datacount.open();
        List<String> Activity = datacount.getSubActivity();
        String Activity_name = Activity.get(0);

        if (graphLevel.equals("city")) {
            count.add(datacount.getAttendanceCountCity(Activity_name));
            TargetCountNumber = TargetCountNumberCounting;
        }
        else if (graphLevel.equals("barangay")){
            count.add(datacount.getAttendanceCountBarangay(Activity_name, BgyPct));
            TargetCountNumber = TargetPerBrgy;

        }
        else if (graphLevel.equals("precinct")){
            count.add(datacount.getAttendanceCountPrecinct(Activity_name, BgyPct));
            TargetCountNumber = TargetPerPrecicnt;
            //Toast.makeText(GraphActivity.this,String.valueOf(precinct.size() +" "+ TargetCountNumber),Toast.LENGTH_LONG).show();
        }
        datacount.close();

        DatabaseAccess insertcount = DatabaseAccess.getInstance(GraphActivity.this); //insert data
        insertcount.open();
        for (int i=0; i<count.size();i++)
        {
            insertcount.insertAttendanceCount(count.get(i), labels.get(i));
        }
        insertcount.close();

        int TargetCountNumberFinal = Math.round(TargetCountNumber);
        int PrecinctNumber = precinct.size();
        generateQRGraph(TargetCountNumberFinal, BgyPct, PrecinctNumber, graphLevel);
    }

    public void countingMissingGrpah(String graphLevel,String BgyPct, final String getTextInput){
        int TargetCountNumberCounting = Integer.valueOf(getTextInput);
        int TargetCountNumber = 0;
        int barangayNumber = barangays.size();
        int TargetPerPrecicnt =  (TargetCountNumberCounting/590);
        int TargetPerBrgy = TargetPerPrecicnt*precinct.size();
        int votersCount=0;
        DatabaseAccess datacountM = DatabaseAccess.getInstance(GraphActivity.this); //count the names
        datacountM.open();
        votersCount = datacountM.getCountBarangayList(BgyPct);
        List<String> Activity = datacountM.getSubActivity();
        String Activity_name = Activity.get(0);

        List<String> labels = new ArrayList<>();
        labels.add("Attend");
        labels.add("Missing");
        labels.add("Target");
        int countActivity = 0;

        List<Integer> countM = new ArrayList<>();

            if (graphLevel.equals("city")) {
                countM.add(datacountM.getAttendanceCountCity(Activity_name));
                countActivity = datacountM.getAttendanceCountCity(Activity_name);

                int countAllM = datacountM.getCountCityList();
                countActivity = countAllM - countActivity;

                TargetCountNumber = TargetCountNumberCounting;
            }
            else if (graphLevel.equals("barangay")){
                countM.add(datacountM.getAttendanceCountBarangay(Activity_name,BgyPct));
                countActivity = datacountM.getAttendanceCountBarangay(Activity_name,BgyPct);

                int countAllM = datacountM.getCountBarangayList(BgyPct);
                countActivity = countAllM - countActivity;

                TargetCountNumber = TargetPerBrgy;
            }
            else if (graphLevel.equals("precinct")){
                countM.add(datacountM.getAttendanceCountPrecinct(Activity_name,BgyPct));
                countActivity = datacountM.getAttendanceCountBarangay(Activity_name,BgyPct);

                int countAllM = datacountM.getCountPrecinctList(BgyPct);
                countActivity = countAllM - countActivity;

                TargetCountNumber = TargetPerPrecicnt;
            }
        datacountM.close();

        countM.add(countActivity);

        countM.add(TargetCountNumber);

        DatabaseAccess insertcount = DatabaseAccess.getInstance(GraphActivity.this); //insert data
        insertcount.open();
        for (int i=0; i<countM.size();i++)
        {
            insertcount.insertAttendanceCount(countM.get(i), labels.get(i));
        }
        insertcount.close();

        int PrecinctNumber = precinct.size();
        generateQRGraphM(BgyPct, PrecinctNumber,graphLevel, votersCount);
    }

    public void generateQRGraph(int countTarget,String Label, int PrecinctCount, String graphLevels){
        Intent intent = new Intent(this, QR_GraphView.class);
        intent.putExtra("TargetCount", countTarget);
        intent.putExtra("Precinct", PrecinctCount);
        intent.putExtra("Label", Label);
        intent.putExtra("GraphLabel", graphLevels);
        startActivity(intent);
    }

    public void generateQRGraphM(String Label, int PrecinctCount, String graphLevels, int countBarangay){

        Intent intent = new Intent(this, QR_MGraphView.class);
        intent.putExtra("Label", Label);
        intent.putExtra("Precinct", PrecinctCount);
        intent.putExtra("GraphLabel", graphLevels);
        intent.putExtra("VoterBrgy", countBarangay);
        startActivity(intent);
    }

}