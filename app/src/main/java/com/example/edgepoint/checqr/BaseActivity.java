package com.example.edgepoint.checqr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;
    String UserAccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        DatabaseAccess databaseUserAccess = DatabaseAccess.getInstance(BaseActivity.this);
        databaseUserAccess.open();
        UserAccess = databaseUserAccess.getUserAccess();
        databaseUserAccess.close();

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_scanner) {
                startActivity(new Intent(this, MainActivity.class));
            }
            else if (itemId == R.id.navigation_attendees) {
                    startActivity(new Intent(this, AttendeesActivity.class));
            }
            else if (itemId == R.id.navigation_home) {
                finish();
            }
            else if (itemId == R.id.navigation_graph) {

                if (UserAccess.equals("isky")){
                    startActivity(new Intent(this, GraphActivity.class));
                }
                else {
                    Toast.makeText(BaseActivity.this,"Graph is Disabled",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            finish();
        return true;
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
        if (!UserAccess.equals("isky")) {
            MenuItem itemgraph = navigationView.getMenu().findItem(R.id.navigation_graph);
            itemgraph.setEnabled(false);
        }
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

}
