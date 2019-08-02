package com.example.edgepoint.checqr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class AttendeesFragment_2 extends Fragment {

    Spinner barangay_spinner;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView sectionHeader;
    private SectionedRecyclerViewAdapter sectionAdapter;

    public AttendeesFragment_2() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_attendees_fragment_2, container, false);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.line_divider));

        sectionHeader = (RecyclerView) rootView.findViewById(R.id.add_header);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        sectionHeader.setLayoutManager(linearLayoutManager);
        sectionHeader.setHasFixedSize(true);
        sectionHeader.addItemDecoration(dividerItemDecoration);

        barangay_spinner = (Spinner) rootView.findViewById(R.id.bgy_list_spinner);
        DatabaseAccess databaseAccessBarangay = DatabaseAccess.getInstance(getActivity());
        databaseAccessBarangay.open();
        List<String> barangays = databaseAccessBarangay.getQRDatabase("Barangay");
        databaseAccessBarangay.close();

        ArrayAdapter<String> adapterBarangay = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, barangays);
        barangay_spinner.setAdapter(adapterBarangay);
        barangay_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String Bgyspin = barangay_spinner.getSelectedItem().toString();

                DatabaseAccess databaseAccessPrecinct = DatabaseAccess.getInstance(getActivity());
                databaseAccessPrecinct.open();

                List<String> precinct = databaseAccessPrecinct.getPrecinct_List(Bgyspin);
                sectionAdapter = new SectionedRecyclerViewAdapter();
                for (int i = 0; i < precinct.size(); i++) {

                    List<String> votersList = databaseAccessPrecinct.getName_List(precinct.get(i));
                    HeaderRecyclerViewSection SectionHeader = new HeaderRecyclerViewSection("Precinct: "+precinct.get(i), getDataSource(votersList));
                    sectionAdapter.addSection(SectionHeader);

                }
                sectionHeader.setAdapter(sectionAdapter);
                databaseAccessPrecinct.close();


            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

// Inflate the layout for this fragment
        return rootView;
    }

    private List<ItemObject> getDataSource(List<String> votersname){
        List<ItemObject> data = new ArrayList<ItemObject>();
        for (int x = 0; x < votersname.size(); x++){
            data.add(new ItemObject(votersname.get(x)));
        }
        return data;
    }

}
