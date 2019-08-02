package com.example.edgepoint.checqr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AttendeesActivity extends BaseActivity {

    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;
    TabLayout.Tab firstTab, secondTab;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    ProgressDialog progressDialog;

    @Override
    int getContentViewId() {
        return R.layout.activity_attendees;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_attendees;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);

        DatabaseAccess databaseSubActivity = DatabaseAccess.getInstance(AttendeesActivity.this);
        databaseSubActivity.open();
        List<String> SubActivity = databaseSubActivity.getSubActivity();
        databaseSubActivity.close();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        firstTab = tabLayout.newTab();
        firstTab.setText(SubActivity.get(0));
        tabLayout.addTab(firstTab);

        secondTab = tabLayout.newTab();
        secondTab.setText("Summary List"); // set the Text for the second Tab
        tabLayout.addTab(secondTab); // add  the tab  in the TabLayout
        // perform setOnTabSelectedListener event on
        fragment = new AttendeesFragment_1();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.simpleFrameLayout, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        progressDialog.show();
                        fragment = new AttendeesFragment_1();
                        break;

                    case 1:
                        progressDialog.show();
                        fragment = new AttendeesFragment_2();
                        break;
                }
                progressDialog.dismiss();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.simpleFrameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}
