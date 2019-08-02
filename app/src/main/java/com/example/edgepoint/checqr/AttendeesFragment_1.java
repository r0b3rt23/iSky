package com.example.edgepoint.checqr;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AttendeesFragment_1 extends Fragment {
    private ListView listView;
    private CustomAdapter customAdapter_list;
    private Context context;
    ProgressDialog progressDialog;

    private List<AttendeesModel> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CustomAdapter mAdapter;

    public AttendeesFragment_1() {
// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_attendees_fragment_1, container, false);

        DatabaseAccess databaseInformation = DatabaseAccess.getInstance(getActivity());
        databaseInformation.open();
        List<String> SubActivity = databaseInformation.getSubActivity();
        final ArrayList<AttendeesModel> InfoList = databaseInformation.Attendees("VotersName",SubActivity.get(0));
        databaseInformation.close();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.line_divider));

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_names);

        mAdapter = new CustomAdapter(InfoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AttendeesModel attendees = InfoList.get(position);
                String namelist = attendees.getName().toString();
                Intent intent = new Intent(getActivity(), Information.class);
                intent.putExtra("Name",namelist);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }
}
