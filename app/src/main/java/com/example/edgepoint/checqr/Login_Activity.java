package com.example.edgepoint.checqr;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login_Activity extends AppCompatActivity {
    BroadcastReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        mNetworkReceiver = new NetworkMonitor();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        Button loginbtn = (Button) findViewById(R.id.login_btn_id);
        final AutoCompleteTextView Edittextusername = (AutoCompleteTextView)findViewById(R.id.uname_id);
        final EditText Edittextpassword = (EditText)findViewById(R.id.pswd_id);
        String[] cityArr = getResources().getStringArray(R.array.city_municipality);

        ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(Login_Activity.this,android.R.layout.simple_dropdown_item_1line, cityArr);
        Edittextusername.setAdapter(searchAdapter);
        Edittextusername.setThreshold(1);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = Edittextusername.getText().toString();
                String password = Edittextpassword.getText().toString();

                try {

                    DatabaseAccess databaseloginInfo = DatabaseAccess.getInstance(Login_Activity.this);
                    databaseloginInfo.open();

                    boolean LoginInfo = databaseloginInfo.loginInfo(username,password,"admin_table");

                    if (LoginInfo == true){

                        boolean UserAccess = databaseloginInfo.userAccess(username);

                        if (UserAccess == true){
                            if (username.equals("isky")) {
                                Intent intent = new Intent(Login_Activity.this, Homepage.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(Login_Activity.this, Homepage.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else {
                            Toast.makeText(Login_Activity.this,"Invalid Username or Password!", Toast.LENGTH_LONG).show();
                        }

                    }
                    else {
                        Toast.makeText(Login_Activity.this,"Invalid Username or Password!", Toast.LENGTH_LONG).show();
                    }

                    databaseloginInfo.close();

                } catch (Exception e) {
                    Toast.makeText(Login_Activity.this,"Invalid Username or Password! E", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
