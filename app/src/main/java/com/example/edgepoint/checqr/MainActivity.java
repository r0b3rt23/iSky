package com.example.edgepoint.checqr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String stringResult,QRVIN,QRID,datestring,QRVotersname;
    AlertDialog.Builder builder;
    EditText solicite_id,amount_id;
    TextView solicite_tv,amount_tv,scan_tv_id;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(stringResult)) {
                return;
            }

            stringResult = result.getText();

            try{
                stringResult = result.getText();
                String[] QRcodeResult = stringResult.split("_");

                QRID = QRcodeResult[0];
                QRVIN = QRcodeResult[1];

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                datestring = formatter.format(date).toString();

                DatabaseAccess databaseAttendance = DatabaseAccess.getInstance(MainActivity.this);
                databaseAttendance.open();
                List<String> SubActivity = databaseAttendance.getSubActivity();
                int voters_id = Integer.parseInt(QRID);
                QRVotersname = databaseAttendance.getVotersName(QRID,QRVIN);

                if (SubActivity.get(3).equals("true")){
                    if (solicite_id.getText().toString().isEmpty()){

                        Toast.makeText(MainActivity.this,"Fill-up Solicitation Name",Toast.LENGTH_LONG).show();
                    }else {
                        String solicite_name = solicite_id.getText().toString();
                        int amount = 0;
                        if (!amount_id.getText().toString().isEmpty()){
                             amount = Integer.parseInt(amount_id.getText().toString());
                        }

                        boolean submitSolicitation = databaseAttendance.insertSolicitation(voters_id,solicite_name,amount);
                        if(submitSolicitation == true) {

                            Intent intent = new Intent(MainActivity.this, Information.class);
                            intent.putExtra("Name",QRVotersname);
                            startActivity(intent);
                        }
                        databaseAttendance.close();
                    }
                }
                else {
                    if (SubActivity.get(0).equals("Choose Activity")){
                        Toast.makeText(MainActivity.this,"Empty Activity. Please Choose Activity",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        boolean CheckList = databaseAttendance.CheckList(QRVotersname,SubActivity.get(0));
                        List<String> SubPassword = databaseAttendance.getSubActivity();

                        databaseAttendance.close();

                        if (SubActivity.size() > 0){
                            if (CheckList == true){
                                Toast.makeText(MainActivity.this,"Already Saved",Toast.LENGTH_LONG).show();
                            }
                            else {

                                if (SubPassword.get(2).equals("bts")){
                                    AllCoverage(SubActivity.get(0),voters_id);
                                }
                                else if (SubPassword.get(2).equals("set")){
                                    Toast.makeText(MainActivity.this,"[Failed] Set a coverage",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    withCoverage(SubActivity.get(0),SubPassword.get(2),voters_id);
                                }
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Go to Settings and Create New Activity",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            catch (Exception e){
                Toast.makeText(MainActivity.this,"Invalid Entry!",Toast.LENGTH_LONG).show();
            }

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public void withCoverage(String activity, String password, int votersID){

        DatabaseAccess databaseAttendance = DatabaseAccess.getInstance(MainActivity.this);
        databaseAttendance.open();
        List<String> userPrecinct = databaseAttendance.getPrecinctPassword(password);
        userPrecinct.add(QRVotersname);
        String[] whereArgs = userPrecinct.toArray(new String[userPrecinct.size()]);

        String questionMark = "";
        for(int i=1; i<userPrecinct.size();i++){
            String temp = "?,";
            questionMark = questionMark+temp;
        }
        String questionMarkFinal = questionMark.substring(0, questionMark.length()-1);

        boolean checkPrecinctList = databaseAttendance.checkPrecinct(QRVotersname,whereArgs,questionMarkFinal);

        if (checkPrecinctList == true){
            String Activity_name = activity;

            boolean submitUpdate = databaseAttendance.insertAttendance(Activity_name,datestring,votersID);

            if(submitUpdate == true) {
                Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Information.class);
                intent.putExtra("Name",QRVotersname);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(MainActivity.this,"Not in the Coverage",Toast.LENGTH_LONG).show();
        }
        databaseAttendance.close();
    }

    public void AllCoverage(String activity, int votersID){

        DatabaseAccess databaseAttendance = DatabaseAccess.getInstance(MainActivity.this);
        databaseAttendance.open();

            String Activity_name = activity;

            boolean submitUpdate = databaseAttendance.insertAttendance(Activity_name,datestring,votersID);

            if(submitUpdate == true) {
                Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Information.class);
                intent.putExtra("Name",QRVotersname);
                startActivity(intent);
            }
        databaseAttendance.close();
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_scanner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CameraPermission cameraPermission = new CameraPermission(this);

        if (!cameraPermission.checkPermissionForCamera()) {
            cameraPermission.requestPermissionForCamera();
        }
        DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(MainActivity.this);
        databaseSubActivity.open();
        final List<String> SubActivity = databaseSubActivity.getSubActivity();
        databaseSubActivity.close();

        scan_tv_id = (TextView) findViewById(R.id.scanqrcode_id);
        scan_tv_id.setText(SubActivity.get(0));
        solicite_id = (EditText)findViewById(R.id.solicite_id);
        solicite_tv = (TextView) findViewById(R.id.solicite_tv);
        amount_id = (EditText)findViewById(R.id.amount_id);
        amount_tv = (TextView) findViewById(R.id.amount_tv);

        Switch sw = (Switch) findViewById(R.id.scan_switch);
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
                    scan_tv_id.setText("SCANNING FOR SOLICITATION");
                    solicite_id.setVisibility(View.VISIBLE);
                    solicite_tv.setVisibility(View.VISIBLE);
                    amount_id.setVisibility(View.VISIBLE);
                    amount_tv.setVisibility(View.VISIBLE);

                } else {
                    scan_tv_id.setText(SubActivity.get(0));
                    solicite_id.setVisibility(View.GONE);
                    amount_id.setVisibility(View.GONE);
                    solicite_tv.setVisibility(View.GONE);
                    amount_tv.setVisibility(View.GONE);

                }

                String strChecked = String.valueOf(isChecked);
                DatabaseAccess databaseSolicitation = DatabaseAccess.getInstance(MainActivity.this);
                databaseSolicitation.open();
                boolean saveActivity = databaseSolicitation.updateSolicitation(strChecked);
                databaseSolicitation.close();

            }
        });


        builder = new AlertDialog.Builder(this);
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.setStatusText(null);
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        checklistbutton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void checklistbutton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.check_fbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder restorebuilder = new AlertDialog.Builder(MainActivity.this);
                restorebuilder.setTitle("Enter Admin Password");
                final EditText restorepassword = new EditText(MainActivity.this);
                restorebuilder.setView(restorepassword);

                restorebuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String passwordValue = restorepassword.getText().toString();

                        if (!passwordValue.isEmpty()){
                            DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(MainActivity.this);
                            databaseSubActivity.open();
                            List<String> SubFinishActivityPassword = databaseSubActivity.getSubActivity();
                            databaseSubActivity.close();

                            if (passwordValue.equals(SubFinishActivityPassword.get(4))){
                                Intent i = new Intent(MainActivity.this, RecyclerSearchView.class);
                                startActivity(i);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Enter a password", Toast.LENGTH_SHORT).show();
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


            }
        });
    }

}
