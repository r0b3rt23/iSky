package com.example.edgepoint.checqr;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Homepage extends AppCompatActivity {

    String UserAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        DatabaseAccess databaseUserAccess = DatabaseAccess.getInstance(Homepage.this);
        databaseUserAccess.open();
        UserAccess = databaseUserAccess.getUserAccess();
        databaseUserAccess.close();

        Button graphbtn = (Button) findViewById(R.id.Bgraph);

        if (!UserAccess.equals("isky")){
            graphbtn.setEnabled(false);
            graphbtn.setTextColor(Color.parseColor("#C6C2B6"));
            android.graphics.drawable.Drawable mDrawable = android.support.v4.content.ContextCompat.getDrawable(Homepage.this,R.drawable.home_graph);
                    mDrawable.setColorFilter(new
                            android.graphics.PorterDuffColorFilter(0xffC6C2B6,android.graphics.PorterDuff.Mode.SRC_IN));
        }

    }


    public void onClick(View v) {
        if (v.getId() == R.id.Bscanner) {
            Intent i = new Intent(Homepage.this, MainActivity.class);
            startActivity(i);
        }

        if (v.getId() == R.id.Battendees) {
            Intent i = new Intent(Homepage.this, AttendeesActivity.class);
            startActivity(i);
        }

        if (v.getId() == R.id.Bgraph) {
            if (UserAccess.equals("isky")){
                Intent i = new Intent(Homepage.this, GraphActivity.class);
                startActivity(i);
            }
        }

        if (v.getId() == R.id.Bsettings) {
            Intent i = new Intent(Homepage.this, SettingsActivity.class);
            startActivity(i);
        }
    }
}
