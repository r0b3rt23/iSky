package com.example.edgepoint.checqr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        String Name = intent.getStringExtra("Name");

        DatabaseAccess databaseInformation = DatabaseAccess.getInstance(Information.this);
        databaseInformation.open();
        List<String> checkerInfo = databaseInformation.checker(Name);
        ArrayList<List_Activity_Data> ListActivity = databaseInformation.getRowActivity(Name);
        ArrayList<Solicitation_Data> SolicitationList = databaseInformation.getVotersSolicitation(Name);
        databaseInformation.close();

        String Barangay = checkerInfo.get(1);
        String Precinct = checkerInfo.get(2);
        String Address = checkerInfo.get(4);
        String VIN = checkerInfo.get(5);
        String Activity = "";
        String Timestamp = "";

        for (int x=0; x<ListActivity.size(); x++){
            List_Activity_Data data = ListActivity.get(x);
//            DateFormat dateformat = new SimpleDateFormat("hh:mm a, MMM dd ");
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//                Date date = formatter.parse(data.getTimestamp().toString());
//                Timestamp = dateformat.format(date);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            String tempActivity = data.getActivityname()+"\n";
            Activity = Activity + tempActivity;
        }

        String Solicitation = "";
        for (int x=0; x<SolicitationList.size(); x++){
            Solicitation_Data data = SolicitationList.get(x);
            String amount = "";
            if (data.getAmount()!=0){
                NumberFormat numberFormat = new DecimalFormat("#,###");
                amount = " - ₱"+String.valueOf(numberFormat.format(data.getAmount()));
            }

            String tempSolicite = "• "+data.getSolicitename()+amount+"\n";
            Solicitation = Solicitation + tempSolicite;
        }



        TextView Name_tv = findViewById(R.id.qr_name_id);
            Name_tv.setText(Name);

        TextView Barangay_tv = findViewById(R.id.qr_barangay_id);
            Barangay_tv.setText(Barangay);

        TextView Address_tv = findViewById(R.id.qr_address_id);
            Address_tv.setText(Address);

        TextView Precinct_tv = findViewById(R.id.qr_precinct_id);
            Precinct_tv.setText(Precinct);

        TextView Vin_tv = findViewById(R.id.qr_vin_id);
            Vin_tv.setText(VIN);

        TextView Act_tv = findViewById(R.id.qr_activity_id);
            Act_tv.setText(Activity);

        TextView Timestamp_tv = findViewById(R.id.qr_solicitaion_id);
            Timestamp_tv.setText(Solicitation);

        backButton();
    }

    public void backButton(){
        Button backbutton = findViewById(R.id.back_btn);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
